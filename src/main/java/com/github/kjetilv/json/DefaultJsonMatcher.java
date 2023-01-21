package com.github.kjetilv.json;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

import static com.github.kjetilv.json.JsonUtils.mapArrayElements;
import static com.github.kjetilv.json.JsonUtils.mapNamedFields;

record DefaultJsonMatcher(JsonNode main, JsonMatchers.ArrayStrategy arrayStrategy) implements JsonMatcher {

    DefaultJsonMatcher(JsonNode main, JsonMatchers.ArrayStrategy arrayStrategy) {
        this.main = Objects.requireNonNull(main, "main");
        this.arrayStrategy = arrayStrategy == null ? JsonMatchers.ArrayStrategy.SUBSET : arrayStrategy;
    }

    @Override
    public boolean contains(JsonNode part) {
        return pathsIn(part)
            .flatMap(path ->
                path.through(main))
            .allMatch(Pathway::found);
    }

    private Stream<Path> pathsIn(JsonNode part) {
        if (part.isObject()) {
            return mapNamedFields(part, (name, subNode) ->
                pathsIn(subNode).map(path ->
                    new Leg(name, path)));
        }
        if (part.isArray()) {
            Stream<Path> arrayParts = mapArrayElements(part, this::pathsIn);
            return switch (arrayStrategy) {
                case EXACT -> Stream.of(new Array(arrayParts.toList()));
                case SUBSEQ -> Stream.of(new SubArray(arrayParts.toList()));
                case SUBSET -> arrayParts.map(Fork::new);
            };
        }
        return Stream.of(new Destination(part));
    }
}
