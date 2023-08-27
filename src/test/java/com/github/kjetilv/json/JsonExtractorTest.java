package com.github.kjetilv.json;

import java.util.Map;

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
                  "arrs": [
                    {
                      "key": "a",
                      "val": 1
                    },
                    {
                      "key": "b",
                      "val":2
                    }
                  ]
                },
                "zip": true,
                "more": "stuff"
              },
              "qos": 1.5
            }
            """);

        StructureMatcher<JsonNode> structureMatcher = matcher(main);
        StructureExtractor<JsonNode> extractor = extractor(main);
        StructureMatcher<JsonNode> matcher = structureMatcher;
        StructureDiffer<JsonNode> differ = differ(main);

        JsonNode mask = JsonDings.json(
            """
            {
              "foo": {
                "bar": {
                  "zot": 43,
                  "arrs":
                    [
                      null,
                      {
                        "key": "a",
                        "val": 2
                      },
                      {
                      }
                    ]
                },
                "zip": true
              },
              "qos": 1.5
            }
            """
        );
        assertThat(extractor.extract(mask)).hasValueSatisfying(subset -> {
            assertThat(matcher.contains(subset)).isTrue();
            Match<JsonNode> match = matcher(subset).match(mask);
            System.out.println("\nPathways\n");
            match.pathways().forEach(System.out::println);
            System.out.println("\nStructure\n");
            System.out.println(main);
            System.out.println("\nMask\n");
            System.out.println(mask);
            System.out.println("\nSubset\n");
            System.out.println(subset);
            System.out.println("\nLeaves\n");
            match.leaves().forEach(System.out::println);
        });

        Map<Pointer<JsonNode>, Diff<JsonNode>> subdiff = differ.subdiff(mask);
//        System.out.println(subdiff);

        subdiff.forEach((jsonNodePointer, jsonNodeDiff) ->
            System.out.println(jsonNodePointer + " -> " + jsonNodeDiff));

        assertThat(differ.diff(mask)).hasValueSatisfying(System.out::println);
    }

    private static StructureExtractor<JsonNode> extractor(JsonNode main) {
        return Structures.extractor(main, new JsonNodeStructure());
    }

    private static StructureDiffer<JsonNode> differ(JsonNode main) {
        return Structures.differ(main, new JsonNodeStructure());
    }

    private static StructureMatcher<JsonNode> matcher(JsonNode main) {
        return Structures.matcher(main, new JsonNodeStructure(), Structures.ArrayStrategy.EXACT);
    }
}
