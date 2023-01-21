package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

sealed interface Path
    permits Leg, SubArray, Array, Destination, Fork {

    default Stream<Pathway> through(JsonNode main) {
        return through(main, null);
    }

    Stream<Pathway> through(JsonNode main, List<String> trace);
}
