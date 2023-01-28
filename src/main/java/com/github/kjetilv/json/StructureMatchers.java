package com.github.kjetilv.json;

@SuppressWarnings("unused")
public final class StructureMatchers {

    public static <T> StructureMatcher<T> node(T obj, Structure<T> structure) {
        return node(obj, structure, null);
    }

    public static <T> StructureMatcher<T> node(T obj, Structure<T> structure, ArrayStrategy arrayStrategy) {
        return new DefaultStructureMatcher<>(obj, structure, arrayStrategy);
    }

    public enum ArrayStrategy {

        SUBSET, SUBSEQ, EXACT
    }

    private StructureMatchers() {
    }
}
