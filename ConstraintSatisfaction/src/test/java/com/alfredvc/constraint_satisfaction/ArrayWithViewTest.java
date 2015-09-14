package com.alfredvc.constraint_satisfaction;

import org.junit.Test;

import java.util.InputMismatchException;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by erpa_ on 9/14/2015.
 */
public class ArrayWithViewTest {

    @Test
    public void removeElements(){
        Integer[] list = {0,1,2,3,4,5,6,7,8,9};
        Integer[] expectedList = {0,2,4,6,8};
        ArrayWithView<Integer> array = new ArrayWithView<>(list);
        for (Iterator<Integer> iterator = array.iterator(); iterator.hasNext(); ) {
            Integer i = iterator.next();
            if (i % 2 == 0) iterator.remove();
        }
        int a = 0;
        for (Integer i : array) {
            assertThat(list[a], is(i));
            a++;
        }

    }
}
