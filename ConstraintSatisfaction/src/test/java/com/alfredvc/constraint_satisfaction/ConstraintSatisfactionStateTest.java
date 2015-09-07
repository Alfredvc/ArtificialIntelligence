package com.alfredvc.constraint_satisfaction;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by erpa_ on 9/7/2015.
 */
public class ConstraintSatisfactionStateTest {

    @Test
    public void testGenerationOfSuccessors(){
        int expectedSuccessorCount = 4 + 6 + 4;
        List<Integer> set1 = new ArrayList<>();
        for (int i = 0; i < 4; i++) set1.add(i);
        List<Integer> set2 = new ArrayList<>();
        for (int i = 0; i < 6; i++) set2.add(i);
        List<Integer> set3 = new ArrayList<>();
        for (int i = 4; i < 8; i++) set3.add(i);
        Variable<Integer> x = new Variable<>("x", set1);
        Variable<Integer> y = new Variable<>("y", set2);
        Variable<Integer> z = new Variable<>("z", set3);
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
        List<Integer> set1 = new ArrayList<>();
        for (int i = 0; i < 1000; i++) set1.add(i);
        List<Integer> set2 = new ArrayList<>();
        for (int i = 0; i < 1000; i++) set2.add(i);
        List<Integer> set3 = new ArrayList<>();
        for (int i = 4; i < 1000; i++) set3.add(i);
        Variable<Integer> x = new Variable<>("x", set1);
        Variable<Integer> y = new Variable<>("y", set2);
        Variable<Integer> z = new Variable<>("z", set3);
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
