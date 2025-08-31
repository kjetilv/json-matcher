package com.github.kjetilv.json.matcher.core;

import java.util.List;

record FoundLeaf<T>(T main, List<String> trace) implements LeafProbe<T> {

    @Override
    public Rate successRate() {
        return Rate.SUCCESS;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + Print.trace(trace) + ": " + main + "]";
    }
}
