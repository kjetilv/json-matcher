package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

public interface Probe {

    default boolean found() {
        return successRate().is100Percent();
    }

    Rate successRate();

    List<String> trace();

    default Stream<Probe> leaves() {
        return Stream.of(this);
    }
}
