package com.github.kjetilv.json;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

import static com.github.kjetilv.json.JsonUtils.arrayElements;
import static com.github.kjetilv.json.Pathway.noMatchStream;

record SubArray(List<Path> paths) implements Path {

    @Override
    public Stream<Pathway> through(JsonNode main, List<String> trace) {
        List<JsonNode> mainElements = arrayElements(main).toList();
        if (mainElements.size() < paths.size()) {
            return noMatchStream(main, trace);
        }
        OptionalInt firstMatch = IntStream.range(0, mainElements.size())
            .filter(i ->
                paths.get(0).through(mainElements.get(i)).allMatch(Pathway::found))
            .findFirst();
        if (firstMatch.isEmpty()) {
            return noMatchStream(main, trace);
        }
        return Pathway.exactPaths(
            main,
            trace,
            subsequence(firstMatch.getAsInt(), mainElements),
            paths);
    }

    private List<JsonNode> subsequence(int startIndex, List<JsonNode> nodes) {
        return nodes.subList(
            startIndex,
            Math.min(nodes.size(), startIndex + paths.size()));
    }
}
