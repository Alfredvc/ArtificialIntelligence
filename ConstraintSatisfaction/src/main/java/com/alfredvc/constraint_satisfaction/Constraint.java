package com.alfredvc.constraint_satisfaction;


import com.alfredvc.Function;

import java.util.LinkedHashSet;

/**
 * Created by erpa_ on 9/5/2015.
 */
public class Constraint {

    private final LinkedHashSet<String> variableSet;
    private Function function;

    public Constraint(Function function) {
        this.variableSet = function.getVariableSet();
        this.function = function;
    }

    public boolean evaluate (Object[] args) {
        return function.evaluateToBoolean(args);
    }

    public LinkedHashSet<String> getVariableSet(){
        return this.variableSet;
    }
}
