package com.alfredvc.constraint_satisfaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erpa_ on 9/5/2015.
 */
public class Variable<T> {
    private String name;
    private List<T> domain;

    public Variable(String name, List<T> domain) {
        this.name = name;
        this.domain = domain;
    }

    public List<T> getDomain() {
        return domain;
    }

    public void setDomain(List<T> domain) {
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
                "name='" + name + '\'' +
                ", domain=" + domain +
                '}';
    }
}
