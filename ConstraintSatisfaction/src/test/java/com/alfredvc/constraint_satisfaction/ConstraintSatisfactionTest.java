package com.alfredvc.constraint_satisfaction;


import com.alfredvc.FunctionParser;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for simple ConstraintSatisfaction.
 */
public class ConstraintSatisfactionTest {

    @Test
    public void simpleHappyDayTestWithoutSearch() {
        int expectedX = 3;
        int expectedY = 2;
        int expectedZ = 4;
        int expectedSuccessorCount = 4 + 6 + 4;
        Integer[] set1 = new Integer[4];
        for (int i = 0; i < 4; i++) set1[i] = i;
        Integer[] set2 = new Integer[5];
        for (int i = 0; i < 5; i++) set2[i] = i;
        Integer[] set3 = new Integer[4];
        for (int i = 4; i < 8; i++) set3[i-4] = i;
        Variable<Integer> x = new Variable<>("x", new ArrayWithView<>(set1));
        Variable<Integer> y = new Variable<>("y", new ArrayWithView<>(set2));
        Variable<Integer> z = new Variable<>("z", new ArrayWithView<>(set3));
        Constraint c1 = new Constraint(FunctionParser.fromString("boolean(Integer x,y)-> x > y"));
        Constraint c2 = new Constraint(FunctionParser.fromString("boolean(Integer z,y,x)-> x + y > z"));

        ConstraintSatisfaction<Integer> constraintSatisfaction = new ConstraintSatisfaction<>(Arrays.asList(c1, c2),Arrays.asList(x, y, z));
        ConstraintSatisfactionResult<Integer> result = constraintSatisfaction.solve();

        assertThat(result.getVariables().get("x").packageGetDomain().size(), is(1));
        assertThat(result.getVariables().get("x").packageGetDomain().getFirst(), is(expectedX));
        assertThat(result.getVariables().get("y").packageGetDomain().size(), is(1));
        assertThat(result.getVariables().get("y").packageGetDomain().getFirst(), is(expectedY));
        assertThat(result.getVariables().get("z").packageGetDomain().size(), is(1));
        assertThat(result.getVariables().get("z").packageGetDomain().getFirst(), is(expectedZ));
        assertThat(result.getStatus(), Matchers.is(ConstraintSatisfactionResult.Status.SUCCEEDED));
        assertThat(result.getViolatedConstraints(), Matchers.is(0));
        assertThat(result.getVariablesWithDomainNotEqualToOne(), Matchers.is(0));
    }

    @Test
    public void simpleHappyDayTestWithSearch() {
        int expectedSuccessorCount = 4 + 6 + 4;
        Integer[] set1 = new Integer[11];
        for (int i = 0; i < 11; i++) set1[i] = i;
        Integer[] set2 = new Integer[11];
        for (int i = 0; i < 11; i++) set2[i] = i;
        Integer[] set3 = new Integer[11];
        for (int i = 0; i < 11; i++) set3[i] = i;
        Variable<Integer> x = new Variable<>("x", new ArrayWithView<>(set1));
        Variable<Integer> y = new Variable<>("y", new ArrayWithView<>(set2));
        Variable<Integer> z = new Variable<>("z", new ArrayWithView<>(set3));
        Constraint c1 = new Constraint(FunctionParser.fromString("boolean(Integer x,y)-> x > y"));
        Constraint c2 = new Constraint(FunctionParser.fromString("boolean(Integer y,x,z)-> x + y > z"));

        ConstraintSatisfaction<Integer> constraintSatisfaction = new ConstraintSatisfaction<>(Arrays.asList(c1, c2), Arrays.asList(x, y, z));
        ConstraintSatisfactionResult<Integer> result = constraintSatisfaction.solve();

        assertThat(result.getVariables().get("x").packageGetDomain().size(), is(1));
        assertThat(result.getVariables().get("y").packageGetDomain().size(), is(1));
        assertThat(result.getVariables().get("z").packageGetDomain().size(), is(1));

        Object[] args1 = new Object[2];
        args1[0] = result.getVariables().get("x").packageGetDomain().getFirst();
        args1[1] = result.getVariables().get("y").packageGetDomain().getFirst();
        Object[] args2 = new Object[3];
        args2[0] = result.getVariables().get("x").packageGetDomain().getFirst();
        args2[1] = result.getVariables().get("y").packageGetDomain().getFirst();
        args2[2] = result.getVariables().get("z").packageGetDomain().getFirst();

        assertThat(c1.evaluate(args1), is(true));
        assertThat(c2.evaluate(args2), is(true));
        assertThat(result.getStatus(), Matchers.is(ConstraintSatisfactionResult.Status.SUCCEEDED));
        assertThat(result.getViolatedConstraints(), Matchers.is(0));
        assertThat(result.getVariablesWithDomainNotEqualToOne(), Matchers.is(0));
    }
}
