package com.alfredvc;

import java.util.LinkedHashSet;

/**
 * Created by erpa_ on 9/5/2015.
 */
public class Constraint {

    private final LinkedHashSet<String> variableSet;
    private Function function;

    public Constraint(LinkedHashSet<String> variableSet, Function function) {
        this.variableSet = variableSet;
        this.function = function;
    }

    public double evaluate (Object[] args) {
        return function.evaluateToDouble(args);
    }

    public LinkedHashSet<String> getVariableSet(){
        return this.variableSet;
    }
}
