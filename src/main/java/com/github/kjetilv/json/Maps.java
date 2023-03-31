package com.github.kjetilv.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Maps {

    private Maps() {

    }

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
        if (one instanceof Map<?,?> mapOne && two instanceof Map<?, ?> mapTwo) {
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
        return Stream.concat(one.keySet().stream(), two.keySet().stream())
            .map(key ->
                Map.entry(key, combine(one.get(key), two.get(key))))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                Maps::combine,
                LinkedHashMap::new
            ));
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
}
