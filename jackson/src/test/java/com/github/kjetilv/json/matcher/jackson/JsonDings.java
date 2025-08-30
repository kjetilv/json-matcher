package com.github.kjetilv.json.matcher.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

final class JsonDings {

    public static String write(Object json) {
        try {
            return OBJECT_MAPPER.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to write: " + json, e);
        }
    }

    static JsonNode json(
        //language=json
        String content
    ) {
        try {
            return OBJECT_MAPPER.readTree(content);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse: " + content, e);
        }
    }
    static Object map(String content) {
        try {
            return OBJECT_MAPPER.readerFor(Map.class).readValue(content);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse: " + content, e);
        }
    }

    private JsonDings() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
}
