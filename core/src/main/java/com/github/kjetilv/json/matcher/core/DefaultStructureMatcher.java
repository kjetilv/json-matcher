package com.github.kjetilv.json.matcher.core;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

record DefaultStructureMatcher<T>(
    T main,
    Structure<T> str,
    Structures.ArrayStrategy arr
)
    implements StructureMatcher<T>, StructureExtractor<T>, StructureDiffer<T> {

    static <T> Stream<Probe<T>> exactPaths(
        List<String> trace, List<T> mainElements, List<Path<T>> paths
    ) {
        if (paths.size() < mainElements.size()) {
            return Stream.concat(
                matches(mainElements, paths, trace),
                mainElements.subList(paths.size(), mainElements.size())
                    .stream()
                    .map(element ->
                        DeadLeaf.deadEnd(element, trace))
            );
        }
        if (paths.size() <= mainElements.size()) {
            return matches(mainElements, paths, trace);
        }
        return Stream.concat(
            matches(mainElements, paths, trace),
            paths.subList(mainElements.size(), paths.size())
                .stream()
                .flatMap(path ->
                    path.probe(null, trace))
        );
    }

    DefaultStructureMatcher(T main, Structure<T> str, Structures.ArrayStrategy arr) {
        this.main = Objects.requireNonNull(main, "main");
        this.str = Objects.requireNonNull(str, "str");
        this.arr = arr == null ? Structures.ArrayStrategy.SUBSET : arr;
    }

    @Override
    public Match<T> match(T part) {
        return new PathsMatch<>(pathsIn(part)
            .flatMap(path -> path.probe(main))
            .toList());
    }

    @Override
    public Optional<T> extract(T mask) {
        return pathsIn(mask)
            .map(path -> path.extract(main))
            .flatMap(Optional::stream)
            .map(Extract::value)
            .reduce(str::combine);
    }

    @Override
    public Map<Pointer<T>, Diff<T>> subdiff(T subset) {
        return Maps.toMap(pointersIn(subset).sorted(Comparator.naturalOrder())
            .map(pointer ->
                Map.entry(pointer, pointer.get(subset)
                    .filter(value -> !str.isNull(value))
                    .map(value ->
                        new Diff<>(value, pointer.get(main).orElse(null)))))
            .filter(entry ->
                entry.getValue()
                    .filter(Diff::isDiff).isPresent())
            .map(entry ->
                Map.entry(entry.getKey(), entry.getValue().get())));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> diff(T subset) {
        return (Optional<T>) subdiff(subset).entrySet()
            .stream()
            .map(entry -> entry.getKey()
                .map(entry.getValue().found()))
            .reduce(Combine::objects)
            .map(Map.class::cast)
            .map(diff ->
                str.toObject(diff));
    }

    private Stream<Pointer<T>> pointersIn(T part) {
        if (str.isObject(part)) {
            if (str.namedFields(part).findAny().isEmpty()) {
                return empty();
            }
            return str.mapNamedFields(part, (name, subpart) ->
                pointersIn(subpart).map(pointer ->
                    new Pointer.Node<>(name, pointer, str)));
        }
        if (str.isArray(part)) {
            if (str.arrayElements(part).findAny().isEmpty()) {
                return empty();
            }
            AtomicInteger index = new AtomicInteger();
            Supplier<Integer> nextIndex = index::getAndIncrement;
            return str.mapArrayElements(part, element ->
                pointersIn(element).map(pointer ->
                    new Pointer.Array<>(nextIndex.get(), pointer, str)));
        }
        return empty();
    }

    private Stream<Path<T>> pathsIn(T part) {
        if (str.isObject(part)) {
            List<Path.ObjectField<T>> objectFields = str.mapNamedFields(part, (name, subpart) ->
                    pathsIn(subpart).map(path ->
                        new Path.ObjectField<>(name, path, str)))
                .toList();
            return Stream.of(new Path.ExactObject<>(objectFields, str));
        }
        if (str.isArray(part)) {
            Stream<Path<T>> arrayParts = str.mapArrayElements(part, this::pathsIn);
            return switch (arr) {
                case EXACT -> exact(arrayParts);
                case SUBSEQ -> subseq(arrayParts);
                case SUBSET -> subset(arrayParts);
            };
        }
        return Stream.of(new Path.Destination<>(part));
    }

    private Stream<Path<T>> exact(Stream<Path<T>> paths) {
        return Stream.of(new Path.ExactMatches<>(paths.toList(), str));
    }

    private Stream<Path<T>> subseq(Stream<Path<T>> paths) {
        return Stream.of(new Path.Subsequence<>(paths.toList(), str));
    }

    private Stream<Path<T>> subset(Stream<Path<T>> paths) {
        return paths.map(path -> new Path.Subset<>(path, str));
    }

    private static final Pointer.Leaf<?> EMPTY_LEAF = new Pointer.Leaf<>();

    private static <T> Stream<Pointer<T>> empty() {
        return Stream.of(emptyLeaf());
    }

    @SuppressWarnings("unchecked")
    private static <T> Pointer.Leaf<T> emptyLeaf() {
        return (Pointer.Leaf<T>) EMPTY_LEAF;
    }

    private static <T> Stream<Probe<T>> matches(List<T> mainElements, List<Path<T>> paths, List<String> trace) {
        return Zip.of(paths, mainElements)
            .map(Zip.Idxd::t)
            .flatMap(pathNode ->
                pathNode.p1().probe(pathNode.p2(), trace));
    }
}
