package com.alfredvc.constraint_satisfaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import search_algorithm.State;

/**
 * Created by Alfredvc on 9/7/2015.
 */
public class ConstraintSatisfactionState<T> extends State<ConstraintSatisfactionState<T>> {

    public static final int H_FOR_ILLEGAL_STATES = Integer.MAX_VALUE - 100000000;

    private Map<String, BitSet> varMap;

    public ConstraintSatisfactionState(Map<String,Variable<T>> vars) {
        this.varMap = new HashMap<>();
        for (Map.Entry<String, Variable<T>> e : vars.entrySet()) {
            varMap.put(e.getKey(), e.getValue().getDomain().getBitSet());
        }
    }

    /**
     * Returns the amount of assumptions needed to reach a state in which all variables have a
     * domain of a single value if not variables have an empty domain. If any variable has an empty
     * domain then H_FOR_ILLEGAL_STATES is returned;
     */
    @Override
    public int getH() {
        int count = 0;
        for (BitSet bitSet : varMap.values()) {
            if (bitSet.cardinality() == 0) return H_FOR_ILLEGAL_STATES;
            if (bitSet.cardinality() > 1) count++;
        }
        return count;
    }

    @Override
    public boolean isASolution() {
        return varMap.values().stream().allMatch(v -> v.cardinality() == 1);
    }

    @Override
    public List<ConstraintSatisfactionState<T>> generateSuccessors() {
        List<ConstraintSatisfactionState<T>> successors = new ArrayList<>();
        for (Variable<T> var : variables) {
            for (int i = 0; i < var.getDomain().size(); i++) {
                ArraySet<Variable<T>> vars = new ArraySet<>();
                for (int a = 0; a < variables.size(); a++) {
                    if (a == i) continue;
                    vars.add(new Variable<>(variables.get(a)));
                }
                Variable<T> toAdd = new Variable<>(var);
                toAdd.getDomain().removeAllExept(i);
                vars.add(toAdd);
                successors.add(new ConstraintSatisfactionState<>(vars));
            }
        }
        return successors;
    }

    @Override
    public int getArcCost() {
        return 1;
    }

    /**
     * Returns the amount of assumptions needed to get from state to this. It is reflective.
     */
    @Override
    public int getCostFrom(ConstraintSatisfactionState<T> state) {
        int thisVarsWithSingleDomain = (int) variables.stream().filter(v -> v.getDomain().size() == 1).count();
        int stateVarsWithSingleDomain = (int) state.variables.stream().filter(v -> v.getDomain().size() == 1).count();
        return Math.abs(thisVarsWithSingleDomain - stateVarsWithSingleDomain);
    }

    @Override
    public int hashCode() {
        return variables.hashCode();
    }

    /**
     * Assumes the variables arrays are in the same order, this should be the case as this class
     * uses an ArraySet to store the variables.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof ConstraintSatisfactionState)) return false;
        ConstraintSatisfactionState<T> other = ((ConstraintSatisfactionState) obj);
        if (this.variables.size() != other.variables.size()) return false;
        return Arrays.equals(this.variables.toArray(), other.variables.toArray());
    }

    @Override
    public String toString() {
        return "ConstraintSatisfactionState{" +
                "variables=" + Arrays.toString(variables.toArray()) +
                '}';
    }
}
