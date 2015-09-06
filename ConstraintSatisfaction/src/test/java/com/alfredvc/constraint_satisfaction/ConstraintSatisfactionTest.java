package com.alfredvc.constraint_satisfaction;


import com.alfredvc.FunctionParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit test for simple ConstraintSatisfaction.
 */
public class ConstraintSatisfactionTest {

    @Test
    public void simpleHappyDayTest(){
        Set<Integer> integerSet = new HashSet<>();
        for (int i = 0; i < 11; i++) integerSet.add(i);
        Variable<Integer> x = new Variable<>(integerSet, "x");
        Variable<Integer> y = new Variable<>(integerSet, "y");
        Variable<Integer> z = new Variable<>(integerSet, "z");
        Constraint c1 = new Constraint(FunctionParser.fromString("(boolean=Integer:x,y)-> x > y"));
        Constraint c2 = new Constraint(FunctionParser.fromString("(boolean=Integer:x,y,z)-> x + y > z"));

        ConstraintSatisfaction<Integer> constraintSatisfaction = new ConstraintSatisfaction<>(Arrays.asList(c1,c2), Arrays.asList(x, y, z));
        ConstraintSatisfactionResult<Integer> result = constraintSatisfaction.solve();

        System.out.println(result);
    }

}
