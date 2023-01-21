package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

import static com.github.kjetilv.json.JsonUtils.arrayElements;
import static com.github.kjetilv.json.Pathway.noMatchOption;

record Fork(Path path) implements Path {

    @Override
    public Stream<Pathway> through(JsonNode main, List<String> trace) {
        Stream<JsonNode> mains = arrayElements(main);
        return mains.flatMap(node ->
                path.through(node, trace))
            .filter(Pathway::found)
            .findFirst().or(() ->
                noMatchOption(main, trace))
            .stream();
    }
}
