package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

import static com.github.kjetilv.json.JsonUtils.arrayElements;

record Array(List<Path> paths) implements Path {

    @Override
    public Stream<Pathway> through(JsonNode main, List<String> trace) {
        return Pathway.exactPaths(main, trace, arrayElements(main).toList(), paths);
    }
}
