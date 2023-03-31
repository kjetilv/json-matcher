package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

public interface Match<T> {

    List<? extends Probe<T>> pathways();

    default Stream<Probe<T>> leaves() {
        return pathways().stream().flatMap(Probe::leaves);
    }

    boolean matches();
}
