package com.github.kjetilv.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

final class JsonDings {

    static JsonNode json(String content) {
        try {
            return OBJECT_MAPPER.readTree(content);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse: " + content, e);
        }
    }

    private JsonDings() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
}
