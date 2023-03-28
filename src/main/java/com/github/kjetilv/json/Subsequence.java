package com.github.kjetilv.json;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

record Subsequence<T>(List<Path<T>> paths, Structure<T> structure) implements Path<T> {

    @Override
    public Optional<Extract<T>> extract(T main) {
        return Optional.ofNullable(main).map(value -> () -> value);
    }

    @Override
    public Stream<Probe> probe(T main, List<String> trace) {
        List<T> mainElements = structure.arrayElements(main).toList();
        if (mainElements.size() < paths.size()) {
            return Stream.of(DeadEnd.deadEnd(main, trace));
        }
        OptionalInt firstMatch = IntStream.range(0, mainElements.size())
            .filter(i ->
                paths.get(0).probe(mainElements.get(i), trace)
                    .allMatch(Probe::found))
            .findFirst();
        if (firstMatch.isEmpty()) {
            return Stream.of(DeadEnd.deadEnd(main, trace));
        }
        List<T> subsequence = subsequence(firstMatch.getAsInt(), mainElements);
        return DefaultStructureMatcher.exactPaths(trace, subsequence, paths);
    }

    private List<T> subsequence(int startIndex, List<T> nodes) {
        return nodes.subList(
            startIndex,
            Math.min(nodes.size(), startIndex + paths.size()));
    }
}
