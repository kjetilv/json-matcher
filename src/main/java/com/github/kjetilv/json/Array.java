package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

record Array<T>(List<Path<T>> paths, Structure<T> structure) implements Path<T> {

    @Override
    public Stream<Pathway<T>> through(T main, List<String> trace) {
        List<T> mainElements = structure.arrayElements(main).toList();
        return Pathway.exactPaths(main, trace, mainElements, paths);
    }
}
