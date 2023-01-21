package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

record Destination(JsonNode expected) implements Path {

    @Override
    public Stream<Pathway> through(JsonNode main, List<String> trace) {
        return Pathway.possibleMatch(main, expected, trace);
    }
}
