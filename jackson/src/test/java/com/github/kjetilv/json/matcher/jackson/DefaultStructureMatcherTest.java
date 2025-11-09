package com.github.kjetilv.json.matcher.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kjetilv.json.matcher.core.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

class DefaultStructureMatcherTest {

    @Test
    void diff1() {
        var json1 = JsonDings.json(
            //language=json
            """
                {
                  "foo": "bar",
                  "zot": true
                }
                """);
        var json2 = JsonDings.json(
            //language=json
            """
                {
                  "foo": 42,
                  "zot": true
                }
                """);

        StructureDiffer<JsonNode> differ = Structures.differ(json1, new JacksonStructure());
        Map<Pointer<JsonNode>, Diff<JsonNode>> diff = differ.subdiff(json2);
        System.out.println(diff);
        differ.diff(json2).ifPresent(System.out::println);
    }
}