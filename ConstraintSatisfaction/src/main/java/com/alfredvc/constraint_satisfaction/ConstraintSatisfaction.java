package com.alfredvc.constraint_satisfaction;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import a_star.AStar;
import bfs.BFS;
import dfs.DFS;
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

    public ConstraintSatisfaction(List<Constraint> constraints, List<Variable<T>> vars) {
        this.constraints = constraints;
        this.listeners = new ArrayList<>();
        this.vars = vars;
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < vars.size(); i++) {
            map.put(vars.get(i).getName(), i);
        }
        varNameToIndex = new HashMap<>(map);
    }

    public ConstraintSatisfactionResult<T> solve(){
        BitSet[] domains = new BitSet[vars.size()];
        for (int i = 0; i < domains.length; i++) {
            domains[i] = vars.get(i).getDomain().getBitSet();
        }
        filterDomain(domains);
        ConstraintSatisfactionState<T> state = new ConstraintSatisfactionState<>(domains, this);
        if (state.isASolution()) return new ConstraintSatisfactionResult<>(vars, state.getBitSets());
        SearchAlgorithm<ConstraintSatisfactionState<T>> searchAlgorithm = new AStar(state, Integer.MAX_VALUE);

        searchAlgorithm.addNodePopListener( n -> fireCurrentVariableDomainChanged(n.getState().getBitSets()));
        SearchAlgorithmResult<ConstraintSatisfactionState<T>> result = searchAlgorithm.search();
        return new ConstraintSatisfactionResult<>(vars, result.getFinalNode().getState().getBitSets());
    }

    public void filterDomain(BitSet[] domains) {
        Queue<Revise> reviseQueue  = new LinkedList<>();
        Set<Revise> reviseSet = new HashSet<>();
        //TODO: Optimize by picking the constraint with the fewest variables, maybe also the variables with smallest domain??
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
        for (Iterator<T> iterator = variable.getDomain().iterator(domains[revise.getVarIndex()]); iterator.hasNext(); ) {
            T domainElement = iterator.next();
            if (!evaluateAllCombinations(constraint, revise.getVarIndex(), domainElement, domains)) {
                iterator.remove();
                reducedDomain = true;
            }
        }
        if (reducedDomain) {
            addAllToQueue(reviseQueue, reviseSet, getRevisesForConstraint(constraint, vars.get(revise.getVarIndex()).getName()));
        }
    }

    private void addAllToQueue(Queue<Revise> reviseQueue, Set<Revise> reviseSet, List<Revise> revises) {
        revises.stream().filter(r -> !reviseSet.contains(r)).forEach(r -> reviseQueue.add(r));
    }

    /**
     * Evaluates all combinations of all variables in the given constraint, except for the
     * currentVariable whose value is kept at currentValue.
     *
     * @return the result of all the evaluations ored together.
     */
    private boolean evaluateAllCombinations(Constraint constraint, int currentVariable, T currentValue, BitSet[] domains) {
        int variableCount = constraint.getVariableArraySet().size();
        if (variableCount == 2) {
            return evaluateAllCombinationsDouble(constraint, currentVariable, currentValue,domains);
        }
        int combinationCount = 1;
        for (int i = 0; i < domains.length; i++) {
            if (i == currentVariable) continue;
            combinationCount *= domains[i].cardinality();
        }
        int[] alternateEvery = new int[variableCount];
        int[] domainSize = new int[variableCount];
        for (int i = 0; i < variableCount; i++) {
            if (i == currentVariable) {
                domainSize[i] = 1;
                continue;
            }
            domainSize[i] = domains[i].cardinality();
        }

        //We alternate the first argument on every iteration
        alternateEvery[0] = 1;
        for (int i = 1; i < variableCount; i++) {
            alternateEvery[i] = alternateEvery[i - 1] * domainSize[i - 1];
        }

        Iterator[] iterators = new Iterator[variableCount];
        for (int i = 0; i < variableCount; i++) {
            if (i == currentVariable) continue;
            iterators[i] = vars.get(i).getDomain().cycleIterator(domains[i]);
        }
        Object[] args = new Object[variableCount];
        args[currentVariable] = currentValue;
        for (int n = 0; n < combinationCount; n++) {
            for (int index = 0; index < variableCount; index++) {
                if (index == currentVariable) continue;

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

    private boolean evaluateAllCombinationsDouble(Constraint constraint, int currentVariable, T currentValue, BitSet[] domains) {
        Variable<T> otherVariable;
        int globalIndex0 = varNameToIndex.get(constraint.getVariableArraySet().get(0));
        int globalIndex1 = varNameToIndex.get(constraint.getVariableArraySet().get(1));
        int globalIndexOther;
        Object[] args = new Object[2];
        int localIndexOther;
        if (globalIndex0 == currentVariable) {
            args[0] = currentValue;
            localIndexOther = 1;
            globalIndexOther = globalIndex1;
            otherVariable = vars.get(varNameToIndex.get(constraint.getVariableArraySet().get(1)));
        } else {
            args[1] = currentValue;
            localIndexOther = 0;
            otherVariable = vars.get(globalIndex0);
            globalIndexOther = globalIndex0;
        }

        for (Iterator<T> iterator = otherVariable.getDomain().iterator(domains[globalIndexOther]); iterator.hasNext(); ) {
            T val = iterator.next();
            args[localIndexOther] = val;
            boolean eval = constraint.evaluate(args);
            if (eval) return true;
        }
        return false;
    }

    public boolean fulfillsAllConstrains(BitSet[] domains) {
        for (Constraint constraint: constraints) {
            List<String> variables = constraint.getVariableArraySet();
            Object[] args = new Object[variables.size()];
            for(int i = 0; i < constraint.getVariableArraySet().size(); i++) {
                int varIndex = varNameToIndex.get(variables.get(i));
                args[i] = vars.get(varIndex).getDomain().getFirst(domains[varIndex]);
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

    public void fireCurrentVariableDomainChanged(BitSet[] variables) {
        for (CurrentVariableDomainChangeListener<T> listener : listeners) {
            listener.currentSolutionChanged(variables);
        }
    }

    public interface CurrentVariableDomainChangeListener<T>{
        void currentSolutionChanged(BitSet[] bitSets);
    }

    private List<Revise> getRevisesForConstraint(Constraint c, String toSkip) {
        List<Revise> toReturn = new ArrayList<>();
        for (String varName : c.getVariableArraySet()) {
            if (varName.equals(toSkip)) continue;
            toReturn.add(new Revise(varNameToIndex.get(varName), c));
        }
        return toReturn;
    }

}
