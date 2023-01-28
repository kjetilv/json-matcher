package com.github.kjetilv.json;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

record SubArray<T>(List<Path<T>> paths, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Pathway<T>> through(T main, List<String> trace) {
        List<T> mainElements = structure.arrayElements(main).toList();
        if (mainElements.size() < paths.size()) {
            return Stream.of(Pathway.deadEnd(main, trace));
        }
        OptionalInt firstMatch = IntStream.range(0, mainElements.size())
            .filter(i ->
                paths.get(0).through(mainElements.get(i))
                    .allMatch(Pathway::found))
            .findFirst();
        if (firstMatch.isEmpty()) {
            return Stream.of(Pathway.deadEnd(main, trace));
        }
        List<T> subsequence = subsequence(firstMatch.getAsInt(), mainElements);
        return Pathway.exactPaths(main, trace, subsequence, paths);
    }

    private List<T> subsequence(int startIndex, List<T> nodes) {
        return nodes.subList(
            startIndex,
            Math.min(nodes.size(), startIndex + paths.size()));
    }
}
