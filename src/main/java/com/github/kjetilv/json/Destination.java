package com.github.kjetilv.json;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

record Destination<T>(T expected) implements Path<T> {

    @Override
    public Stream<Search<T>> through(T main, List<String> trace) {
        return Optional.ofNullable(main)
            .filter(expected::equals)
            .map(equal ->
                found(expected, trace))
            .or(() ->
                unexpected(main, expected, trace))
            .stream();
    }

    private static <T> Search<T> found(T expected, List<String> trace) {
        return new FoundLeaf<>(expected, trace);
    }

    private static <T> Optional<Search<T>> unexpected(T main, T expected, List<String> trace) {
        return Optional.of(new DeadEnd<>(main, expected, trace));
    }
}
