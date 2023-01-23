package com.github.kjetilv.json;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

record Pathway(JsonNode main, JsonNode expected, List<String> trace) {

    static Stream<Pathway> deadEndStream(JsonNode main, List<String> trace) {
        return deadEndOption(main, trace).stream();
    }

    static Optional<Pathway> deadEndOption(JsonNode main, List<String> trace) {
        return Optional.of(deadEnd(main, trace));
    }

    public static Pathway deadEnd(JsonNode expected, List<String> trace) {
        return new Pathway(null, expected, trace);
    }

    static Stream<Pathway> arrived(JsonNode main, JsonNode expected, List<String> trace) {
        return Stream.of(new Pathway(main, expected, trace));
    }

    static Stream<Pathway> exactPaths(
        JsonNode main,
        List<String> trace,
        List<JsonNode> mainElements,
        List<Path> paths
    ) {
        return mainElements.size() != paths.size()
            ? deadEndStream(main, trace)
            : Zip.of(paths, mainElements)
                .flatMap(xy ->
                    xy.x().through(xy.y()));
    }

    boolean found() {
        return Objects.equals(main, expected);
    }
}
