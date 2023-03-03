package com.github.kjetilv.json;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

record DefaultStructureMatcher<T>(
    T main, Structure<T> structure, StructureMatchers.ArrayStrategy arrayStrategy) implements StructureMatcher<T> {

    static <T> Stream<Search> exactPaths(
        T main, List<String> trace, List<T> mainElements, List<Path<T>> paths
    ) {
        Stream<Search>
            matches =
            Zip.of(paths, mainElements)
                .flatMap((Zip.Pair<Path<T>, T> pathAndNode) -> pathAndNode.p1().through(pathAndNode.p2()));
        if (paths.size() < mainElements.size()) {
            return Stream.concat(
                matches,
                mainElements.subList(paths.size(), mainElements.size())
                    .stream()
                    .map(element -> DeadEnd.deadEnd(element, trace)));
        }
        if (paths.size() <= mainElements.size()) {
            return matches;
        }
        return Stream.concat(
            matches,
            paths.subList(mainElements.size(), paths.size()).stream().flatMap(path -> path.through(null)));
    }

    DefaultStructureMatcher(T main, Structure<T> structure, StructureMatchers.ArrayStrategy arrayStrategy) {
        this.main = Objects.requireNonNull(main, "main");
        this.structure = Objects.requireNonNull(structure, "structure");
        this.arrayStrategy = arrayStrategy == null ? StructureMatchers.ArrayStrategy.SUBSET : arrayStrategy;
    }

    @Override
    public boolean contains(T part) {
        return new PathsMatch<>(pathsIn(part).flatMap(path -> path.through(main)).toList()).matches();
    }

    @Override
    public T subset(T part) {
        return null;
    }

    private Stream<Path<T>> pathsIn(T part) {
        if (structure.isObject(part)) {
            return Stream.of(new Legs<>(
                structure.mapNamedFields(
                    part,
                    (name, subNode) -> pathsIn(subNode).map(path -> new Leg<>(name, path, structure))).toList(),
                structure));
        }
        if (structure.isArray(part)) {
            return navigate(structure.mapArrayElements(part, this::pathsIn));
        }
        return Stream.of(new Destination<>(part));
    }

    private Stream<Path<T>> navigate(Stream<Path<T>> arrayParts) {
        return switch (arrayStrategy) {
            case EXACT -> Stream.of(new ExactArray<T>(arrayParts.toList(), structure));
            case SUBSEQ -> Stream.of(new SubArray<T>(arrayParts.toList(), structure));
            case SUBSET -> arrayParts.map(path -> new Fork<T>(path, structure));
        };
    }
}
