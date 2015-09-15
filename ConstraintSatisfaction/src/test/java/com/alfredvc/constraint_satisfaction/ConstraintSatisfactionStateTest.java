package com.alfredvc.constraint_satisfaction;

import org.junit.Before;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Alfredvc on 9/7/2015.
 */
public class ConstraintSatisfactionStateTest {

    private ConstraintSatisfaction constraintSatisfaction;

    @Before
    public void setup(){
        constraintSatisfaction = Mockito.mock(ConstraintSatisfaction.class, Mockito.RETURNS_DEEP_STUBS);
    }

    @Test
    public void testGenerationOfSuccessors(){
        int expectedSuccessorCount = 4;
        Integer[] set1 = new Integer[4];
        for (int i = 1; i < 5; i++) set1[i-1] = i;
        Integer[] set2 = new Integer[6];
        for (int i = 1; i < 7; i++) set2[i-1] = i;
        Integer[] set3 = new Integer[4];
        for (int i = 4; i < 8; i++) set3[i-4] = i;
        Variable<Integer> x = new Variable<>("x", new ArrayWithView<>(set1));
        Variable<Integer> y = new Variable<>("y", new ArrayWithView<>(set2));
        Variable<Integer> z = new Variable<>("z", new ArrayWithView<>(set3));
        ArrayList<Variable<Integer>> variables = new ArrayList<>();
        variables.add(x);
        variables.add(y);
        variables.add(z);
        ConstraintSatisfactionState<Integer> css = new ConstraintSatisfactionState<>(bitSetFromVariables(variables), constraintSatisfaction);
        List<ConstraintSatisfactionState<Integer>> successors = css.generateSuccessors();
        assertThat(successors.size(), is(expectedSuccessorCount));
    }

    @Test
    public void timeGenerationOfSuccessors() throws InterruptedException {
        int count = 1000;
        int expectedSuccessorCount = 4 + 6 + 4;
        Integer[] set1 = new Integer[count];
        for (int i = 0; i < count; i++) set1[i] = i;
        Integer[] set2 = new Integer[count];
        for (int i = 0; i < count; i++) set2[i] = i;
        Integer[] set3 = new Integer[count];
        for (int i = 0; i < count; i++) set3[i] = i;
        Variable<Integer> x = new Variable<>("x", new ArrayWithView<>(set1));
        Variable<Integer> y = new Variable<>("y", new ArrayWithView<>(set2));
        Variable<Integer> z = new Variable<>("z", new ArrayWithView<>(set3));
        ArrayList<Variable<Integer>> variables = new ArrayList<>();
        variables.add(x);
        variables.add(y);
        variables.add(z);
        long start = System.nanoTime();
        ConstraintSatisfactionState<Integer> css = new ConstraintSatisfactionState<>(bitSetFromVariables(variables), constraintSatisfaction);
        List<ConstraintSatisfactionState<Integer>> successors = css.generateSuccessors();
        long end = System.nanoTime();
        System.out.println("Took :" + (end - start) / 1000000 + " ms to generate " + successors.size() + " successors.");
    }

    private <T> BitSet[] bitSetFromVariables(List<Variable<T>> variables) {
        BitSet[] toReturn = new BitSet[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            toReturn[i] = variables.get(i).packageGetDomain().getBitSet();
        }
        return toReturn;
    }
}
