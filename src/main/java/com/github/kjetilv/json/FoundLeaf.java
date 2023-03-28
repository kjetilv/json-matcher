package com.github.kjetilv.json;

import java.util.List;

record FoundLeaf<T>(T main, List<String> trace) implements Probe {

    @Override
    public Rate successRate() {
        return Rate.SUCCESS;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + Print.trace(trace) + ": " + main + "]";
    }
}
