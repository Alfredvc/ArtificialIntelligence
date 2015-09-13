package com.alfredvc.constraint_satisfaction;


import com.alfredvc.ParsedFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alfredvc on 9/5/2015.
 */
public class Constraint {

    private final List<String> arrayList;
    private ParsedFunction parsedFunction;

    public Constraint(ParsedFunction parsedFunction) {
        this.arrayList = new ArrayList<>(parsedFunction.getVariableSet());
        this.parsedFunction = parsedFunction;
    }

    public Constraint(ParsedFunction parsedFunction, List<String> arrayList) {
        this.parsedFunction = parsedFunction;
        this.arrayList = new ArrayList<>(arrayList);
    }

    public boolean evaluate(Object[] args) {
        return parsedFunction.evaluateToBoolean(args);
    }

    public List<String> getVariableArraySet() {
        return this.arrayList;
    }
}
