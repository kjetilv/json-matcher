package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

record Fork<T>(Path<T> path, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Search> through(T main, List<String> trace) {
        Stream<T> mains = structure.arrayElements(main);
        Search pathway = mains.flatMap(node ->
                path.through(node, trace))
            .filter(Search::found)
            .findFirst()
            .orElseGet(() ->
                new DeadEnd<>(main, null, trace));
        return Stream.of(pathway);
    }
}
