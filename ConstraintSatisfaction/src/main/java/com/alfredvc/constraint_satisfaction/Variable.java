package com.alfredvc.constraint_satisfaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Alfredvc on 9/5/2015.
 */
public class Variable<T> implements Comparable<Variable<T>> {
    private String name;
    private ArrayWithView<T> domain;

    /**
     * Creates a new Variable instance, creates new list to be used internally
     * @param name
     * @param domain
     */
    Variable(String name, ArrayWithView<T> domain) {
        this.name = name;
        this.domain = new ArrayWithView<>(domain);
    }

    public Variable(String name, List<T> domain) {
        this(name, new ArrayWithView<>(domain.toArray((T[])new Object[0])));
    }

    public Variable(Variable<T> other) {
        this(other.name, other.domain);
    }

    ArrayWithView<T> packageGetDomain() {
        return domain;
    }

    /**
     * Returns an unmodifiable list containing the current domain.
     * @return the current domain
     */
    public List<T> getDomain() {
        return Collections.unmodifiableList(domain.stream().collect(Collectors.toList()));
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Variable<T> o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable)) return false;
        if (obj == null) return false;
        Variable<T> v = (Variable) obj;
        return this.name.equals(v.name) && this.domain.equals(v.domain);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", domain=" + domain +
                '}';
    }
}
