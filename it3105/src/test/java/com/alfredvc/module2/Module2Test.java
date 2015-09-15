package com.alfredvc.module2;


import com.alfredvc.constraint_satisfaction.ArraySet;
import com.alfredvc.constraint_satisfaction.Constraint;
import com.alfredvc.constraint_satisfaction.Variable;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.isOneOf;

/**
 * Unit test for simple Module2.
 */
public class Module2Test {

    @Test
    public void textParseTest() {
        DoublePoint expected0 = new DoublePoint(2.5, 1.5);
        DoublePoint expected1 = new DoublePoint(4.0, 4.0);
        DoublePoint expected2 = new DoublePoint(5.5, 1.5);

        PointLine expectedL0 = new PointLine(expected0, expected1);
        PointLine expectedL1 = new PointLine(expected0, expected2);
        PointLine expectedL2 = new PointLine(expected1, expected2);

        List<String> varNames = Arrays.asList("0", "1", "2");

        List<Integer> domain = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            domain.add(i);
        }

        List<List<String>> constraintVars = new ArrayList<>();
        constraintVars.add(Arrays.asList("0", "1"));
        constraintVars.add(Arrays.asList("0", "2"));
        constraintVars.add(Arrays.asList("1", "2"));

        String input = "3 3\n" +
                "0 2.5 1.5\n" +
                "1 4.0 4.0\n" +
                "2 5.5 1.5\n" +
                "0 1\n" +
                "0 2\n" +
                "1 2";

        Module2.Module2DataHolder holder = Module2.parseInput(input, 4);
        assertThat(holder.getColorSetSize(), is(4));
        assertThat(equalDoublePoints(holder.getPoints().get(0), expected0), is(true));
        assertThat(equalDoublePoints(holder.getPoints().get(1), expected1), is(true));
        assertThat(equalDoublePoints(holder.getPoints().get(2), expected2), is(true));

        assertThat(equalPointLine(holder.getLines().get(0), expectedL0), is(true));
        assertThat(equalPointLine(holder.getLines().get(1), expectedL1), is(true));
        assertThat(equalPointLine(holder.getLines().get(2), expectedL2), is(true));

        for(Variable<Integer> var : holder.getVariables()) {
            assertThat(var.getName(), isIn(varNames));
            int i = 0;
            for(Integer a : var.getDomain()) {
                assertThat(domain.get(i), is(a));
                i++;
            }
        }

        Object[] args = new Object[2];
        args[0] = Integer.valueOf(1);
        args[1] = Integer.valueOf(2);

        for (Constraint c : holder.getConstraints()) {
            assertThat(c.getVariableArraySet(), isIn(constraintVars));
            assertThat(c.evaluate(args), is(true));
        }
    }

    private boolean equalDoublePoints(DoublePoint a, DoublePoint b) {
        return ( a.getX() == b.getX()) && ( b.getY() == b.getY() );
    }

    private boolean equalPointLine(PointLine a, PointLine b) {
        return equalDoublePoints(a.getFrom(), b.getFrom()) && equalDoublePoints(a.getTo(),b.getTo());
    }

}
