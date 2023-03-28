package com.github.kjetilv.json;

import com.fasterxml.jackson.databind.JsonNode;
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

        DefaultStructureMatcher<JsonNode> structureMatcher = matcher(main);
        StructureExtractor<JsonNode> extractor = structureMatcher;
        StructureMatcher<JsonNode> matcher = structureMatcher;

        JsonNode mask = JsonDings.json(
            """
            {
              "foo": {
                "bar": {
                  "zot": 43,
                  "arrs": ["x"]
                }
              },
              "qos": []
            }
            """
        );
        assertThat(extractor.subset(mask)).hasValueSatisfying(subset -> {
            assertThat(matcher.contains(subset));
            Match match = matcher(subset).match(mask);
            System.out.println("\nPathways\n");
            match.pathways().forEach(System.out::println);
            System.out.println("\nJSON\n");
            System.out.println(main);
            System.out.println(subset);
            System.out.println(mask);
            System.out.println("\nLeaves\n");
            match.leaves().forEach(System.out::println);
        });
    }

    private static DefaultStructureMatcher<JsonNode> matcher(JsonNode main) {
        return new DefaultStructureMatcher<>(main, new JsonNodeStructure(), StructureMatchers.ArrayStrategy.EXACT);
    }
}
