package com.alfredvc.module3;

import com.alfredvc.constraint_satisfaction.Constraint;
import com.alfredvc.constraint_satisfaction.ConstraintSatisfaction;
import com.alfredvc.constraint_satisfaction.ConstraintSatisfactionResult;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Comparator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by erpa_ on 9/26/2015.
 */
public class Module3Test {

    @Test
    public void testConstraint(){
        Boolean[] vars = {Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE};
        assertThat(evalConstraint(vars), is(true));

        Boolean[] vars1 = {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE};
        assertThat(evalConstraint(vars1), is(false));

        Boolean[] vars2 = {Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE};
        assertThat(evalConstraint(vars2), is(false));

        Boolean[] vars3 = {Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE};
        assertThat(evalConstraint(vars3), is(true));

        Boolean[] vars4 = {Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
        assertThat(evalConstraint(vars4), is(true));

        Boolean[] vars5 = {Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE};
        assertThat(evalConstraint(vars5), is(false));

        Boolean[] vars6 = {Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE};
        assertThat(evalConstraint(vars6), is(false));

        Boolean[] vars7 = {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
        assertThat(evalConstraint(vars7), is(false));
        
    }

    private boolean evalConstraint(Boolean[] vars){
        int[] c = {2, 3};
        return evalConstraints(vars, c);
    }

    private boolean evalConstraints(Boolean[] vars, int[] c){
        int currentConstraint = 0;
        int currentLength = 0;
        for (int i = 0; i < vars.length; i++) {
            if (currentConstraint == c.length && vars[i]) {
                return false;
            }
            if (currentLength > 0) {
                if (vars[i]){
                    currentLength++;
                } else {
                    if (currentConstraint >= c.length || currentLength != c[currentConstraint]) return false;
                    currentConstraint++;
                    currentLength = 0;
                }
            } else {
                if (vars[i]) currentLength++;
            }
        }
        if (currentConstraint == c.length ||
                (currentConstraint == c.length - 1 && currentLength == c[currentConstraint]) ) {
            return true;
        } else {
            return false;
        }
    }

    @Test
    public void testConstraintGeneration_first_example(){
        //Rows
        Object[] c0args = {true, true, true, true, true, true};
        Object[] c1args = {false, true, false, false, true, false};
        Object[] c2args = {false, true, false, false, true, false};
        Object[] c3args = {false, true, false, false, true, false};
        Object[] c4args = {true, false, true, true, false, true};
        Object[] c5args = {false, true, false, false, true, false};
        Object[] c6args = {false, false, true, true, false, false};

        //Columns
        Object[] c7args = {false, false, true, false, false, false, true};
        Object[] c8args = {false, true, false, true, true, true, true};
        Object[] c9args = {true, false, true, false, false, false, true};
        Object[] c10args = {true, false, true, false, false, false, true};
        Object[] c11args = {false, true, false, true, true, true, true};
        Object[] c12args = {false, false, true, false, false, false, true};
        Object[][] args = {c0args, c1args, c2args, c3args, c4args, c5args, c6args, c7args, c8args, c9args,c10args ,c11args, c12args};
        String input = "6 7\n" +
                "6\n" +
                "1 1\n" +
                "1 1\n" +
                "1 1\n" +
                "1 2 1\n" +
                "1 1\n" +
                "2\n" +
                "1 1\n" +
                "1 4\n" +
                "1 1 1\n" +
                "1 1 1\n" +
                "1 4\n" +
                "1 1";
        Module3Convenience.Module3DataHolder holder = Module3Convenience.parseInput(input);
        for (int i = 0; i < holder.getConstraints().size(); i++) {
            assertThat(holder.getConstraints().get(i).evaluate(args[i]), is(true));
        }
    }

    @Test
    public void testConstraintSatisfaction(){
        String input = "6 7\n" +
                "6\n" +
                "1 1\n" +
                "1 1\n" +
                "1 1\n" +
                "1 2 1\n" +
                "1 1\n" +
                "2\n" +
                "1 1\n" +
                "1 4\n" +
                "1 1 1\n" +
                "1 1 1\n" +
                "1 4\n" +
                "1 1";
        Module3Convenience.Module3DataHolder holder = Module3Convenience.parseInput(input);
        ConstraintSatisfaction<Boolean> csp = new ConstraintSatisfaction<>(holder.getConstraints(), holder.getVariables());
        ConstraintSatisfactionResult<Boolean> result = csp.solve();
        assertThat(result.getVariablesWithDomainNotEqualToOne(), is(0));
        assertThat(result.getViolatedConstraints(), is(0));
    }

    @Ignore
    @Test
    public void performanceTestSailboat(){
        String input = "20 20\n" +
                "8\n" +
                "10\n" +
                "13\n" +
                "15\n" +
                "20\n" +
                "1 1 1\n" +
                "15\n" +
                "2 2 10\n" +
                "2 2 11\n" +
                "2 2 11\n" +
                "2 3 10\n" +
                "3 3 9\n" +
                "3 3 8\n" +
                "3 3 7\n" +
                "3 3 6\n" +
                "3 3 5\n" +
                "3 3 4\n" +
                "3 3 3\n" +
                "3 3\n" +
                "3\n" +
                "4 1\n" +
                "6 1\n" +
                "3 2 1\n" +
                "3 3 1 2\n" +
                "3 6 3\n" +
                "3 3 2 3\n" +
                "3 3 2 1 4\n" +
                "3 3 5 5\n" +
                "3 3 6 5\n" +
                "2 3 7 5\n" +
                "1 3 14\n" +
                "3 9 5\n" +
                "2 10 5\n" +
                "1 17\n" +
                "12 5\n" +
                "12 4\n" +
                "16\n" +
                "2\n" +
                "1\n" +
                "1";
        Comparator<Constraint> constraintComparator = (c1, c2) -> c2.getRating() - c1.getRating();
        Module3Convenience.Module3DataHolder holder = Module3Convenience.parseInput(input);
        ConstraintSatisfaction<Boolean> csp = new ConstraintSatisfaction<>(holder.getConstraints(), holder.getVariables(), constraintComparator);
        long start = System.nanoTime();
        ConstraintSatisfactionResult<Boolean> result = csp.solve();
        long finish = System.nanoTime();
        assertThat(result.getVariablesWithDomainNotEqualToOne(), is(0));
        assertThat(result.getViolatedConstraints(), is(0));
        System.out.printf("Took %d ms", (finish - start) / 1000000);
    }


}

