package com.github.kjetilv.json;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonExtractorTest {

    private StructureExtractor<JsonNode> subsetMatcher;

    @Test
    void canExtractSimple() {
        JsonNode main = JsonDings.json(
            """
            {
              "foo": {
                "bar": {
                  "zot": 42,
                  "arrs": [{"key": "a", "val": 1}, {"key": "b", "val":2}]
                },
                "zip": true
              },
              "qos": 1.5
            }
            """);
        DefaultStructureMatcher<JsonNode> structureMatcher =
            matcher(main);

        StructureExtractor<JsonNode> extractor = structureMatcher;
        StructureMatcher<JsonNode> matcher = structureMatcher;

        JsonNode mask = JsonDings.json(
            """
            {
              "foo": {
                "bar": 43,
                "arrs": [{"key": "a", "val": 2}]
              },
              "qos": []
            }
            """
        );
        assertThat(extractor.subset(mask)).hasValueSatisfying(subset -> {
            assertThat(matcher.contains(subset));
            Match match = matcher(subset).match(mask);
            match.pathways().forEach(System.out::println);
        });
    }

    private static DefaultStructureMatcher<JsonNode> matcher(JsonNode main) {
        return new DefaultStructureMatcher<>(main, new JsonNodeStructure(), StructureMatchers.ArrayStrategy.EXACT);
    }
}
