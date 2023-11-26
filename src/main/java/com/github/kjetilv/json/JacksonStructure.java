package com.github.kjetilv.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class JacksonStructure implements Structure<JsonNode> {

    @Override
    public boolean isNull(JsonNode node) {
        return node == null || node.isNull();
    }

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
        if (isNull(object)) {
            return Optional.empty();
        }
        return Optional.ofNullable(object.get(field))
            .filter(node -> !node.isNull());
    }

    @Override
    public Stream<JsonNode> arrayElements(JsonNode array) {
        return isNull(array) ? Stream.empty() : stream(array::elements);
    }

    @Override
    public Stream<Map.Entry<String, JsonNode>> namedFields(JsonNode object) {
        return isNull(object) ? Stream.empty() : stream(object::fields);
    }

    @Override
    public JsonNode toObject(Map<String, ?> map) {
        return OBJECT_MAPPER.convertValue(map, JsonNode.class);
    }

    @Override
    public JsonNode combine(JsonNode one, JsonNode two) {
        if (isObject(one) && isObject(two)) {
            return OBJECT_MAPPER.convertValue(
                Combine.maps(
                    OBJECT_MAPPER.convertValue(one, Map.class),
                    OBJECT_MAPPER.convertValue(two, Map.class)
                ),
                JsonNode.class
            );
        }
        if (isArray(one) && isArray(two)) {
            return OBJECT_MAPPER.convertValue(
                Combine.lists(
                    OBJECT_MAPPER.convertValue(one, List.class),
                    OBJECT_MAPPER.convertValue(two, List.class)
                ),
                JsonNode.class
            );
        }
        throw new IllegalStateException("Could not combine: " + one + " / " + two);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[]";
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static <T> Stream<T> stream(Iterable<T> elements) {
        return elements != null && elements.iterator().hasNext()
            ? StreamSupport.stream(elements.spliterator(), false)
            : Stream.empty();
    }
}
