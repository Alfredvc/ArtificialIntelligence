package com.alfredvc.constraint_satisfaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erpa_ on 9/5/2015.
 */
public class Variable<T> implements Comparable<Variable<T>> {
    private String name;
    private ArraySet<T> domain;

    /**
     * Creates a new Variable instance, creates new list to be used internally
     * @param name
     * @param domain
     */
    public Variable(String name, List<T> domain) {
        this.name = name;
        this.domain = new ArraySet<>(domain);
    }

    /**
     * Returns a copy of the given variable, creates new list to be used internally.
     */
    public Variable(Variable<T> variable) {
        this(variable.getName(), new ArraySet<>(variable.getDomain()));
    }

    /**
     * Returns a copy of the given variable, uses the given list internally.
     */
    public Variable(Variable<T> variable, ArraySet<T> newDomain) {
        this.name = variable.getName();
        this.domain = newDomain;
    }

    public List<T> getDomain() {
        return domain;
    }

    public void setDomain(ArraySet<T> domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return this.name.equals(v.name) && this.domain.size() == v.domain.size() && domain.stream().allMatch(d -> v.domain.contains(d));
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", domain=" + domain +
                '}';
    }
}
