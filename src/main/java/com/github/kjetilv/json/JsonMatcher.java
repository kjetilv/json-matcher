package com.github.kjetilv.json;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface JsonMatcher {

    boolean contains(JsonNode part);
}
