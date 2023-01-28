package com.github.kjetilv.json;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Structure<T> {

    boolean isObject(T object);

    boolean isArray(T array);

    Optional<T> get(T object, String field);

    Stream<T> arrayElements(T array);

    Stream<Map.Entry<String, T>> namedFields(T object);

    default <R> Stream<R> map(
        T object,
        BiFunction<String, T, Stream<R>> objectMapper,
        Function<T, Stream<R>> arrayMapper,
        Function<T, Stream<R>> leafMapper
    ) {
        return isObject(object) ? mapNamedFields(object, objectMapper)
            : isArray(object) ? mapArrayElements(object, arrayMapper)
                : leafMapper.apply(object);
    }

    default <R> Stream<R> mapNamedFields(T object, BiFunction<String, T, Stream<R>> map) {
        return namedFields(object).flatMap(e ->
            map.apply(e.getKey(), e.getValue()));
    }

    default <R> Stream<R> mapArrayElements(T array, Function<T, Stream<R>> map) {
        return arrayElements(array).flatMap(map);
    }
}
