package com.alfredvc.constraint_satisfaction;


/**
 * Used to queue revise requests.
 */
class Revise {
    /**
     * The global index of the variable to be revised
     */
    private final int varIndex;

    private final Constraint constraint;

    public Revise(int varIndex, Constraint constraint) {
        this.varIndex = varIndex;
        this.constraint = constraint;
    }

    public int getVarIndex() {
        return varIndex;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Revise revise = (Revise) o;

        if (varIndex != revise.varIndex) return false;
        return constraint == revise.constraint;

    }

    @Override
    public int hashCode() {
        int result = varIndex;
        result = 31 * result + (constraint != null ? constraint.hashCode() : 0);
        return result;
    }
}
