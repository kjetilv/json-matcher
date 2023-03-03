package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

sealed interface Path<T>
    permits Leg, SubArray, ExactArray, Destination, Fork {

    default Stream<Search<T>> through(T main) {
        return through(main, null);
    }

    Stream<Search<T>> through(T main, List<String> trace);
}
