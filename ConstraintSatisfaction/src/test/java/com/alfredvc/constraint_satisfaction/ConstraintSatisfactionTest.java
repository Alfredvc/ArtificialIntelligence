package com.alfredvc.constraint_satisfaction;


import com.alfredvc.FunctionParser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for simple ConstraintSatisfaction.
 */
public class ConstraintSatisfactionTest {

    @Test
    public void simpleHappyDayTestWithoutSearch() {
        List<Integer> xDomain = new ArrayList<>();
        xDomain.add(3);
        List<Integer> yDomain = new ArrayList<>();
        yDomain.add(2);
        List<Integer> zDomain = new ArrayList<>();
        zDomain.add(4);

        List<Integer> set1 = new ArrayList<>();
        for (int i = 0; i < 4; i++) set1.add(i);
        List<Integer> set2 = new ArrayList<>();
        for (int i = 0; i < 6; i++) set2.add(i);
        List<Integer> set3 = new ArrayList<>();
        for (int i = 4; i < 8; i++) set3.add(i);
        Variable<Integer> x = new Variable<>("x", set1);
        Variable<Integer> y = new Variable<>("y", set2);
        Variable<Integer> z = new Variable<>("z", set3);
        Constraint c1 = new Constraint(FunctionParser.fromString("boolean(Integer x,y)-> x > y"));
        Constraint c2 = new Constraint(FunctionParser.fromString("boolean(Integer x,y,z)-> x + y > z"));

        ConstraintSatisfaction<Integer> constraintSatisfaction = new ConstraintSatisfaction<>(Arrays.asList(c1, c2));
        ConstraintSatisfactionResult<Integer> result = constraintSatisfaction.solve(Arrays.asList(x, y, z));

        assertThat(result.getVariables().get("x").getDomain(), is(xDomain));
        assertThat(result.getVariables().get("y").getDomain(), is(yDomain));
        assertThat(result.getVariables().get("z").getDomain(), is(zDomain));
    }

    @Test
    public void simpleHappyDayTestWithSearch() {
        List<Integer> set1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) set1.add(i);
        List<Integer> set2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) set2.add(i);
        List<Integer> set3 = new ArrayList<>();
        for (int i = 4; i < 16; i++) set3.add(i);
        Variable<Integer> x = new Variable<>("x", set1);
        Variable<Integer> y = new Variable<>("y", set2);
        Variable<Integer> z = new Variable<>("z", set3);
        Constraint c1 = new Constraint(FunctionParser.fromString("boolean(Integer x,y)-> x > y"));
        Constraint c2 = new Constraint(FunctionParser.fromString("boolean(Integer x,y,z)-> x + y > z"));

        ConstraintSatisfaction<Integer> constraintSatisfaction = new ConstraintSatisfaction<>(Arrays.asList(c1, c2));
        ConstraintSatisfactionResult<Integer> result = constraintSatisfaction.solve(Arrays.asList(x, y, z));

        assertThat(result.getVariables().get("x").getDomain().size(), is(1));
        assertThat(result.getVariables().get("y").getDomain().size(), is(1));
        assertThat(result.getVariables().get("z").getDomain().size(), is(1));

        Object[] args1 = new Object[2];
        args1[0] = result.getVariables().get("x").getDomain().get(0);
        args1[1] = result.getVariables().get("y").getDomain().get(0);
        Object[] args2 = new Object[3];
        args2[0] = result.getVariables().get("x").getDomain().get(0);
        args2[1] = result.getVariables().get("y").getDomain().get(0);
        args2[2] = result.getVariables().get("z").getDomain().get(0);

        assertThat(c1.evaluate(args1), is(true));
        assertThat(c2.evaluate(args2), is(true));
    }
}
