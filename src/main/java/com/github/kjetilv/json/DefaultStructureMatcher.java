package com.github.kjetilv.json;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

record DefaultStructureMatcher<T>(
    T main,
    Structure<T> str,
    StructureMatchers.ArrayStrategy arrayStrategy
)
    implements StructureMatcher<T>, StructureExtractor<T> {

    static <T> Stream<Probe> exactPaths(
        List<String> trace, List<T> mainElements, List<Path<T>> paths
    ) {
        if (paths.size() < mainElements.size()) {
            return Stream.concat(
                matches(mainElements, paths, trace),
                mainElements.subList(paths.size(), mainElements.size())
                    .stream()
                    .map(element ->
                        DeadEnd.deadEnd(element, trace)));
        }
        if (paths.size() <= mainElements.size()) {
            return matches(mainElements, paths, trace);
        }
        return Stream.concat(
            matches(mainElements, paths, trace),
            paths.subList(mainElements.size(), paths.size()).stream()
                .flatMap(path ->
                    path.probe(null, trace)));
    }

    private static <T> Stream<Probe> matches(List<T> mainElements, List<Path<T>> paths, List<String> trace) {
        return Zip.of(paths, mainElements).flatMap(pathAndNode ->
            pathAndNode.p1().probe(pathAndNode.p2(), trace));
    }

    DefaultStructureMatcher(T main, Structure<T> str, StructureMatchers.ArrayStrategy arrayStrategy) {
        this.main = Objects.requireNonNull(main, "main");
        this.str = Objects.requireNonNull(str, "str");
        this.arrayStrategy = arrayStrategy == null ? StructureMatchers.ArrayStrategy.SUBSET : arrayStrategy;
    }

    @Override
    public Match match(T part) {
        return new PathsMatch<>(pathsIn(part).flatMap(path -> path.probe(main)).toList());
    }

    @Override
    public Optional<T> subset(T mask) {
        Stream<Path<T>> pathStream = pathsIn(mask);
        return pathStream.map(path ->
                path.extract(main))
            .flatMap(Optional::stream)
            .map(Extract::value)
            .reduce(str::combine);
    }

    private Stream<Path<T>> pathsIn(T part) {
        if (str.isObject(part)) {
            return Stream.of(new ExactObject<>(
                str.mapNamedFields(
                    part,
                    (name, subNode) ->
                        pathsIn(subNode).map(path ->
                            new ObjectField<>(name, path, str))).toList(),
                str));
        }
        if (str.isArray(part)) {
            Stream<Path<T>> arrayParts = str.mapArrayElements(part, this::pathsIn);
            return switch (arrayStrategy) {
                case EXACT -> Stream.of(new ExactMatches<T>(arrayParts.toList(), str));
                case SUBSEQ -> Stream.of(new Subsequence<T>(arrayParts.toList(), str));
                case SUBSET -> arrayParts.map(path -> new Subset<T>(path, str));
            };
        }
        return Stream.of(new Destination<>(part));
    }
}
