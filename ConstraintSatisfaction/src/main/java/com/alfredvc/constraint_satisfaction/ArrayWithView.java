package com.alfredvc.constraint_satisfaction;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * Created by erpa_ on 9/13/2015.
 */
public class ArrayWithView<T> implements Iterable<T>{

    private T[] list;
    private BitSet inView;
    private ArrayListWithViewIterator<T> it;
    private ArrayListWithViewCycleIterator<T> cycleIt;

    public ArrayWithView(T[] list, BitSet inView) {
        this.list = list;
        this.inView = inView;
        this.it = new ArrayListWithViewIterator<>(list, inView);
        this.cycleIt = new ArrayListWithViewCycleIterator<>(this, inView);
    }

    public ArrayWithView(ArrayWithView<T> other) {
        this(other.list, (BitSet) other.inView.clone());
    }

    public ArrayWithView(T[] list) {
        this.list = list;
        this.inView = new BitSet(list.length);
        this.inView.set(0, list.length, true);
    }

    public void setView(BitSet newView) {
        this.inView = newView;
    }

    public int size() {
        return this.inView.cardinality();
    }

    public boolean isEmpty(){
        return this.inView.cardinality() == 0;
    }

    public T getFirst(){
        return list[inView.nextSetBit(0)];
    }

    public T getFirst(BitSet view){
        return list[view.nextSetBit(0)];
    }

    public BitSet getBitSet() {
        return (BitSet) inView.clone();
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

    public Stream<T> stream(){
        return this.inView.stream().mapToObj(i -> list[i]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayWithView<?> that = (ArrayWithView<?>) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (this.inView.cardinality() != that.inView.cardinality()) return false;
        if (this.list.length != that.list.length) return false;
        if (!this.inView.equals(that.inView)) return false;
        for (int i = 0; i < list.length; i++) {
            if (this.list[i] != that.list[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = list != null ? Arrays.hashCode(list) : 0;
        result = 31 * result + (inView != null ? inView.hashCode() : 0);
        return result;
    }

    private void checkBounds(int i) {
        if (i < 0 || i >= list.length) throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        stream().forEach(t -> joiner.add(t.toString()));
        return "ArrayWithView{ "+ joiner.toString() + " }";
    }

    @Override
    public Iterator<T> iterator() {
        return this.it.setView(this.inView);
    }

    public Iterator<T> iterator(BitSet view) {
        return this.it.setView(view);
    }

    public Iterator<T> cycleIterator(BitSet view) {
        return this.cycleIt.setView(view);
    }

    private class ArrayListWithViewCycleIterator<T> extends ArrayListWithViewIterator<T> {
        private ArrayWithView<T> arrayWithView;
        private Iterator<T> innerIterator;
        private BitSet view;

        public ArrayListWithViewCycleIterator(ArrayWithView<T> arrayWithView, BitSet view) {
            this.arrayWithView = arrayWithView;
            this.innerIterator = arrayWithView.iterator(view);
            this.view = view;
        }

        private ArrayListWithViewIterator<T> setView(BitSet view){
            this.view = view;
            this.innerIterator = arrayWithView.iterator(view);
            return this;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public T next() {
            if (!innerIterator.hasNext()) innerIterator = arrayWithView.iterator(view);
            return innerIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class ArrayListWithViewIterator<T> implements Iterator<T> {

        private int currentIndex;
        private T[] array;
        private Iterator<Integer> viewIterator;
        private BitSet bitSet;

        ArrayListWithViewIterator(){

        }

        public ArrayListWithViewIterator(T[] array, BitSet bitSet) {
            this.array = array;
            this.bitSet = bitSet;
            this.viewIterator = bitSet.stream().iterator();
        }

        private ArrayListWithViewIterator<T> setView(BitSet view) {
            this.bitSet = view;
            this.viewIterator = bitSet.stream().iterator();
            return this;
        }

        @Override
        public boolean hasNext() {
            return viewIterator.hasNext();
        }

        @Override
        public T next() {
            currentIndex = viewIterator.next();
            return array[currentIndex];
        }

        @Override
        public void remove() {
            this.bitSet.set(currentIndex, false);
        }
    }

}
