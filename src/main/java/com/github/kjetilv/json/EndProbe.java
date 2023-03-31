package com.github.kjetilv.json;

import java.util.stream.Stream;

public interface EndProbe<T> extends Probe<T> {

    T main();

    @Override
    default Stream<EndProbe<T>> leaves() {
        return Stream.of(this);
    }
}
