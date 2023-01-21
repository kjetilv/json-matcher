package com.github.kjetilv.json;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

record Pathway(JsonNode main, JsonNode expected, List<String> trace) {

    static Optional<Pathway> noMatchOption(JsonNode main, List<String> trace) {
        return Optional.of(noMatch(main, trace));
    }

    static Stream<Pathway> noMatchStream(JsonNode main, List<String> trace) {
        return Stream.of(noMatch(main, trace));
    }

    public static Pathway noMatch(JsonNode expected, List<String> trace) {
        return new Pathway(null, expected, trace);
    }

    static Stream<Pathway> possibleMatch(JsonNode main, JsonNode expected, List<String> trace) {
        return Stream.of(new Pathway(main, expected, trace));
    }

    static Stream<Pathway> exactPaths(
        JsonNode main,
        List<String> trace,
        List<JsonNode> mainElements,
        List<Path> paths
    ) {
        return mainElements.size() != paths.size()
            ? noMatchStream(main, trace)
            : Zip.of(paths, mainElements)
                .flatMap(xy ->
                    xy.x().through(xy.y()));
    }

    boolean found() {
        return Objects.equals(main, expected);
    }
}
