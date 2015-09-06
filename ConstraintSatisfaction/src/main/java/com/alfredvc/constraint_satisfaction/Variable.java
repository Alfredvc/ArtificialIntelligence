package com.alfredvc.constraint_satisfaction;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by erpa_ on 9/5/2015.
 */
public class Variable<T> {
    private Set<T> domain;
    private String name;

    public Variable(Set<T> domain, String name) {
        this.domain = new HashSet<>(domain);
        this.name = name;
    }

    public Set<T> getDomain() {
        return domain;
    }

    public void setDomain(Set<T> domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "domain=" + domain +
                ", name='" + name + '\'' +
                '}';
    }
}
