package com.alfredvc.constraint_satisfaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import search_algorithm.State;

/**
 * Created by Alfredvc on 9/7/2015.
 */
public class ConstraintSatisfactionState<T> extends State<ConstraintSatisfactionState<T>> {

    public static final int H_FOR_ILLEGAL_STATES = Integer.MAX_VALUE - 100000000;

    private ArraySet<Variable<T>> variables;

    public ConstraintSatisfactionState(ArraySet<Variable<T>> variables) {
        this.variables = variables;
    }

    public ArraySet<Variable<T>> getVariables() {
        return variables;
    }

    public void setVariables(ArraySet<Variable<T>> variables) {
        this.variables = variables;
    }

    /**
     * Returns the amount of assumptions needed to reach a state in which all variables have a
     * domain of a single value if not variables have an empty domain. If any variable has an empty
     * domain then H_FOR_ILLEGAL_STATES is returned;
     */
    @Override
    public int getH() {
        int count = 0;
        for (Variable<T> var : variables) {
            if (var.getDomain().isEmpty()) return H_FOR_ILLEGAL_STATES;
            if (var.getDomain().size() > 1) count++;
        }
        return count;
    }

    @Override
    public boolean isASolution() {
        return variables.stream().allMatch(v -> v.getDomain().size() == 1);
    }

    @Override
    public List<ConstraintSatisfactionState<T>> generateSuccessors() {
        List<ConstraintSatisfactionState<T>> successors = new ArrayList<>();
        List<T> singleElementList = new ArrayList<>(1);
        for (Variable<T> var : variables) {
            for (T val : var.getDomain()) {
                ArraySet<Variable<T>> vars = new ArraySet<>();
                for (Variable<T> internalVar : variables) {
                    if (!internalVar.equals(var)) vars.add(new Variable<T>(internalVar.getName(), internalVar.getDomain()));
                }
                singleElementList.add(val);
                //We reuse the same singleElementList because the constructor of Variable creates
                //a new array
                vars.add(new Variable<>(var.getName(), singleElementList));
                singleElementList.clear();
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
