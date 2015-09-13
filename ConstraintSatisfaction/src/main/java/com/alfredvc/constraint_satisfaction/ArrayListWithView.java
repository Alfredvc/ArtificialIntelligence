package com.alfredvc.constraint_satisfaction;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by erpa_ on 9/13/2015.
 */
public class ArrayListWithView<T> implements Iterable<T>{

    private T[] list;
    private BitSet inView;

    public ArrayListWithView(T[] list, BitSet inView) {
        this.list = list;
        this.inView = inView;
    }

    public ArrayListWithView(T[] list) {
        this.list = list;
        this.inView = new BitSet(list.length);
        this.inView.set(0, list.length, true);
    }

    public void setView(BitSet newView) {
        this.inView = newView;
    }

    public void remove(int i) {
        checkBounds(i);
        this.inView.set(i, false);
    }

    public void removeAllExept(int i) {
        checkBounds(i);
        this.inView.clear();
        this.inView.set(i, true);
    }

    private void checkBounds(int i) {
        if (i < 0 || i >= list.length) throw new IndexOutOfBoundsException();
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayListWithViewIterator<>(list, inView);
    }

    private class ArrayListWithViewIterator<T> implements Iterator<T> {

        private int currentIndex;
        private T[] array;
        private BitSet inView;

        public ArrayListWithViewIterator(T[] array, BitSet inView) {
            this.array = array;
            this.currentIndex = 0;
            this.inView = inView;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < array.length || (currentIndex + 1< array.length && inView.nextSetBit(currentIndex + 1) != -1);
        }

        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            currentIndex = inView.nextSetBit(currentIndex + 1);
            return array[currentIndex];
        }

        @Override
        public void remove() {
            inView.set(currentIndex, false);
        }
    }

}
