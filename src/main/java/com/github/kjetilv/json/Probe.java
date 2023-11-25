package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

public sealed interface Probe<T> permits LeafProbe, NodeProbe {

    default boolean found() {
        return successRate().is100Percent();
    }

    Rate successRate();

    List<String> trace();

    Stream<LeafProbe<T>> leaves();
}
