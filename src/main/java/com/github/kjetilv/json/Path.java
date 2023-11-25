package com.github.kjetilv.json;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.kjetilv.json.DeadLeaf.deadEnd;

sealed interface Path<T> {

    default Stream<Probe<T>> probe(T main) {
        return probe(main, null);
    }

    Stream<Probe<T>> probe(T main, List<String> trace);

    Optional<Extract<T>> extract(T main);

    record Destination<T>(T expected) implements Path<T> {

        @Override
        public Stream<Probe<T>> probe(T main, List<String> trace) {
            return Optional.ofNullable(main)
                .filter(expected::equals)
                .map(equal -> found(expected, trace))
                .or(() -> unexpected(main, expected, trace))
                .stream();
        }

        @Override
        public Optional<Extract<T>> extract(T main) {
            return Optional.ofNullable(main)
                .map(value -> () -> value);
        }

        private static <T> Probe<T> found(T expected, List<String> trace) {
            return new FoundLeaf<>(expected, trace);
        }

        private static <T> Optional<Probe<T>> unexpected(T main, T expected, List<String> trace) {
            return Optional.of(new DeadLeaf<>(main, expected, trace));
        }
    }

    record ExactMatches<T>(List<Path<T>> paths, Structure<T> structure) implements Path<T> {

        @Override
        public Stream<Probe<T>> probe(T main, List<String> trace) {
            return DefaultStructureMatcher.exactPaths(
                trace,
                structure.listElements(main),
                paths
            );
        }

        @Override
        public Optional<Extract<T>> extract(T main) {
            return Optional.ofNullable(main)
                .map(value -> () -> value);
        }
    }

    record Subset<T>(Path<T> path, Structure<T> structure) implements Path<T> {

        @Override
        public Stream<Probe<T>> probe(T main, List<String> trace) {
            return Stream.of(structure.arrayElements(main)
                .flatMap(node -> path.probe(node, trace))
                .filter(Probe::found)
                .findFirst()
                .orElseGet(() -> new DeadLeaf<>(main, null, trace)));
        }

        @Override
        public Optional<Extract<T>> extract(T main) {
            return Optional.ofNullable(main)
                .map(value -> () -> value);
        }
    }

    record ExactObject<T>(List<ObjectField<T>> objectFields, Structure<T> structure) implements Path<T> {

        @Override
        public Stream<Probe<T>> probe(T main, List<String> trace) {
            List<Probe<T>> probes = objectFields.stream()
                .flatMap(objectField ->
                    objectField.probe(main, trace))
                .toList();
            return Stream.of(new FoundNode<>(probes, trace));
        }

        @Override
        public Optional<Extract<T>> extract(T main) {
            return Optional.of(
                    Maps.toMap(objectFields.stream()
                        .flatMap(objectField ->
                            objectField.extract(main)
                                .map(Extract::value)
                                .map(t -> Map.entry(objectField.name(), t))
                                .stream())))
                .filter(map -> !map.isEmpty())
                .map(map -> () -> structure.toObject(map));
        }
    }

    record ObjectField<T>(String name, Path<T> next, Structure<T> structure) implements Path<T> {

        @Override
        public Stream<Probe<T>> probe(T main, List<String> trace) {
            return structure.get(main, name)
                .map(field -> next.probe(field, addTo(trace, name)))
                .orElseGet(() -> Stream.of(deadEnd(main, trace)));
        }

        @Override
        public Optional<Extract<T>> extract(T main) {
            return structure.get(main, name).flatMap(next::extract);
        }

        private static List<String> addTo(List<String> list, String added) {
            return list == null
                ? Collections.singletonList(added)
                : Stream.concat(list.stream(), Stream.of(added))
                    .toList();
        }
    }

    record Subsequence<T>(List<Path<T>> paths, Structure<T> structure) implements Path<T> {

        @Override
        public Stream<Probe<T>> probe(T main, List<String> trace) {
            List<T> mainElements = structure.listElements(main);
            if (mainElements.size() < paths.size()) {
                return Stream.of(deadEnd(main, trace));
            }
            OptionalInt firstMatch = IntStream.range(0, mainElements.size())
                .filter(i ->
                    paths.get(0).probe(mainElements.get(i), trace)
                        .allMatch(Probe::found))
                .findFirst();
            return firstMatch.isEmpty()
                ? Stream.of(deadEnd(main, trace))
                : DefaultStructureMatcher.exactPaths(
                    trace,
                    subsequence(firstMatch.getAsInt(), mainElements),
                    paths
                );
        }

        @Override
        public Optional<Extract<T>> extract(T main) {
            return Optional.ofNullable(main)
                .map(value -> () -> value);
        }

        private List<T> subsequence(int startIndex, List<T> nodes) {
            return nodes.subList(
                startIndex,
                Math.min(nodes.size(), startIndex + paths.size())
            );
        }
    }
}
