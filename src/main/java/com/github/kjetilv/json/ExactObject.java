package com.github.kjetilv.json;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record ExactObject<T>(List<ObjectField<T>> objectFields, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Probe> probe(T main, List<String> trace) {
        return Stream.of(
            new FoundNode<>(
                objectFields.stream().flatMap(objectField ->
                        objectField.probe(main, trace))
                    .toList(),
                trace));
    }

    @Override
    public Optional<Extract<T>> extract(T main) {
        return Optional.of(objectFields.stream()
                .flatMap(objectField ->
                    objectField.extract(main)
                        .map(Extract::value)
                        .map(t -> Map.entry(objectField.name(), t)).stream())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (t1, t2) -> {
                        throw new IllegalStateException("Failed to combine: " + t1 + " / " + t2);
                    },
                    LinkedHashMap::new
                )))
            .filter(map1 -> !map1.isEmpty())
            .map(map ->
                () -> structure.toObject(map));
    }
}
