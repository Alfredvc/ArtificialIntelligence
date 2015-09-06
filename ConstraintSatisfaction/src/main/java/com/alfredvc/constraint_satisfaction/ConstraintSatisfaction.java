package com.alfredvc.constraint_satisfaction;

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
            Object[] args = new Object[constraint.getVariableSet().size()];
            int i = 0;
            for (String varName : constraint.getVariableSet()) {
                args[i] = getVariable(varName);
                i++;
            }
            if (constraint.evaluate(args)) {
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
}
