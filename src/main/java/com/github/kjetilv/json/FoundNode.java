package com.github.kjetilv.json;

import java.util.List;

record FoundNode<T>(List<Probe> branches, List<String> trace) implements Probe {

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
}
