package com.github.kjetilv.json;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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

    DefaultStructureMatcher(T main, Structure<T> str, Structures.ArrayStrategy arr) {
        this.main = Objects.requireNonNull(main, "main");
        this.str = Objects.requireNonNull(str, "str");
        this.arr = arr == null ? Structures.ArrayStrategy.SUBSET : arr;
    }

    @Override
    public Match<T> match(T part) {
        return new PathsMatch<>(pathsIn(part).flatMap(path ->
            path.probe(main)).toList());
    }

    @Override
    public Optional<T> extract(T mask) {
        Stream<Path<T>> pathStream = pathsIn(mask);
        return pathStream.map(path ->
                path.extract(main))
            .flatMap(Optional::stream)
            .map(Extract::value)
            .reduce(str::combine);
    }

    @Override
    public Map<Pointer<T>, Diff<T>> subdiff(T subset) {
        List<Pointer<T>> pointers = pointersIn(subset)
            .sorted(Comparator.naturalOrder())
            .toList();
        return pointers.stream()
            .map(pointer ->
                Map.entry(pointer, pointer.get(subset)
                    .filter(value -> !str.isNull(value))
                    .map(value ->
                        new Diff<>(value, pointer.get(main).orElse(null)))))
            .filter(entry ->
                entry.getValue().filter(Diff::isDiff).isPresent())
            .map(entry ->
                Map.entry(entry.getKey(), entry.getValue().get()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (o1, o2) -> {
                    throw new IllegalStateException("Cannot combine: " + o1 + " / " + o2);
                },
                LinkedHashMap::new
            ));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> diff(T subset) {
        Map<Pointer<T>, Diff<T>> subdiff = subdiff(subset);

        List<Object>
            list =
            subdiff.entrySet().stream().map(entry -> entry.getKey().map(entry.getValue().found())).toList();

        return (Optional<T>) list.stream().reduce(Maps::combine)
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
                pointersIn(subpart).map(pointer -> new Pointer.Node<>(name, pointer, str)));
        }
        if (str.isArray(part)) {
            if (str.arrayElements(part).findAny().isEmpty()) {
                return empty();
            }
            AtomicInteger index = new AtomicInteger();
            return str.mapArrayElements(part, element -> {
                int i = index.getAndIncrement();
                return pointersIn(element).map(pointer ->
                    new Pointer.Array<>(i, pointer, str));
            });
        }
        return empty();
    }

    private Stream<Path<T>> pathsIn(T part) {
        if (str.isObject(part)) {
            List<ObjectField<T>> objectFields = str.mapNamedFields(part, (name, subpart) ->
                pathsIn(subpart).map(path ->
                    new ObjectField<>(name, path, str))).toList();
            return Stream.of(new ExactObject<>(objectFields, str));
        }
        if (str.isArray(part)) {
            Stream<Path<T>> arrayParts = str.mapArrayElements(part, this::pathsIn);
            return switch (arr) {
                case EXACT -> Stream.of(new ExactMatches<T>(arrayParts.toList(), str));
                case SUBSEQ -> Stream.of(new Subsequence<T>(arrayParts.toList(), str));
                case SUBSET -> arrayParts.map(path -> new Subset<T>(path, str));
            };
        }
        return Stream.of(new Destination<>(part));
    }

    private static <T> Stream<Pointer<T>> empty() {
        return Stream.of(new Pointer.Leaf<>());
    }

    private static <T> Stream<Probe<T>> matches(List<T> mainElements, List<Path<T>> paths, List<String> trace) {
        return Zip.of(paths, mainElements)
            .map(Zip.Idxd::t)
            .flatMap(pathNode ->
                pathNode.p1().probe(pathNode.p2(), trace));
    }
}
