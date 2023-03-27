package com.github.kjetilv.json;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Structure<T> {

    boolean isNull(T object);

    boolean isObject(T object);

    boolean isArray(T array);

    Optional<T> get(T object, String field);

    Stream<T> arrayElements(T array);

    default Map<String, T> fieldsMap(T object) {
        return namedFields(object).collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue
        ));
    }

    Stream<Map.Entry<String, T>> namedFields(T object);

    T toObject(Map<String, T> map);

    T toArray(Collection<T> values);

    T combine(T one, T two);

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
