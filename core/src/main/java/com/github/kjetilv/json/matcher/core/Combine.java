package com.github.kjetilv.json.matcher.core;

import java.util.*;
import java.util.stream.Stream;

public final class Combine {

    static Object objects(Object one, Object two) {
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
            return maps(mapOne, mapTwo);
        }
        if (one instanceof List<?> collOne && two instanceof List<?> collTwo) {
            return lists(collOne, collTwo);
        }
        throw new IllegalArgumentException("Cannot combine: " + one + " + " + two);
    }

    public static Map<?, ?> maps(Map<?, ?> one, Map<?, ?> two) {
        if (one == null || one.isEmpty()) {
            return two == null ? Collections.emptyMap() : two;
        }
        if (two == null || two.isEmpty()) {
            return one;
        }
        return Maps.toMap(
            Stream.of(one, two)
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .map(key ->
                    Map.entry(key, objects(one.get(key), two.get(key)))),
            Combine::objects
        );
    }

    public static List<?> lists(List<?> one, List<?> two) {
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
            list.add(objects(
                oneLen > i ? one.get(i) : null,
                twoLen > i ? two.get(i) : null
            ));
        }
        return list;
    }

    private Combine() {

    }

}
