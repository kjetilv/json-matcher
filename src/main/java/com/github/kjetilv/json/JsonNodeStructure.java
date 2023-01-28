package com.github.kjetilv.json;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

public final class JsonNodeStructure implements Structure<JsonNode> {

    @Override
    public boolean isObject(JsonNode object) {
        return object.isObject();
    }

    @Override
    public boolean isArray(JsonNode array) {
        return array.isArray();
    }

    @Override
    public Optional<JsonNode> get(JsonNode object, String field) {
        return Optional.ofNullable(object.get(field)).filter(jsonNode -> !jsonNode.isNull());
    }

    @Override
    public Stream<JsonNode> arrayElements(JsonNode array) {
        return stream(array::elements);
    }

    @Override
    public Stream<Map.Entry<String, JsonNode>> namedFields(JsonNode object) {
        return stream(object::fields);
    }

    private static <T> Stream<T> stream(Iterable<T> elements) {
        return StreamSupport.stream(elements.spliterator(), false);
    }
}
