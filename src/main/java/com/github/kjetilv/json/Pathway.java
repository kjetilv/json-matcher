package com.github.kjetilv.json;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

record Pathway<T>(T main, T expected, List<String> trace) {

    public static <T> Pathway<T> deadEnd(T expected, List<String> trace) {
        return new Pathway<>(null, expected, trace);
    }

    static <T> Stream<Pathway<T>> arrived(T main, T expected, List<String> trace) {
        return Stream.of(new Pathway<>(main, expected, trace));
    }

    static <T> Stream<Pathway<T>> exactPaths(
        T main,
        List<String> trace,
        List<T> mainElements,
        List<Path<T>> paths
    ) {
        return mainElements.size() != paths.size()
            ? Stream.of(deadEnd(main, trace))
            : Zip.of(paths, mainElements)
                .flatMap((Zip.XY<Path<T>, T> xy) ->
                    xy.x().through(xy.y()));
    }

    boolean found() {
        return Objects.equals(main, expected);
    }
}
