package com.alfredvc.constraint_satisfaction;

import java.util.List;

import search_algorithm.State;

/**
 * Created by erpa_ on 9/7/2015.
 */
public class ConstraintSatisfactionState<T> extends State {

    private List<Variable<T>> variables;

    @Override
    public int getH() {
        return 0;
    }

    @Override
    public boolean isASolution() {
        return variables.stream().allMatch(v -> v.getDomain().size() == 1);
    }

    @Override
    public List<State> generateSuccessors() {
        return null;
    }

    @Override
    public int getArcCost() {
        return 0;
    }

    @Override
    public int getCostFrom(State state) {
        return 0;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }
}
