package com.alfredvc.constraint_satisfaction;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Alfredvc on 9/7/2015.
 */
public class ConstraintSatisfactionStateTest {

    @Test
    public void testGenerationOfSuccessors(){
        int expectedSuccessorCount = 4 + 6 + 4;
        Integer[] set1 = new Integer[4];
        for (int i = 1; i < 5; i++) set1[i-1] = i;
        Integer[] set2 = new Integer[6];
        for (int i = 1; i < 7; i++) set2[i-1] = i;
        Integer[] set3 = new Integer[4];
        for (int i = 4; i < 8; i++) set3[i-4] = i;
        Variable<Integer> x = new Variable<>("x", new ArrayWithView<>(set1));
        Variable<Integer> y = new Variable<>("y", new ArrayWithView<>(set2));
        Variable<Integer> z = new Variable<>("z", new ArrayWithView<>(set3));
        ArraySet<Variable<Integer>> variables = new ArraySet<>();
        variables.add(x);
        variables.add(y);
        variables.add(z);
        ConstraintSatisfactionState<Integer> css = new ConstraintSatisfactionState<>(variables);
        List<ConstraintSatisfactionState<Integer>> successors = css.generateSuccessors();
        assertThat(successors.size(), is(expectedSuccessorCount));
    }

    @Test
    public void timeGenerationOfSuccessors(){
        int expectedSuccessorCount = 4 + 6 + 4;
        Integer[] set1 = new Integer[9999];
        for (int i = 1; i < 10000; i++) set1[i-1] = i;
        Integer[] set2 = new Integer[9999];
        for (int i = 1; i < 10000; i++) set2[i-1] = i;
        Integer[] set3 = new Integer[9999];
        for (int i = 1; i < 10000; i++) set3[i-1] = i;
        Variable<Integer> x = new Variable<>("x", new ArrayWithView<>(set1));
        Variable<Integer> y = new Variable<>("y", new ArrayWithView<>(set2));
        Variable<Integer> z = new Variable<>("z", new ArrayWithView<>(set3));
        ArraySet<Variable<Integer>> variables = new ArraySet<>();
        variables.add(x);
        variables.add(y);
        variables.add(z);
        long start = System.nanoTime();
        ConstraintSatisfactionState<Integer> css = new ConstraintSatisfactionState<>(variables);
        List<ConstraintSatisfactionState<Integer>> successors = css.generateSuccessors();
        long end = System.nanoTime();
        System.out.println("Took :" + (end - start) / 1000000 + " ms to generate " + successors.size() + " successors.");
    }
}
