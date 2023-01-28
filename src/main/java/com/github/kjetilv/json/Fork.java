package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

import static com.github.kjetilv.json.Pathway.deadEnd;

record Fork<T>(Path<T> path, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Pathway<T>> through(T main, List<String> trace) {
        Stream<T> mains = structure.arrayElements(main);
        Pathway<T> pathway = mains.flatMap(node ->
                path.through(node, trace))
            .filter(Pathway::found)
            .findFirst()
            .orElseGet(() ->
                deadEnd(main, trace));
        return Stream.of(pathway);
    }
}
