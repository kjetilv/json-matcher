package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

record Destination<T>(T expected) implements Path<T> {

    @Override
    public Stream<Pathway<T>> through(T main, List<String> trace) {
        return Pathway.arrived(main, expected, trace);
    }
}
