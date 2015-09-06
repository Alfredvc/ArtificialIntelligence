package com.alfredvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by erpa_ on 9/5/2015.
 */
public class Revise {
    private final String variableName;
    private final Constraint constraint;

    public Revise(String variableName, Constraint constraint) {
        this.variableName = variableName;
        this.constraint = constraint;
    }

    public String getVariableName() {
        return variableName;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public static List<Revise> forConstraint(Constraint constraint, String skipVariable) {
        List<Revise> toReturn = constraint.getVariableSet()
                .stream()
                .filter(v -> !v.equals(skipVariable))
                .map(varName -> new Revise(varName, constraint))
                .collect(Collectors.toList());
        return toReturn;
    }

    public static List<Revise> forConstraint(Constraint constraint) {
        return forConstraint(constraint, "");
    }
}
