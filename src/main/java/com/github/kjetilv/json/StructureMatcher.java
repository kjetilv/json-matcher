package com.github.kjetilv.json;

@FunctionalInterface
public interface StructureMatcher<T> {

    Match match(T part);

    default boolean contains(T part) {
        return match(part).matches();
    }
}
