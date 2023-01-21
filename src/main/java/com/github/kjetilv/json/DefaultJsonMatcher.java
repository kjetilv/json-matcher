package com.github.kjetilv.json;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;

import static com.github.kjetilv.json.JsonUtils.addTo;
import static com.github.kjetilv.json.JsonUtils.arrayElements;
import static com.github.kjetilv.json.JsonUtils.mapArrayElements;
import static com.github.kjetilv.json.JsonUtils.mapNamedFields;

public record DefaultJsonMatcher(JsonNode main, ArrayStrategy arrayStrategy) implements JsonMatcher {

    public sealed interface Path {

        default Stream<Pathway> through(JsonNode main) {
            return through(main, null);
        }

        Stream<Pathway> through(JsonNode main, List<String> trace);
    }

    public DefaultJsonMatcher(JsonNode main) {
        this(main, null);
    }

    public DefaultJsonMatcher(JsonNode main, ArrayStrategy arrayStrategy) {
        this.main = Objects.requireNonNull(main, "main");
        this.arrayStrategy = arrayStrategy == null ? ArrayStrategy.SUBSET : arrayStrategy;
    }

    @Override
    public boolean contains(JsonNode part) {
        return pathsIn(part)
            .flatMap(path ->
                path.through(main))
            .allMatch(Pathway::found);
    }

    private Stream<Path> pathsIn(JsonNode part) {
        if (part.isObject()) {
            return mapNamedFields(part, (name, subNode) ->
                pathsIn(subNode).map(leg(name)));
        }
        if (part.isArray()) {
            Stream<Path> arrayParts = mapArrayElements(part, this::pathsIn);
            return switch (arrayStrategy) {
                case EXACT -> Stream.of(new Array(arrayParts.toList()));
                case SUBSEQ -> Stream.of(new SubArray(arrayParts.toList()));
                case SUBSET -> arrayParts.map(Fork::new);
            };
        }
        return Stream.of(new Destination(part));
    }

    private static Function<Path, Path> leg(String name) {
        return path -> new Leg(name, path);
    }

    private static Optional<Pathway> deadEndOption(JsonNode main, List<String> trace) {
        return deadEnd(main, trace).findFirst();
    }

    private static Stream<Pathway> deadEnd(JsonNode main, List<String> trace) {
        return Stream.of(new Pathway(null, main, trace));
    }

    private static Stream<Pathway> exactPaths(
        JsonNode main, List<String> trace, List<JsonNode> mainElements,
        List<Path> paths1
    ) {
        if (mainElements.size() != paths1.size()) {
            return deadEnd(main, trace);
        }
        return Zip.of(paths1, mainElements)
            .flatMap(xy ->
                xy.x().through(xy.y()));
    }

    private record Leg(String name, Path next) implements Path {

        @Override
        public Stream<Pathway> through(JsonNode main, List<String> trace) {
            return main.hasNonNull(name)
                ? next.through(main.get(name), addTo(trace, name))
                : deadEnd(main, trace);
        }
    }

    private record SubArray(List<Path> paths) implements Path {

        @Override
        public Stream<Pathway> through(JsonNode main, List<String> trace) {
            List<JsonNode> mainElements = arrayElements(main).toList();
            if (mainElements.size() < paths.size()) {
                return deadEnd(main, trace);
            }
            OptionalInt firstMatch = IntStream.range(0, mainElements.size())
                .filter(i ->
                    paths.get(0).through(mainElements.get(i)).allMatch(Pathway::found))
                .findFirst();
            if (firstMatch.isEmpty()) {
                return deadEnd(main, trace);
            }
            return exactPaths(
                main,
                trace,
                subsequence(firstMatch.getAsInt(), mainElements),
                paths);
        }

        private List<JsonNode> subsequence(int startIndex, List<JsonNode> nodes) {
            return nodes.subList(
                startIndex,
                Math.min(nodes.size(), startIndex + paths.size()));
        }
    }

    private record Array(List<Path> paths) implements Path {

        @Override
        public Stream<Pathway> through(JsonNode main, List<String> trace) {
            return exactPaths(main, trace, arrayElements(main).toList(), paths);
        }
    }

    private record Fork(Path path) implements Path {

        @Override
        public Stream<Pathway> through(JsonNode main, List<String> trace) {
            Stream<JsonNode> mains = arrayElements(main);
            return mains.flatMap(node ->
                    path.through(node, trace))
                .filter(Pathway::found)
                .findFirst().or(() ->
                    deadEndOption(main, trace))
                .stream();
        }
    }

    private record Destination(JsonNode expected) implements Path {

        @Override
        public Stream<Pathway> through(JsonNode main, List<String> trace) {
            return Stream.of(new Pathway(main, expected, trace));
        }
    }

    public record Pathway(JsonNode main, JsonNode expected, List<String> trace) {

        public boolean found() {
            return Objects.equals(main, expected);
        }
    }
}
