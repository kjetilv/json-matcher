package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

sealed interface Path<T>
    permits Destination, ExactArray, Fork, Legs, Leg, SubArray {

    default Stream<Search> through(T main) {
        return through(main, null);
    }

    Stream<Search> through(T main, List<String> trace);
}
