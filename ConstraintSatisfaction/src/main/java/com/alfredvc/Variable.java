package com.alfredvc;

import java.util.Set;

/**
 * Created by erpa_ on 9/5/2015.
 */
public class Variable<T> {
    private Set<T> domain;
    private String name;

    public Variable(Set<T> domain, String name) {
        this.domain = domain;
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
}
