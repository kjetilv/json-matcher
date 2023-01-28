package com.github.kjetilv.json;

@FunctionalInterface
public interface StructureMatcher<T> {

    boolean contains(T part);
}
