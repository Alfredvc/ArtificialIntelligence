package com.alfredvc.constraint_satisfaction;

import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * General constraint satisfaction solving algorithm
 */
public class ConstraintSatisfaction<T> {

    private final List<Constraint> constraints;
    private final Queue<Revise> reviseQueue;
    private Map<String, Variable<T>> variables;

    public ConstraintSatisfaction(List<Constraint> constraints, List<Variable<T>> variables) {
        Map<String, Variable<T>> map = getVariableMap(variables);
        this.constraints = constraints;
        this.variables = map;
        this.reviseQueue = new LinkedList<>();
        //TODO: Optimize by picking the constraint with the fewest variables, maybe also the variables with smallest domain??
        for (Constraint constraint : this.constraints) {
            this.reviseQueue.addAll(Revise.forConstraint(constraint));
        }
    }

    private Map<String, Variable<T>> getVariableMap(List<Variable<T>> variables) {
        Map<String, Variable<T>> map = new HashMap<>();
        for (Variable<T> var : variables) {
            map.put(var.getName(), var);
        }
        return map;
    }

    public void setVariables(List<Variable<T>> newVariables) {
        if (variables.entrySet().size() != newVariables.size()) {
            throw new IllegalArgumentException("Size of given variable list does not match current variable amount");
        }
        if (reviseQueue.size() > 0) {
            throw new IllegalStateException("Can only set variables when the revise queue is empty");
        }
        variables = getVariableMap(newVariables);
    }

    public ConstraintSatisfactionResult<T> filterDomain() {
        Revise currentRevise;
        while (!reviseQueue.isEmpty()) {
            currentRevise = reviseQueue.poll();
            performRevise(currentRevise);
        }
        return new ConstraintSatisfactionResult<>(variables);
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

    /**
     * Evaluates all combinations of all variables in the given constraint, except for the
     * currentVariable whose value is kept at currentValue.
     *
     * @return the result of all the evaluations ored together.
     */
    private boolean evaluateAllCombinations(Constraint constraint, String currentVariable, T currentValue) {
        int variableCount = constraint.getVariableArraySet().size();
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

}
