package com.github.kjetilv.json;

public interface StructureMatcher<T> {

    boolean contains(T part);

    T subset(T part);
}
