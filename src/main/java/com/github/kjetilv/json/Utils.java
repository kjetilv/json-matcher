package com.github.kjetilv.json;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Utils {

    static Object combine(Object one, Object two) {
        if (Objects.equals(one, two)) {
            return one;
        }
        if (one == null) {
            return two;
        }
        if (two == null) {
            return one;
        }
        if (one instanceof Map<?, ?> mapOne && two instanceof Map<?, ?> mapTwo) {
            return combine(mapOne, mapTwo);
        }
        if (one instanceof List<?> collOne && two instanceof List<?> collTwo) {
            return combine(collOne, collTwo);
        }
        throw new IllegalArgumentException("Cannot combine: " + one + " + " + two);
    }

    static Map<?, ?> combine(Map<?, ?> one, Map<?, ?> two) {
        if (one == null || one.isEmpty()) {
            return two == null ? Collections.emptyMap() : two;
        }
        if (two == null || two.isEmpty()) {
            return one;
        }
        return toMap(
            Stream.of(one, two)
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .map(key ->
                    Map.entry(key, combine(one.get(key), two.get(key)))),
            Utils::combine
        );
    }

    static List<?> combine(List<?> one, List<?> two) {
        if (one == null || one.isEmpty()) {
            return two == null ? Collections.emptyList() : new ArrayList<>(two);
        }
        if (two == null || two.isEmpty()) {
            return new ArrayList<>(one);
        }
        int oneLen = one.size();
        int twoLen = two.size();
        int len = Math.max(oneLen, twoLen);
        List<Object> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(combine(
                oneLen > i ? one.get(i) : null,
                twoLen > i ? two.get(i) : null
            ));
        }
        return list;
    }

    static <K, V> Map<K, V> toMap(Stream<Map.Entry<K, V>> entryStream) {
        return toMap(entryStream, null);
    }

    private Utils() {

    }

    private static <K, V> LinkedHashMap<K, V> toMap(
        Stream<Map.Entry<K, V>> entryStream,
        BinaryOperator<V> mergeFunction
    ) {
        return entryStream.collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            mergeFunction == null ? Utils::noCombine : mergeFunction,
            LinkedHashMap::new
        ));
    }

    private static <V> V noCombine(V o1, V o2) {
        if (o1.equals(o2)) {
            return o1;
        }
        throw new IllegalStateException("Cannot combine: " + o1 + " / " + o2);
    }
}
