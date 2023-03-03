package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

record ExactArray<T>(List<Path<T>> paths, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Search<T>> through(T main, List<String> trace) {
        List<T> mainElements = structure.arrayElements(main).toList();
        return DefaultStructureMatcher.exactPaths(main, trace, mainElements, paths);
    }
}
