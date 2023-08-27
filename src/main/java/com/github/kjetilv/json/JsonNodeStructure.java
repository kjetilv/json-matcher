package com.github.kjetilv.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class JsonNodeStructure implements Structure<JsonNode> {

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
            .filter(notNull());
    }

    @Override
    public Stream<JsonNode> arrayElements(JsonNode array) {
        if (isNull(array)) {
            return Stream.empty();
        }
        return stream(array::elements);
    }

    @Override
    public Stream<Map.Entry<String, JsonNode>> namedFields(JsonNode object) {
        return isNull(object)
            ? Stream.empty()
            : stream(object::fields);
    }

    @Override
    public JsonNode toObject(Map<String, ?> map) {
        return OBJECT_MAPPER.convertValue(map, JsonNode.class);
    }

    @Override
    public JsonNode combine(JsonNode one, JsonNode two) {
        if (isObject(one) && isObject(two)) {
            Map<?, ?> mapOne = OBJECT_MAPPER.convertValue(one, Map.class);
            Map<?, ?> mapTwo = OBJECT_MAPPER.convertValue(two, Map.class);
            Map<?, ?> combine = Utils.combine(mapOne, mapTwo);
            return OBJECT_MAPPER.convertValue(combine, JsonNode.class);
        }
        if (isArray(one) && isArray(two)) {
            List<?> listOne = OBJECT_MAPPER.convertValue(one, List.class);
            List<?> listTwo = OBJECT_MAPPER.convertValue(two, List.class);
            List<?> combine = Utils.combine(listOne, listTwo);
            return OBJECT_MAPPER.convertValue(combine, JsonNode.class);
        }
        throw new IllegalStateException("Could not combine: " + one + " / " + two);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[]";
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static Predicate<JsonNode> notNull() {
        return node -> !node.isNull();
    }

    private static <T> Stream<T> stream(Iterable<T> elements) {
        return elements != null && elements.iterator().hasNext()
            ? StreamSupport.stream(elements.spliterator(), false)
            : Stream.empty();
    }
}
