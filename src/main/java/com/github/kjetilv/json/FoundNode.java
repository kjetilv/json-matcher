package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

record FoundNode<T>(List<Probe<T>> branches, List<String> trace) implements Probe<T> {

    @Override
    public Rate successRate() {
        if (branches.isEmpty()) {
            return Rate.SUCCESS;
        }
        long count = branches.stream().filter(Probe::found).count();
        return new Rate(
            Math.toIntExact(count),
            branches.size());
    }

    @Override
    public Stream<EndProbe<T>> leaves() {
        return branches.stream().flatMap(Probe::leaves);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + Print.trace(trace) + " -> " + branches + "]";
    }
}
