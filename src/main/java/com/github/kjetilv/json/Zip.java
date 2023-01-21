package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Zip {

    public static <X, Y> Stream<XY<X, Y>> of(List<X> xs, List<Y> ys) {
        return IntStream.range(0, Math.min(xs.size(), ys.size())).mapToObj(i -> new XY<>(i, xs.get(i), ys.get(i)));
    }

    private Zip() {
    }

    public record XY<X, Y>(int i, X x, Y y) {}
}
