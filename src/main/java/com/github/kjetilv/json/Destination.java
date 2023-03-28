package com.github.kjetilv.json;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

record Destination<T>(T expected) implements Path<T> {

    @Override
    public Stream<Probe> probe(T main, List<String> trace) {
        return Optional.ofNullable(main)
            .filter(expected::equals)
            .map(equal ->
                found(expected, trace))
            .or(() ->
                unexpected(main, expected, trace))
            .stream();
    }

    @Override
    public Optional<Extract<T>> extract(T main) {
        return Optional.ofNullable(main).map(value -> () -> value);
    }

    private static <T> Probe found(T expected, List<String> trace) {
        return new FoundLeaf<>(expected, trace);
    }

    private static <T> Optional<Probe> unexpected(T main, T expected, List<String> trace) {
        return Optional.of(new DeadEnd<>(trace, main, expected));
    }
}
