package com.github.kjetilv.json;

import java.util.Objects;
import java.util.stream.Stream;

record DefaultStructureMatcher<T>(T main, Structure<T> structure, StructureMatchers.ArrayStrategy arrayStrategy)
    implements StructureMatcher<T> {

    DefaultStructureMatcher(T main, Structure<T> structure, StructureMatchers.ArrayStrategy arrayStrategy) {
        this.main = Objects.requireNonNull(main, "main");
        this.structure = Objects.requireNonNull(structure, "structure");
        this.arrayStrategy = arrayStrategy == null ? StructureMatchers.ArrayStrategy.SUBSET : arrayStrategy;
    }

    @Override
    public boolean contains(T part) {
        Stream<Pathway<T>> pathwayStream = pathsIn(part)
            .flatMap(path ->
                path.through(main));
        return new PathsMatch<>(pathwayStream.toList()).matches();
    }

    private Stream<Path<T>> pathsIn(T part) {
        if (structure.isObject(part)) {
            return structure.mapNamedFields(
                part,
                (name, subNode) ->
                    pathsIn(subNode).map(path ->
                        new Leg<>(name, path, structure)));
        }
        if (structure.isArray(part)) {
            return navigate(structure.mapArrayElements(part, this::pathsIn));
        }
        return Stream.of(new Destination<>(part));
    }

    private Stream<Path<T>> navigate(Stream<Path<T>> arrayParts) {
        return switch (arrayStrategy) {
            case EXACT -> Stream.of(new Array<T>(arrayParts.toList(), structure));
            case SUBSEQ -> Stream.of(new SubArray<T>(arrayParts.toList(), structure));
            case SUBSET -> arrayParts.map(path -> new Fork<T>(path, structure));
        };
    }
}
