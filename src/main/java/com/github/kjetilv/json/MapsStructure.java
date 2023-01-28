package com.github.kjetilv.json;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public final class MapsStructure implements Structure<Object> {

    @Override
    public boolean isObject(Object object) {
        return object instanceof Map<?,?>;
    }

    @Override
    public boolean isArray(Object array) {
        return array instanceof Iterable<?>;
    }

    @Override
    public Optional<Object> get(Object object, String field) {
        return object instanceof Map<?,?> map
            ? Optional.ofNullable(map.get(field))
            : Optional.empty();
    }

    @Override
    public Stream<Object> arrayElements(Object array) {
        return array instanceof Iterable<?> iterable ? stream(iterable) : Stream.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<Map.Entry<String, Object>> namedFields(Object object) {
        return object instanceof Map<?,?> map ? ((Map<String, Object>)map).entrySet().stream() : Stream.empty();
    }

    @SuppressWarnings("unchecked")
    private static Stream<Object> stream(Iterable<?> elements) {
        return (Stream<Object>) StreamSupport.stream(elements.spliterator(), false);
    }
}
