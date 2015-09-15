package com.alfredvc.constraint_satisfaction;

import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by erpa_ on 9/14/2015.
 */
public class ArrayWithViewTest {

    @Test
    public void removeElements(){
        Integer[] list = {0,1,2,3,4,5,6,7,8,9};
        Integer[] expectedList = {1,3,5,7,9};
        ArrayWithView<Integer> array = new ArrayWithView<>(list);
        for (Iterator<Integer> iterator = array.iterator(); iterator.hasNext(); ) {
            Integer i = iterator.next();
            if (i % 2 == 0) iterator.remove();
        }
        int a = 0;
        for (Integer i : array) {
            assertThat(expectedList[a], is(i));
            a++;
        }
    }

    @Test
    public void iteratorTest(){
        Integer[] list = {0,1,2,3,4,5,6,7,8,9};
        ArrayWithView<Integer> array = new ArrayWithView<>(list);
        BitSet original = array.getBitSet();
        BitSet copy = array.getBitSet();
        BitSet empty = new BitSet(list.length);
        for (Iterator<Integer> iterator = array.iterator(copy); iterator.hasNext(); ) {
            Integer i = iterator.next();
            iterator.remove();
        }
        assertThat(original, is(array.getBitSet()));
        assertThat(copy, is(empty));
    }

    @Test
    public void cycleIteratorTest(){
        Integer[] list = {0,1,2,3,4,5,6,7,8,9};
        Integer[] reducedList = {0,2,4,6,8};
        ArrayWithView<Integer> array = new ArrayWithView<>(list);
        BitSet reducedBitSet = new BitSet(list.length);
        for (Integer i : reducedList) {
            reducedBitSet.set(i);
        }
        List<Integer> l = new ArrayList<>();
        IntStream.range(0, 10).forEach(i ->l.addAll(Arrays.asList(reducedList)));
        int a = 0;
        for (Iterator<Integer> it = array.cycleIterator(reducedBitSet); it.hasNext();){
            Integer z = it.next();
            assertThat(l.get(a), is(z));
            a++;
            if (a >= 50) break;
        }

    }

}
