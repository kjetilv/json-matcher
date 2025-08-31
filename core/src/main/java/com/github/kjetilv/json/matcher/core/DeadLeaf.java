package com.github.kjetilv.json.matcher.core;

import java.util.List;

record DeadLeaf<T>(T main, T expected, List<String> trace) implements LeafProbe<T> {

    public static <T> DeadLeaf<T> deadEnd(T main, List<String> trace) {
        return new DeadLeaf<>(main, null, trace);
    }

    @Override
    public Rate successRate() {
        return Rate.FAILURE;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
            Print.trace(trace) + " " + new Diff<>(main, expected) +
            "]";
    }
}
