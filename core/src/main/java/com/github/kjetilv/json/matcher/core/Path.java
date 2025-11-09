package com.github.kjetilv.json.matcher.core;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

sealed interface Path<T> permits Paths.Destination,
    Paths.ExactMatches,
    Paths.ExactObject,
    Paths.ObjectField,
    Paths.Subsequence,
    Paths.Subset {

    default Stream<Probe<T>> probe(T main) {
        return probe(main, null);
    }

    Stream<Probe<T>> probe(T main, List<String> trace);

    Optional<Extract<T>> extract(T main);
}
