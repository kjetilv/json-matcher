package com.github.kjetilv.json.matcher.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Maps {

    static <K, V> Map<K, V> toMap(Stream<Map.Entry<K, V>> entryStream) {
        return toMap(entryStream, null);
    }

    static <K, V> LinkedHashMap<K, V> toMap(
        Stream<Map.Entry<K, V>> entryStream,
        BinaryOperator<V> mergeFunction
    ) {
        return entryStream.collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            mergeFunction == null ? Maps::noCombine : mergeFunction,
            LinkedHashMap::new
        ));
    }

    private Maps(){

    }

    private static <V> V noCombine(V o1, V o2) {
        if (o1.equals(o2)) {
            return o1;
        }
        throw new IllegalStateException("Cannot combine: " + o1 + " / " + o2);
    }
}
