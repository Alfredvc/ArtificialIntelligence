package com.alfredvc.constraint_satisfaction;

import java.util.Arrays;
import java.util.List;

/**
 * Created by erpa_ on 9/6/2015.
 */
public class ConstraintSatisfactionResult<T> {
    private final List<Variable<T>> variables;

    public ConstraintSatisfactionResult(List<Variable<T>> variables) {
        this.variables = variables;
    }

    public List<Variable<T>> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "ConstraintSatisfactionResult{" +
                "variables=" + Arrays.toString(variables.toArray()) +
                '}';
    }
}
