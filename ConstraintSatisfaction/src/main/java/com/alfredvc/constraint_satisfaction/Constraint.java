package com.alfredvc.constraint_satisfaction;


import com.alfredvc.Function;

import java.util.List;

/**
 * Created by erpa_ on 9/5/2015.
 */
public class Constraint {

    private final List<String> variableSet;
    private Function function;

    public Constraint(Function function) {
        this.variableSet = new ArraySet<>(function.getVariableSet());
        this.function = function;
    }

    public boolean evaluate (Object[] args) {
        return function.evaluateToBoolean(args);
    }

    public List<String> getVariableArraySet(){
        return this.variableSet;
    }
}
