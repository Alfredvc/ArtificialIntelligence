package com.alfredvc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Hello world!
 *
 */
public class ConstraintSatisfaction<T> {

    private final Map<String, Variable<T>> variables;
    private final List<Constraint> constraints;
    private final Queue<Revise> reviseQueue;

    public ConstraintSatisfaction(List<Constraint> constraints, Map<String, Variable<T>> variables) {
        this.constraints = constraints;
        this.variables = variables;
        this.reviseQueue = new LinkedList<>();
        for (Constraint constraint : this.constraints) {
            this.reviseQueue.addAll(Revise.forConstraint(constraint));
        }
    }

    public void solve(){
        Revise currentRevise;
        while (!reviseQueue.isEmpty()) {
            currentRevise = reviseQueue.poll();

        }
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
            if (constraint.evaluate(args) <= 0) {
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
            throw new IllegalArgumentException("Variable " + variableName + " does not exist.");
        }
        return variables.get(variableName);
    }

    private void addAllToQueue(Queue<Revise> reviseQueue, List<Revise> revises) {
        revises.stream().peek(r -> reviseQueue.add(r));
    }
}
