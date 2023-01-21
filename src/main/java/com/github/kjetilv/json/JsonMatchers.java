package com.github.kjetilv.json;

import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings("unused")
public final class JsonMatchers {

    public static JsonMatcher node(JsonNode jsonNode) {
        return node(jsonNode, null);
    }

    public static JsonMatcher node(JsonNode jsonNode, ArrayStrategy arrayStrategy) {
        return new DefaultJsonMatcher(jsonNode, arrayStrategy);
    }

    public enum ArrayStrategy {

        SUBSET, SUBSEQ, EXACT
    }

    private JsonMatchers() {
    }
}
