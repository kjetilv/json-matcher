package com.github.kjetilv.json;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

public final class JsonNodeStructure implements Structure<JsonNode> {

    @Override
    public boolean isObject(JsonNode object) {
        return object != null && object.isObject();
    }

    @Override
    public boolean isArray(JsonNode array) {
        return array != null && array.isArray();
    }

    @Override
    public Optional<JsonNode> get(JsonNode object, String field) {
        return Optional.ofNullable(object)
            .filter(node -> !node.isNull())
            .map(node -> node.get(field))
            .filter(jsonNode -> !jsonNode.isNull());
    }

    @Override
    public Stream<JsonNode> arrayElements(JsonNode array) {
        return array == null || array.isNull() ? Stream.empty() : stream(array::elements);
    }

    @Override
    public Stream<Map.Entry<String, JsonNode>> namedFields(JsonNode object) {
        return object == null || object.isNull() ? Stream.empty() : stream(object::fields);
    }

    private static <T> Stream<T> stream(Iterable<T> elements) {
        return elements != null && elements.iterator().hasNext()
            ? StreamSupport.stream(elements.spliterator(), false)
            : Stream.empty();
    }
}
