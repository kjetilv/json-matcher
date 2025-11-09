package com.github.kjetilv.json.matcher.core;

import java.util.List;
import java.util.stream.Stream;

record FoundLeaf<T>(T main, List<String> trace) implements LeafProbe<T> {

    @Override
    public Rate successRate() {
        return Rate.SUCCESS;
    }

    @Override
    public Stream<String> lines(String indent, String delta) {
        return Stream.of(
            getClass().getSimpleName() + "[" + Print.trace(trace) + "]:",
            delta + main
        );
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + Print.trace(trace) + ": " + main + "]";
    }
}
