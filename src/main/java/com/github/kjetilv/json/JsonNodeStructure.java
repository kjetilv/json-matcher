package com.github.kjetilv.json;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        return isNull(object) ? Stream.empty() : stream(object::fields);
    }

    @Override
    public JsonNode toObject(Map<String, JsonNode> map) {
        ObjectNode node = FACTORY.objectNode();
        map.forEach(node::set);
        return node;
    }

    @Override
    public JsonNode toArray(Collection<JsonNode> values) {
        ArrayNode node = FACTORY.arrayNode();
        values.forEach(node::add);
        return node;
    }

    @Override
    public JsonNode combine(JsonNode one, JsonNode two) {
        try {
            return OBJECT_MAPPER.readerForUpdating(one).readValue(two);
        } catch (IOException e) {
            throw new IllegalStateException("Could not combine: " + one + " / " + two, e);
        }
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final JsonNodeFactory FACTORY = new JsonNodeFactory(true);

    private static Predicate<JsonNode> notNull() {
        return node -> !node.isNull();
    }

    private static <T> Stream<T> stream(Iterable<T> elements) {
        return elements != null && elements.iterator().hasNext()
            ? StreamSupport.stream(elements.spliterator(), false)
            : Stream.empty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[]";
    }
}
