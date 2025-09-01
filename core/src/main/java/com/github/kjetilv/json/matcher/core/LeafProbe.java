package com.github.kjetilv.json.matcher.core;

import java.util.stream.Stream;

public sealed interface LeafProbe<T> extends Probe<T> permits FoundLeaf, DeadLeaf {

    T main();

    @Override
    default Stream<Probe<T>> leaves() {
        return Stream.of(this);
    }
}
