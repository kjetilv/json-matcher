package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

record Legs<T>(List<Leg<T>> legs, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Search> through(T main, List<String> trace) {
        return Stream.of(
            new FoundNode<>(
                legs.stream().flatMap(leg -> leg.through(main, trace)).toList(),
                trace));
    }
}
