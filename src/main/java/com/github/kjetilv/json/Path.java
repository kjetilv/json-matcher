package com.github.kjetilv.json;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

sealed interface Path<T>
    permits Destination, ExactMatches, Subset, ExactObject, ObjectField, Subsequence {

    default Stream<Probe<T>> probe(T main) {
        return probe(main, null);
    }

    Stream<Probe<T>> probe(T main, List<String> trace);

    Optional<Extract<T>> extract(T main);
}
