package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

import static com.github.kjetilv.json.JsonUtils.addTo;
import static com.github.kjetilv.json.Pathway.noMatchStream;

record Leg(String name, Path next) implements Path {

    @Override
    public Stream<Pathway> through(JsonNode main, List<String> trace) {
        return main.hasNonNull(name)
            ? next.through(main.get(name), addTo(trace, name))
            : noMatchStream(main, trace);
    }
}
