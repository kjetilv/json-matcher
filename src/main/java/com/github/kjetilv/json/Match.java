package com.github.kjetilv.json;

import java.util.List;
import java.util.stream.Stream;

public interface Match {

    List<? extends Probe> pathways();

    default Stream<Probe> leaves() {
        return pathways().stream().flatMap(Probe::leaves);
    }

    boolean matches();
}
