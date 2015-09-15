package com.alfredvc.constraint_satisfaction;


import com.alfredvc.ParsedFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the list of variables in the constraint, and a function representing the constraint.
 */
public class Constraint {

    private final List<String> variableNames;
    private ParsedFunction parsedFunction;

    public Constraint(ParsedFunction parsedFunction) {
        this.variableNames = new ArrayList<>(parsedFunction.getVariableSet());
        this.parsedFunction = parsedFunction;
    }

    public Constraint(ParsedFunction parsedFunction, List<String> variableNames) {
        this.parsedFunction = parsedFunction;
        this.variableNames = new ArrayList<>(variableNames);
    }

    public boolean evaluate(Object[] args) {
        return parsedFunction.evaluateToBoolean(args);
    }

    public List<String> getVariableNames() {
        return this.variableNames;
    }
}
