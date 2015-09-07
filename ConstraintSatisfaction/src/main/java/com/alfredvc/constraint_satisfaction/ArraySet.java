package com.alfredvc.constraint_satisfaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Alfredvc on 9/7/2015.
 */
public class ArraySet<T> implements List<T>, Set<T> {

    private final List<T> innerList;
    private final Set<T> innerSet;

    private ArraySet(List<T> innerList, Set<T> innerSet) {
        this.innerList = innerList;
        this.innerSet = innerSet;
    }

    public ArraySet() {
        this(new ArrayList<>(), new HashSet<>());
    }

    public ArraySet(Collection<? extends T> list) {
        this(new ArrayList<>(list), new HashSet<>(list));
    }

    public static <F> ArraySet<F> withBackingList(List<F> innerList) {
        return new ArraySet<>(innerList, new HashSet<>(innerList));        
    }

    @Override
    public int size() {
        return innerList.size();
    }

    @Override
    public boolean isEmpty() {
        return innerList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return innerSet.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new ArraySetIterator<>(innerList, innerSet);
    }

    @Override
    public Object[] toArray() {
        return innerList.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return innerList.toArray(a);
    }

    @Override
    public boolean add(T t) {
        if (innerSet.contains(t)) {
            return false;
        } else {
            innerSet.add(t);
            innerList.add(t);
            return true;
        }
    }

    @Override
    public boolean remove(Object o) {
        innerSet.remove(o);
        return innerList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return innerSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return c.stream()
                .map(a -> this.add(a))
                .anyMatch(e -> e);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return c.stream()
                .map(a -> this.remove(a))
                .anyMatch(e -> e);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        innerSet.clear();
        innerList.clear();
    }

    @Override
    public T get(int index) {
        return innerList.get(index);
    }

    @Override
    public T set(int index, T element) {
        if (innerSet.contains(element)) {
            return null;
        } else {
            T removed = innerList.set(index, element);
            innerSet.remove(removed);
            return removed;
        }
    }

    @Override
    public void add(int index, T element) {
        if (innerSet.add(element)) {
            innerList.add(index, element);
        }
    }

    @Override
    public T remove(int index) {
        T removed = innerList.remove(index);
        innerSet.remove(removed);
        return removed;
    }

    @Override
    public int indexOf(Object o) {
        return innerList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return innerList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ArraySetListIterator<>(innerList, innerSet);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ArraySetListIterator<>(innerList, innerSet, index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator<T> spliterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<T> stream() {
        return innerList.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return innerList.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        innerList.forEach(action);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        innerList.sort(c);
    }

    @Override
    public int hashCode() {
        return innerList.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return innerList.equals(obj);
    }

    @Override
    public String toString() {
        return innerList.toString();
    }

    private class ArraySetIterator<T> implements Iterator<T> {

        private final ListIterator<T> innerIterator;
        private final Set<T> backingSet;

        public ArraySetIterator(List<T> backingList, Set<T> backingSet) {
            this.backingSet = backingSet;
            this.innerIterator = backingList.listIterator();
        }

        @Override
        public void remove() {
            backingSet.remove(innerIterator.previous());
            innerIterator.remove();
        }

        @Override
        public boolean hasNext() {
            return innerIterator.hasNext();
        }

        @Override
        public T next() {
            return innerIterator.next();
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            innerIterator.forEachRemaining(action);
        }
    }

    private class ArraySetListIterator<T> implements ListIterator<T> {

        private final Set<T> backingSet;
        private final ListIterator<T> backingIterator;

        public ArraySetListIterator(List<T> backingList, Set<T> backingSet, int index) {
            this.backingSet = backingSet;
            this.backingIterator = backingList.listIterator();
        }

        public ArraySetListIterator(List<T> backingList, Set<T> backingSet) {
            this(backingList, backingSet, 0);
        }

        @Override
        public boolean hasNext() {
            return backingIterator.hasNext();
        }

        @Override
        public T next() {
            return backingIterator.next();
        }

        @Override
        public boolean hasPrevious() {
            return backingIterator.hasPrevious();
        }

        @Override
        public T previous() {
            return backingIterator.previous();
        }

        @Override
        public int nextIndex() {
            return backingIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return backingIterator.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(T t) {
            throw new UnsupportedOperationException();
        }
    }
}
