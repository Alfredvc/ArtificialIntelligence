package com.alfredvc.constraint_satisfaction;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import a_star.AStar;
import search_algorithm.SearchAlgorithm;
import search_algorithm.SearchAlgorithmResult;

/**
 * General constraint satisfaction solving algorithm
 */
public class ConstraintSatisfaction<T> {

    private final List<Constraint> constraints;
    private final List<CurrentVariableDomainChangeListener<T>> listeners;
    private final List<Variable<T>> vars;

    public ConstraintSatisfaction(List<Constraint> constraints, List<Variable<T>> vars) {
        this.constraints = constraints;
        this.listeners = new ArrayList<>();
        this.vars = vars;
    }

    private Map<String, Variable<T>> getVariableMap(List<Variable<T>> variables) {
        Map<String, Variable<T>> map = new HashMap<>();
        for (Variable<T> var : variables) {
            map.put(var.getName(), var);
        }
        return map;
    }

    public ConstraintSatisfactionResult<T> solve(){
        Map<String, Variable<T>> variables = Collections.unmodifiableMap(getVariableMap(vars));
        ArraySet<Variable<T>> variableList = new ArraySet<>(vars);
        filterDomain(variables);
        ConstraintSatisfactionState<T> state = new ConstraintSatisfactionState<>(new ArraySet<>(variableList));
        if (state.isASolution()) return new ConstraintSatisfactionResult<>(variables);
        SearchAlgorithm<ConstraintSatisfactionState<T>> searchAlgorithm = new AStar(state, Integer.MAX_VALUE);
        //Filter variable domains before pushing into agenda
        searchAlgorithm.addNodePrePushListener(n -> {
            filterDomain(getVariableMap(n.getState().getVariables()));
            n.getF();
        });
        searchAlgorithm.addNodePopListener( n -> fireCurrentVariableDomainChanged(n.getState().getVariables()));
        SearchAlgorithmResult<ConstraintSatisfactionState<T>> result = searchAlgorithm.search();
        return new ConstraintSatisfactionResult<>(getVariableMap(result.getFinalNode().getState().getVariables()));
    }

    private ConstraintSatisfaction<T> filterDomain(Map<String, Variable<T>> variables) {
        Queue<Revise> reviseQueue  = new LinkedList<>();
        //TODO: Optimize by picking the constraint with the fewest variables, maybe also the variables with smallest domain??
        for (Constraint constraint : this.constraints) {
            reviseQueue.addAll(Revise.forConstraint(constraint));
        }
        Revise currentRevise;
        while (!reviseQueue.isEmpty()) {
            currentRevise = reviseQueue.poll();
            performRevise(currentRevise, variables, reviseQueue);
        }
        return this;
    }

    private void performRevise(Revise revise, Map<String, Variable<T>> variables, Queue<Revise> reviseQueue) {
        Variable<T> variable = getVariable(variables, revise.getVariableName());
        Constraint constraint = revise.getConstraint();
        boolean reducedDomain = false;
        for (Iterator<T> iterator = variable.getDomain().iterator(); iterator.hasNext(); ) {
            T domainElement = iterator.next();
            if (!evaluateAllCombinations(constraint, variable.getName(), domainElement, variables)) {
                iterator.remove();
                reducedDomain = true;
            }
        }
        if (reducedDomain) {
            addAllToQueue(reviseQueue, Revise.forConstraint(constraint, revise.getVariableName()));
        }
    }

    private Variable<T> getVariable(Map<String, Variable<T>> variables, String variableName) {
        if (!variables.containsKey(variableName)) {
            System.out.println(variableName);
            System.out.println(variables);
            throw new IllegalArgumentException("Variable " + variableName + " does not exist.");
        }
        return variables.get(variableName);
    }

    private void addAllToQueue(Queue<Revise> reviseQueue, List<Revise> revises) {
        revises.stream().peek(r -> reviseQueue.add(r));
    }

    /**
     * Evaluates all combinations of all variables in the given constraint, except for the
     * currentVariable whose value is kept at currentValue.
     *
     * @return the result of all the evaluations ored together.
     */
    private boolean evaluateAllCombinations(Constraint constraint, String currentVariable, T currentValue, Map<String, Variable<T>> variables) {
        int variableCount = constraint.getVariableArraySet().size();
        if (variableCount == 2) {
            return evaluateAllCombinationsDouble(constraint, currentVariable, currentValue, variables);
        }
        int combinationCount = constraint.getVariableArraySet().stream()
                .filter(n -> !currentVariable.equals(n))
                .map(v -> variables.get(v).getDomain().size())
                .reduce(1, (a, b) -> a * b);
        int[] alternateEvery = new int[variableCount];
        int[] domainSize = new int[variableCount];
        List<String> vars = constraint.getVariableArraySet();
        int indexOfCurrentVariable = -1;
        for (int i = 0; i < variableCount; i++) {
            if (vars.get(i).equals(currentVariable)) {
                domainSize[i] = 1;
                indexOfCurrentVariable = i;
            } else {
                domainSize[i] = variables.get(vars.get(i)).getDomain().size();
            }
        }

        //We alternate the first argument on every iteration
        alternateEvery[0] = 1;
        for (int i = 1; i < variableCount; i++) {
            alternateEvery[i] = alternateEvery[i - 1] * domainSize[i - 1];
        }

        if (indexOfCurrentVariable == -1) {
            throw new IllegalStateException("Current variable " + currentVariable + " not found as argument of constraint " + constraint);
        }
        Iterator[] iterators = new Iterator[variableCount];
        for (int i = 0; i < variableCount; i++) {
            String varName = constraint.getVariableArraySet().get(i);
            if (varName.equals(currentVariable)) continue;
            iterators[i] = Iterables.cycle(variables.get(varName).getDomain()).iterator();
        }
        Object[] args = new Object[variableCount];
        boolean toReturn = false;
        for (int n = 0; n < combinationCount; n++) {
            for (int index = 0; index < variableCount; index++) {
                if (index == indexOfCurrentVariable) {
                    args[index] = currentValue;
                    continue;
                }
                if (n % alternateEvery[index] == 0) {
                    args[index] = iterators[index].next();
                }
            }
            boolean eval = constraint.evaluate(args);
            toReturn = toReturn || eval;
        }

        return toReturn;
    }

    private boolean evaluateAllCombinationsDouble(Constraint constraint, String currentVariable, T currentValue, Map<String, Variable<T>> variables) {
        Variable<T> otherVariable;
        int currentVariableIndex;
        int otherVariableIndex;
        if (constraint.getVariableArraySet().get(0).equals(currentVariable)) {
            currentVariableIndex = 0;
            otherVariableIndex = 1;
            otherVariable = variables.get(constraint.getVariableArraySet().get(1));
        } else {
            currentVariableIndex = 1;
            otherVariableIndex = 0;
            otherVariable = variables.get(constraint.getVariableArraySet().get(0));
        }
        boolean toReturn = false;
        Object[] args = new Object[2];
        args[currentVariableIndex] = currentValue;
        for (T val : otherVariable.getDomain()) {
            args[otherVariableIndex] = val;
            toReturn = toReturn || constraint.evaluate(args);
        }
        return toReturn;
    }

    public void addCurrentVariableDomainChangeListener(CurrentVariableDomainChangeListener<T> listener) {
        this.listeners.add(listener);
    }

    public void removeCurrentVariableDomainChangeListener(CurrentVariableDomainChangeListener<T> listener) {
        this.listeners.remove(listener);
    }

    public void fireCurrentVariableDomainChanged(List<Variable<T>> variables) {
        for (CurrentVariableDomainChangeListener<T> listener : listeners) {
            listener.currentSolutionChanged(variables);
        }
    }

    public interface CurrentVariableDomainChangeListener<T>{
        void currentSolutionChanged(List<Variable<T>> variables);
    }

}
