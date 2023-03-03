package com.github.kjetilv.json;

import java.util.List;

record FoundLeaf<T>(T main, List<String> trace) implements Search {

    @Override
    public Rate successRate() {
        return Rate.SUCCESS;
    }
}
