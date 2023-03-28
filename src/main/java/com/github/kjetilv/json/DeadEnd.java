package com.github.kjetilv.json;

import java.util.List;

record DeadEnd<T>(List<String> trace, T main, T expected) implements Probe {

    public static <T> DeadEnd<T> deadEnd(T main, List<String> trace) {
        return new DeadEnd<>(trace, main, null);
    }

    @Override
    public Rate successRate() {
        return Rate.FAILURE;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + Print.trace(trace) + " " + main + ", expected:" + expected + "]";
    }
}
