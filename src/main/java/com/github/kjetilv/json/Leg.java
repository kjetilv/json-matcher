package com.github.kjetilv.json;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.github.kjetilv.json.Pathway.deadEnd;

record Leg<T>(String name, Path<T> next, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Pathway<T>> through(T main, List<String> trace) {
        return structure.get(main, name)
            .map(field ->
                next.through(field, addTo(trace, name)))
            .orElseGet(() ->
                Stream.of(deadEnd(main, trace)));
    }

    private static List<String> addTo(List<String> list, String added) {
        return list == null
            ? Collections.singletonList(added)
            : Stream.concat(list.stream(), Stream.of(added)).toList();
    }
}
