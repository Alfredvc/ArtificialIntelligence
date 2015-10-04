package com.alfredvc.constraint_satisfaction;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import search_algorithm.AStar;
import search_algorithm.SearchAlgorithm;
import search_algorithm.SearchAlgorithmResult;

/**
 * General constraint satisfaction solving algorithm
 */
public class ConstraintSatisfaction<T> {

    private final List<Constraint> constraints;
    private final List<CurrentVariableDomainChangeListener<T>> listeners;
    private final List<Variable<T>> vars;
    private final Map<String, Integer> varNameToIndex;
    private final Comparator<Constraint> constraintComparator;

    public ConstraintSatisfaction(List<Constraint> constraints, List<Variable<T>> vars, Comparator<Constraint> constraintComparator) {
        this.constraints = constraints;
        this.listeners = new ArrayList<>();
        this.vars = vars;
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < vars.size(); i++) {
            map.put(vars.get(i).getName(), i);
        }
        varNameToIndex = new HashMap<>(map);
        this.constraintComparator = constraintComparator;
    }

    /**
     * Creates a new ConstraintSatisfaction instance
     * @param constraints the list of constraints of the problem
     * @param vars the list of variables of the problem
     */
    public ConstraintSatisfaction(List<Constraint> constraints, List<Variable<T>> vars) {
        this(constraints, vars, null);
    }

    /**
     * Attempts to solve the problem with the given constraints and variables using a combination
     * of a general arc consistency algorithm and a search algorithm.
     * @return the result of the algorithm.
     */
    public ConstraintSatisfactionResult<T> solve(){
        BitSet[] domains = new BitSet[vars.size()];
        for (int i = 0; i < domains.length; i++) {
            domains[i] = vars.get(i).packageGetDomain().getBitSet();
        }
        filterDomain(domains);
        ConstraintSatisfactionState<T> state = new ConstraintSatisfactionState<>(domains, this);
        SearchAlgorithm<ConstraintSatisfactionState<T>> searchAlgorithm = new AStar(state, Integer.MAX_VALUE);
        searchAlgorithm.addNodePopListener( n -> fireCurrentVariableDomainChanged(n.getState().getBitSets()));
        SearchAlgorithmResult<ConstraintSatisfactionState<T>> result = searchAlgorithm.search();
        BitSet[] resultBitSets = result.getFinalNode().getState().getBitSets();
        return new ConstraintSatisfactionResult<>(vars, resultBitSets, result, getViolatedConstraintCount(resultBitSets));
    }


    /**
     * Filters the domain of all current variables, with the given domains.
     * @param domains variable containing the domain of each variable, represented as a bit set.
     */
    void filterDomain(BitSet[] domains) {
        Queue<Revise> reviseQueue;
        if (constraintComparator != null) {
            reviseQueue = new PriorityQueue<>(constraints.size(), getReviseComparator(constraintComparator));
        } else {
            reviseQueue  = new LinkedList<>();
        }
        Set<Revise> reviseSet = new HashSet<>();
        for (Constraint constraint : this.constraints) {
            reviseQueue.addAll(getRevisesForConstraint(constraint, ""));
        }
        Revise currentRevise;
        while (!reviseQueue.isEmpty()) {
            currentRevise = reviseQueue.poll();
            performRevise(currentRevise, domains, reviseQueue, reviseSet);
        }
    }

    private void performRevise(Revise revise, BitSet[] domains, Queue<Revise> reviseQueue, Set<Revise> reviseSet) {
        Variable<T> variable = vars.get(revise.getVarIndex());
        Constraint constraint = revise.getConstraint();
        boolean reducedDomain = false;
        for (Iterator<T> iterator = variable.packageGetDomain().iterator(domains[revise.getVarIndex()]); iterator.hasNext(); ) {
            T domainElement = iterator.next();
            if (!evaluateAllCombinations(constraint, variable, revise.getVarIndex(), domainElement, domains)) {
                iterator.remove();
                reducedDomain = true;
            }
        }
        if (reducedDomain) {
            addAllToQueue(reviseQueue, reviseSet, getRevisesForConstraint(constraint, vars.get(revise.getVarIndex()).getName()));
        }
    }

    private void addAllToQueue(Queue<Revise> reviseQueue, Set<Revise> reviseSet, List<Revise> revises) {
        revises.stream().filter(r -> !reviseSet.contains(r)).forEach(reviseQueue::add);
    }

    /**
     * Evaluates all combinations of all variables for the given constraint, except for the
     * currentVarGlobalIndex whose value is kept at currentValue.
     *
     * @return the result of all the evaluations ored together.
     */
    private boolean evaluateAllCombinations(Constraint constraint, Variable<T> currentVar, int currentVarGlobalIndex, T currentValue, BitSet[] domains) {
        int variableCount = constraint.getVariableNames().size();
        if (variableCount == 2) {
            return evaluateAllCombinationsDouble(constraint, currentVarGlobalIndex, currentValue,domains);
        }
        int combinationCount = 1;
        int[] alternateEvery = new int[variableCount];
        int[] domainSize = new int[variableCount];
        int currentVariableConstraintIndex = -1;

        Iterator[] iterators = new Iterator[variableCount];
        for (int i = 0; i < variableCount; i++) {
            String name = constraint.getVariableNames().get(i);
            int globalIndex = varNameToIndex.get(name);
            combinationCount *= domains[globalIndex].cardinality();
            if (name.equals(currentVar.getName())){
                domainSize[i] = 1;
                currentVariableConstraintIndex = i;
                continue;
            }
            iterators[i] = vars.get(globalIndex).packageGetDomain().cycleIterator(domains[globalIndex]);
            domainSize[i] = domains[globalIndex].cardinality();
        }

        //We alternate the first argument on every iteration
        alternateEvery[0] = 1;
        for (int i = 1; i < variableCount; i++) {
            alternateEvery[i] = alternateEvery[i - 1] * domainSize[i - 1];
        }

        Object[] args = new Object[variableCount];
        args[currentVariableConstraintIndex] = currentValue;
        for (int n = 0; n < combinationCount; n++) {
            for (int index = 0; index < variableCount; index++) {
                if (index == currentVariableConstraintIndex) continue;

                if (n % alternateEvery[index] == 0) {
                    args[index] = iterators[index].next();
                }
            }
            boolean eval = constraint.evaluate(args);
            //If any is true then we can short circuit.
            if (eval) return true;
        }
        return false;
    }

    /*
        Since many CSP have constraints with only two variables an optimized evaluateAllCombinations
        method for constraints with only two variables was created. Tested to be around 33% faster
        than the general evaluateAllCombinations.
     */
    private boolean evaluateAllCombinationsDouble(Constraint constraint, int currentVariable, T currentValue, BitSet[] domains) {
        Variable<T> otherVariable;
        int globalIndex0 = varNameToIndex.get(constraint.getVariableNames().get(0));
        int globalIndex1 = varNameToIndex.get(constraint.getVariableNames().get(1));
        int globalIndexOther;
        Object[] args = new Object[2];
        int localIndexOther;
        if (globalIndex0 == currentVariable) {
            args[0] = currentValue;
            localIndexOther = 1;
            globalIndexOther = globalIndex1;
            otherVariable = vars.get(varNameToIndex.get(constraint.getVariableNames().get(1)));
        } else {
            args[1] = currentValue;
            localIndexOther = 0;
            otherVariable = vars.get(globalIndex0);
            globalIndexOther = globalIndex0;
        }

        for (Iterator<T> iterator = otherVariable.packageGetDomain().iterator(domains[globalIndexOther]); iterator.hasNext(); ) {
            T val = iterator.next();
            args[localIndexOther] = val;
            boolean eval = constraint.evaluate(args);
            if (eval) return true;
        }
        return false;
    }

    boolean fulfillsAllConstrains(BitSet[] domains) {
        for (Constraint constraint: constraints) {
            List<String> variables = constraint.getVariableNames();
            Object[] args = new Object[variables.size()];
            for(int i = 0; i < constraint.getVariableNames().size(); i++) {
                int varIndex = varNameToIndex.get(variables.get(i));
                args[i] = vars.get(varIndex).packageGetDomain().getFirst(domains[varIndex]);
            }
            boolean eval = constraint.evaluate(args);
            if (!eval) return false;
        }
        return true;
    }

    public void addCurrentVariableDomainChangeListener(CurrentVariableDomainChangeListener<T> listener) {
        this.listeners.add(listener);
    }

    public void removeCurrentVariableDomainChangeListener(CurrentVariableDomainChangeListener<T> listener) {
        this.listeners.remove(listener);
    }

    private void fireCurrentVariableDomainChanged(BitSet[] variables) {
        for (CurrentVariableDomainChangeListener<T> listener : listeners) {
            listener.currentSolutionChanged(variables);
        }
    }

    public interface CurrentVariableDomainChangeListener<T>{
        void currentSolutionChanged(BitSet[] bitSets);
    }

    private List<Revise> getRevisesForConstraint(Constraint c, String toSkip) {
        List<Revise> toReturn = new ArrayList<>();
        for (String varName : c.getVariableNames()) {
            if (varName.equals(toSkip)) continue;
            toReturn.add(new Revise(varNameToIndex.get(varName), c));
        }
        return toReturn;
    }

    private int getViolatedConstraintCount(BitSet[] domains) {
        int violatedConstraints = 0;
        for (Constraint constraint : constraints) {
            boolean satisfied = false;
            List<String> varNames = constraint.getVariableNames();
            int varGlobalIndex = varNameToIndex.get(varNames.get(0));
            Variable<T> var = vars.get(varGlobalIndex);
            for (Iterator<T> iterator = var.packageGetDomain().iterator(domains[varGlobalIndex]); iterator.hasNext(); ) {
                T val = iterator.next();
                satisfied = satisfied || evaluateAllCombinations(constraint, var, varGlobalIndex, val, domains);
            }
            if (!satisfied) violatedConstraints++;
        }
        return violatedConstraints;
    }

    private Comparator<Revise> getReviseComparator(Comparator<Constraint> constraintComparator) {
        return (o1, o2) -> constraintComparator.compare(o1.getConstraint(), o2.getConstraint());
    }

}
