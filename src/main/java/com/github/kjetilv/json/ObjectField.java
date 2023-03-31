package com.github.kjetilv.json;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.kjetilv.json.DeadEnd.deadEnd;

record ObjectField<T>(String name, Path<T> next, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Probe<T>> probe(T main, List<String> trace) {
        return structure.get(main, name)
            .map(field ->
                next.probe(field, addTo(trace, name)))
            .orElseGet(() ->
                Stream.of(deadEnd(main, trace)));
    }

    @Override
    public Optional<Extract<T>> extract(T main) {
        return structure.get(main, name).flatMap(next::extract);
    }

    private static List<String> addTo(List<String> list, String added) {
        return list == null
            ? Collections.singletonList(added)
            : Stream.concat(list.stream(), Stream.of(added)).toList();
    }
}
