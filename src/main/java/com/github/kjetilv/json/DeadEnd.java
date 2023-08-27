package com.github.kjetilv.json;

import java.util.List;

record DeadEnd<T>(T main, T expected, List<String> trace) implements EndProbe<T> {

    public static <T> DeadEnd<T> deadEnd(T main, List<String> trace) {
        return new DeadEnd<>(main, null, trace);
    }

    @Override
    public Rate successRate() {
        return Rate.FAILURE;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
            Print.trace(trace) + " " + new Diff<>(main, expected) +
            "]";
    }
}
