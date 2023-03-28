package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

record FoundNode(List<Probe> branches, List<String> trace) implements Probe {

    @Override
    public Rate successRate() {
        if (branches.size() == 0) {
            return Rate.SUCCESS;
        }
        long count = branches.stream().filter(Probe::found).count();
        return new Rate(
            Math.toIntExact(count),
            branches.size());
    }

    @Override
    public Stream<Probe> leaves() {
        return branches.stream().flatMap(Probe::leaves);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
               (trace == null || trace.isEmpty() ? "/" : String.join("/", trace)) + " " + branches +
               "]";
    }
}
