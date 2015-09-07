package com.alfredvc.constraint_satisfaction;


import com.alfredvc.ParsedFunction;

import java.util.List;

/**
 * Created by Alfredvc on 9/5/2015.
 */
public class Constraint {

    private final List<String> variableSet;
    private ParsedFunction parsedFunction;

    public Constraint(ParsedFunction parsedFunction) {
        this.variableSet = new ArraySet<>(parsedFunction.getVariableSet());
        this.parsedFunction = parsedFunction;
    }

    public boolean evaluate(Object[] args) {
        return parsedFunction.evaluateToBoolean(args);
    }

    public List<String> getVariableArraySet() {
        return this.variableSet;
    }
}
