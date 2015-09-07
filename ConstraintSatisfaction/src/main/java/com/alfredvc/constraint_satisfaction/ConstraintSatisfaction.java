package com.alfredvc.constraint_satisfaction;

import com.google.common.collect.Iterables;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class ConstraintSatisfaction<T> {

    private final Map<String, Variable<T>> variables;
    private final List<Constraint> constraints;
    private final Queue<Revise> reviseQueue;

    public ConstraintSatisfaction(List<Constraint> constraints, List<Variable<T>> variables) {
        Map<String, Variable<T>> map = new HashMap<>();
        for (Variable<T> var : variables) {
            map.put(var.getName(), var);
        }
        this.constraints = constraints;
        this.variables = map;
        System.out.println(this.variables);
        this.reviseQueue = new LinkedList<>();
        for (Constraint constraint : this.constraints) {
            this.reviseQueue.addAll(Revise.forConstraint(constraint));
        }
    }

    public ConstraintSatisfactionResult<T> solve(){
        Revise currentRevise;
        while (!reviseQueue.isEmpty()) {
            currentRevise = reviseQueue.poll();
            performRevise(currentRevise);
        }
        return new ConstraintSatisfactionResult<>(variables.entrySet().stream().map( s -> s.getValue()).collect(Collectors.toList()));
    }

    private void performRevise(Revise revise) {
        Variable<T> variable = getVariable(revise.getVariableName());
        Constraint constraint = revise.getConstraint();
        boolean reducedDomain = false;
        for (Iterator<T> iterator = variable.getDomain().iterator(); iterator.hasNext(); ) {
            T domainElement = iterator.next();
            if (!evaluateAllCombinations(constraint, variable.getName(), domainElement)) {
                iterator.remove();
                reducedDomain = true;
            }
        }
        if (reducedDomain) {
            addAllToQueue(reviseQueue, Revise.forConstraint(constraint, revise.getVariableName()));
        }
    }

    private Variable<T> getVariable(String variableName) {
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

    private boolean evaluateAllCombinations(Constraint constraint) {
        return evaluateAllCombinations(constraint,"", null);
    }

    /**
     * Evaluates all combinations of all variables in the given constraint, except for the currentVariable
     * whose value is kept at currentValue.
     *
     * @param constraint
     * @param currentVariable
     * @param currentValue
     * @return the result of all the evaluations ored together.
     */
    private boolean evaluateAllCombinations(Constraint constraint, String currentVariable, T currentValue) {
        boolean doCurrent = currentValue.equals("");
        Object[] args = new Object[constraint.getVariableSet().size()];
        int variableCount = constraint.getVariableSet().size();
        int combinationCount = constraint.getVariableSet().stream()
                .filter(n -> !currentVariable.equals(n))
                .map(v -> variables.get(v).getDomain().size())
                .reduce(1, (a, b) -> a * b);
        int[] combinations = new int[variableCount];
        int[] values = new int[variableCount];
        int i = 0;
        int indexOfCurrentVariable = -1;
        for(String var : constraint.getVariableSet()) {
            if (currentVariable.equals(var) && !doCurrent) {
                combinations[i] = 1;
                values[i] = 1;
                indexOfCurrentVariable = i;
                i++;
                continue;
            }
            combinations[i] = 1;
            values[i] = variables.get(var).getDomain().size();
            for(int a = 0; a < i; a++) {
                combinations[i] *= combinations[a];
            }
            i++;
        }
        if (indexOfCurrentVariable == -1) {
            throw new IllegalStateException("Current variable " + currentVariable + " not found as argument of constraint " + constraint);
        }
        boolean toReturn = false;
        Iterator[] iterators = new Iterator[variableCount];
        T[] storedValues = (T[])new Object[variableCount];
        i = 0;
        for (String varName : constraint.getVariableSet()) {
            iterators[i] = Iterables.cycle(variables.get(varName).getDomain()).iterator();
        }
        for (int n = 0; n < combinationCount; n++) {
            for (int index = 0; index < constraint.getVariableSet().size(); index++) {
                if (index == indexOfCurrentVariable && !doCurrent){
                    args[index] = currentValue;
                    continue;
                }
                //TODO: update iterator, or dont. ( n % values[index] ) / combinations[index]
                //if (shouldUpdate) doUpdate
                args[index] = storedValues[index];
            }
            boolean eval = constraint.evaluate(args);
            toReturn = toReturn || eval;
        }

        return toReturn;
    }

}
