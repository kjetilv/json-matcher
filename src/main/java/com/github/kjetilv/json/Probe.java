package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

public interface Probe<T> {

    default boolean found() {
        return successRate().is100Percent();
    }

    Rate successRate();

    List<String> trace();

    Stream<EndProbe<T>> leaves();
}
