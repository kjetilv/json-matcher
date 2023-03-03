package com.github.kjetilv.json;

import java.util.List;

record FoundNode<T>(List<Search> branches, List<String> trace) implements Search {

    @Override
    public Rate successRate() {
        if (branches.size() == 0) {
            return Rate.SUCCESS;
        }
        long count = branches.stream().filter(Search::found).count();
        return new Rate(
            Math.toIntExact(count),
            branches.size());
    }
}
