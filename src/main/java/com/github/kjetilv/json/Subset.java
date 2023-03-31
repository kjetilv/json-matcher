package com.github.kjetilv.json;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

record Subset<T>(Path<T> path, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Probe<T>> probe(T main, List<String> trace) {
        Stream<T> mains =
            structure.arrayElements(main);
        return Stream.of(mains.flatMap(node ->
                path.probe(node, trace))
            .filter(Probe::found)
            .findFirst()
            .orElseGet(() ->
                new DeadEnd<>(trace, main, null)));
    }

    @Override
    public Optional<Extract<T>> extract(T main) {
        return Optional.ofNullable(main).map(value -> () -> value);
    }
}
