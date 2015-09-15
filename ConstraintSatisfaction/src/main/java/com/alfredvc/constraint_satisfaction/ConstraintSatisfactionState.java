package com.alfredvc.constraint_satisfaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import search_algorithm.State;

/**
 * Created by Alfredvc on 9/7/2015.
 */
class ConstraintSatisfactionState<T> extends State<ConstraintSatisfactionState<T>> {

    private final BitSet[] bitSets;

    private ConstraintSatisfaction constraintSatisfaction;

    public ConstraintSatisfactionState(BitSet[] bitSets, ConstraintSatisfaction constraintSatisfaction) {
        this.bitSets = bitSets;
        this.constraintSatisfaction = constraintSatisfaction;
    }

    public BitSet[] getBitSets() {
        return bitSets;
    }

    /**
     * Returns the sum of the size of each domain minus one.
     */
    @Override
    public int getH() {
        int count = 0;
        for (BitSet bitSet : bitSets) {
            if (bitSet.cardinality() < 1) throw new IllegalStateException("No illegal states should be allowed");
            count += bitSet.cardinality() - 1;
        }
        return count;
    }

    @Override
    public boolean isASolution() {
        for (BitSet bitSet : bitSets) {
            if (bitSet.cardinality() != 1) return false;
        }
        return constraintSatisfaction.fulfillsAllConstrains(bitSets);
    }

    @Override
    public List<ConstraintSatisfactionState<T>> generateSuccessors() {
        List<ConstraintSatisfactionState<T>> successors = new ArrayList<>();
        int i = getFirstDomainLargerThanOne(bitSets);
        if (i >= bitSets.length) return successors;
        BitSet currentBitSet = bitSets[i];
        int lastFromIndex = -1;
        for (int a = 0; a < currentBitSet.cardinality(); a++) {
            BitSet[] successorBitSets = cloneBitSetArray(bitSets);
            lastFromIndex = currentBitSet.nextSetBit(lastFromIndex + 1);
            successorBitSets[i].clear();
            successorBitSets[i].set(lastFromIndex);
            constraintSatisfaction.filterDomain(successorBitSets);
            //Only create states for legal states.
            if (isLegalState(successorBitSets)) {
                successors.add(new ConstraintSatisfactionState<>(successorBitSets, constraintSatisfaction));
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
        int thisVarsWithSingleDomain = (int) Arrays.stream(this.bitSets).filter(v -> v.cardinality() == 1).count();
        int stateVarsWithSingleDomain = (int) Arrays.stream(state.bitSets).filter(v -> v.cardinality() == 1).count();
        return Math.abs(thisVarsWithSingleDomain - stateVarsWithSingleDomain);
    }

    @Override
    public int hashCode() {
        int hashCode = 31;
        for (BitSet b : bitSets) {
            hashCode += b.hashCode() * 31;
        }
        return hashCode;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof ConstraintSatisfactionState)) return false;
        ConstraintSatisfactionState<T> other = ((ConstraintSatisfactionState) obj);
        return Arrays.equals(this.bitSets, other.bitSets);
    }

    @Override
    public String toString() {
        return "ConstraintSatisfactionState{" +
                "variables=" + Arrays.toString(bitSets) +
                '}';
    }

    private BitSet[] cloneBitSetArray(BitSet[] bs) {
        BitSet[] toReturn = new BitSet[bs.length];
        for (int i = 0; i < bs.length; i++) {
            toReturn[i] = (BitSet) bs[i].clone();
        }
        return toReturn;
    }

    private boolean isLegalState(BitSet[] bs) {
        for (BitSet b : bs) {
            if (b.cardinality() < 1) return false;
        }
        return true;
    }

    private int getFirstDomainLargerThanOne(BitSet[] bs) {
        for (int i = 0; i < bs.length; i++) {
            if (bs[i].cardinality() != 1) {
                return i;
            }
        }
        return bitSets.length;
    }
}
