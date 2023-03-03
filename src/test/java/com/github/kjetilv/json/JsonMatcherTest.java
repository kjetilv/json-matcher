package com.github.kjetilv.json;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SameParameterValue")
class JsonMatcherTest {

    private StructureMatcher<JsonNode> subsetMatcher;

    private StructureMatcher<JsonNode> exactMatcher;

    @BeforeEach
    void setUp() {
        JsonNode json = json(JSON);
        subsetMatcher = StructureMatchers.node(
            json,
            new JsonNodeStructure(),
            StructureMatchers.ArrayStrategy.SUBSET);
        exactMatcher = StructureMatchers.node(
            json,
            new JsonNodeStructure(),
            StructureMatchers.ArrayStrategy.EXACT);
    }

    @AfterEach
    void tearDown() {
        subsetMatcher = null;
        exactMatcher = null;
    }

    @Test
    void simpleSubsetIsPart() {
        assertPart(
            subsetMatcher,
            """
            {
              "foo":
              {
                "bar": 4
              }
            }
            """
        );
    }

    @Test
    void simpleDeviatingSubsetIsNotPart() {
        assertNotPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 5
              }
            }
            """
        );
    }

    @Test
    void explicitNullIsNotPart() {
        assertNotPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": null
              }
            }
            """
        );
    }

    @Test
    void notPartIfArrayIsDifferent() {
        assertNotPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                  "zips": [
                    { "argh": [7, 8] }
                  ]
                }
              }
            }
            """);
        assertNotPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                }
              },
              "arr2": [ true, "ouch" ]
            }
            """);
    }

    @Test
    void notPartIfArrayIsActuallyObject() {
        assertNotPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                  "zips": [
                    { "argh": { "foo": 5, "bar": 6 }}
                  ]
                }
              }
            }
            """);
        assertNotPart(
            subsetMatcher,
            """
            {
              "arr2": { "itsATrick": true, "reaction": "dip" }
            }
            """);
    }

    @Test
    void notPartFieldsDontMatchObject() {
        assertNotPart(
            StructureMatchers.node(
                json(
                    """
                      {
                      "arr": [
                        {
                        "foo": 1,
                        "bar": 2
                        },
                        {
                        "foo": 3,
                        "bar": 4
                        }
                      ]
                    }"""),
                new JsonNodeStructure(),
                StructureMatchers.ArrayStrategy.SUBSET),
            """
            {
              "arr": [
              {
                "foo": 1,
                "bar": 4
              }
              ]
            }            
            """);
    }

    @Test
    void isPartIfArrayIsSubset() {
        assertPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                  "zips": [
                    { "rarg": [3]}
                  ]
                }
              }
            }
            """);
    }

    @Test
    void isPartIfArrayIsExactMatch() {
        assertPart(
            exactMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                  "zips": [
                    { "argh": [ 4, 5, 6 ]},
                    { "rarg": [ 3, 2, 1 ]}
                  ]
                }
              }
            }
            """);
    }

    @Test
    void isNotPartIfArrayIsNotExactMatch() {
        StructureMatcher<JsonNode> matcher = StructureMatchers.node(
            json(
                """
                {
                  "foo": [ 1, 2, 3 ]
                }
                """),
            new JsonNodeStructure(),
            StructureMatchers.ArrayStrategy.EXACT);
        assertNotPart(
            matcher,
            """
            { "foo": [ 0, 1, 2, 3 ] }
            """);
        assertNotPart(
            matcher,
            """
            { "foo": [ 1, 3, 2 ] }
            """);
        assertNotPart(
            matcher,
            """
            { "foo": [ 1, 2, 4 ] }
            """);
        assertNotPart(
            matcher,
            """
            { "foo": [ 1, 2, 3, 4 ] }
            """);
        assertNotPart(
            matcher,
            """
            { "foo": [ 0, 1, 2, 3, 4 ] }
            """);
        assertPart(
            matcher,
            """
            { "foo": [ 1, 2, 3 ] }
            """);
        assertNotPart(
            matcher,
            """
            { "foo": [ 1, 2 ] }
            """);
    }

    @Test
    void isNotPartIfArrayIsNotSubsequence() {
        StructureMatcher<JsonNode> matcher = StructureMatchers.node(
            json(
                """
                {
                  "foo": [ 1, 2, 3, 4, 5 ]
                }
                """),
            new JsonNodeStructure(),
            StructureMatchers.ArrayStrategy.SUBSEQ);
        assertNotPart(
            matcher,
            """
            { "foo": [ 0, 1, 2, 3 ] }
            """);
        assertNotPart(
            matcher,
            """
            { "foo": [ 1, 3, 2 ] }
            """);
        assertNotPart(
            matcher,
            """
            { "foo": [ 1, 2, 4 ] }
            """);
        assertNotPart(
            matcher,
            """
            { "foo": [ 1, 2, 3, 5 ] }
            """);
        assertNotPart(
            matcher,
            """
            { "foo": [ 0, 1, 2, 3, 4 ] }
            """);
        List.of(
            "1",
            "1, 2",
            "1, 2, 3",
            "1, 2, 3, 4, 5",
            "2, 3, 4, 5",
            "2, 3, 4",
            "2, 3",
            "2",
            "3, 4, 5",
            "3, 4",
            "3",
            "4",
            "4, 5",
            "5"
        ).forEach(part ->
            assertPart(
                matcher, "{ \"foo\": [" + part + "]}"
            ));
        assertPart(
            matcher,
            """
            { "foo": [ 1, 2, 3 ] }
            """);
    }

    @Test
    void isPartIfArrayIsSubsetRegardlessOfOrder() {
        assertPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                  "zips": [
                    { "argh": [6, 5]}
                  ]
                }
              }
            }
            """);
    }

    @Test
    void isPartIfArrayIsSubsetRegardlessOfArity() {
        assertPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                  "zips": [
                    { "argh": [6, 5, 5, 5, 5]}
                  ]
                }
              }
            }
            """);
    }

    @Test
    void notPartIfArrayHasAdditionalElements() {
        assertNotPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                  "zips": [
                    { "argh": [3, 4, 5, 6]}
                  ]
                }
              }
            }
            """);
    }

    @Test
    void notPartIfArrayHasExplicityNulls() {
        assertPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                  "zips": [
                    { "argh": [4]}
                  ]
                }
              }
            }
            """);
        assertNotPart(
            subsetMatcher,
            """
            {
              "foo": {
                "bar": 4,
                "zot": {
                  "zips": [
                    { "argh": [null]}
                  ]
                }
              }
            }
            """);
    }

    @Test
    void deviatingPathsThroughListsAreReturned() {
        assertPart(
            subsetMatcher,
            """
            {
              "departments": [
                {
                  "tech": {
                    "employees": [
                       { "name": "Harry" }
                    ]
                  }
                }
              ]
            }
            """);
        assertNotPart(
            subsetMatcher,
            """
            {
              "departments": [
                {
                  "sales": {
                    "employees": [
                       { "name": "Harry" }
                    ]
                  }
                }
              ]
            }
            """);
    }

    @Test
    void arrayTest() {
        assertNotPart(subsetMatcher, "[3]");
    }

    private static final String JSON =
        """
        {
          "foo": {
            "bar": 4,
            "zot": {
              "zip": 45.4,
              "zips": [
               { "argh": [ 4, 5, 6 ]},
               { "rarg": [ 3, 2, 1 ]}
              ]
            }
          },
          "arr2": [ "dip", 5, true ],
          "departments": [
            {
                "tech": {
                  "employees": [
                     { "name": "Harry", "salary": 4.5 },
                     { "name": "Sally", "salary": 5.5 }
                  ]
              }
            },
            {
                "sales": {
                  "employees": [
                    { "name": "Dumb", "salary": 10.5 },
                    { "name": "Dumber", "salary": 11.5 }
                  ]
                }
            }
          ]
        }
        """;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static void assertNotPart(StructureMatcher<JsonNode> matcher, String content) {
        assertThat(matcher.contains(json(content)))
            .describedAs("Should not be part of: " + matcher + "\n subset : " + content)
            .isFalse();
    }

    private static void assertPart(StructureMatcher<JsonNode> matcher, String content) {
        assertThat(matcher.contains(json(content)))
            .describedAs("Should be a part: " + content)
            .isTrue();
    }

    private static JsonNode json(String content) {
        try {
            return OBJECT_MAPPER.readTree(content);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse: " + content, e);
        }
    }
}
