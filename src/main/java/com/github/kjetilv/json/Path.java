package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

sealed interface Path<T>
    permits Leg, SubArray, Array, Destination, Fork {

    default Stream<Pathway<T>> through(T main) {
        return through(main, null);
    }

    Stream<Pathway<T>> through(T main, List<String> trace);
}
