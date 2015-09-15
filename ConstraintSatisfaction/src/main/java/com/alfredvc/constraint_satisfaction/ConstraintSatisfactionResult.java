package com.alfredvc.constraint_satisfaction;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alfredvc on 9/6/2015.
 */
public class ConstraintSatisfactionResult<T> {
    private final Map<String, Variable<T>> variables;

    public ConstraintSatisfactionResult(List<Variable<T>> vars, BitSet[] bitSets) {
        variables = new HashMap<>();
        for (int i = 0; i < vars.size(); i++) {
            Variable<T> toAdd = new Variable<>(vars.get(i));
            toAdd.getDomain().setView(bitSets[i]);
            variables.put(vars.get(i).getName(), toAdd);
        }
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
