package com.alfredvc.constraint_satisfaction;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Alfredvc on 9/6/2015.
 */
public class ConstraintSatisfactionResult<T> {
    private final Map<String, Variable<T>> variables;

    public ConstraintSatisfactionResult(Map<String, Variable<T>> variables) {
        this.variables = variables;
    }

    public Map<String, Variable<T>> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "ConstraintSatisfactionResult{" +
                "variables=" + Arrays.toString(variables.entrySet().toArray()) +
                '}';
    }
}
