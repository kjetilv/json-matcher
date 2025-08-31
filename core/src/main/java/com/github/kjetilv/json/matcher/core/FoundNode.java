package com.github.kjetilv.json.matcher.core;

import java.util.List;
import java.util.stream.Stream;

record FoundNode<T>(List<Probe<T>> branches, List<String> trace) implements NodeProbe<T> {

    @Override
    public Rate successRate() {
        if (branches.isEmpty()) {
            return Rate.SUCCESS;
        }
        long count = branches.stream().filter(Probe::found).count();
        return Rate.of(
            Math.toIntExact(count),
            branches.size());
    }

    @Override
    public Stream<LeafProbe<T>> leaves() {
        return branches.stream().flatMap(Probe::leaves);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + Print.trace(trace) + " -> " + branches + "]";
    }
}
