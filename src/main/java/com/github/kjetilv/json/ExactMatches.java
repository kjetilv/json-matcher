package com.github.kjetilv.json;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

record ExactMatches<T>(List<Path<T>> paths, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Probe<T>> probe(T main, List<String> trace) {
        List<T> mainElements = structure.arrayElements(main).toList();
        return DefaultStructureMatcher.exactPaths(trace, mainElements, paths);
    }

    @Override
    public Optional<Extract<T>> extract(T main) {
        return Optional.ofNullable(main).map(value -> () -> value);
    }
}
