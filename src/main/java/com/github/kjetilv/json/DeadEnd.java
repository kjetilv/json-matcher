package com.github.kjetilv.json;

import java.util.List;

record DeadEnd<T>(T main, T expected, List<String> trace) implements Search {

    public static <T> DeadEnd<T> deadEnd(T expected, List<String> trace) {
        return new DeadEnd<>(null, expected, trace);
    }

    @Override
    public Rate successRate() {
        return Rate.FAILURE;
    }
}
