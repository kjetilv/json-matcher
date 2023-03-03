package com.github.kjetilv.json;

import java.util.List;

public interface Search<T> {

    default boolean found() {
        return successRate().is100Percent();
    }

    Rate successRate();

    List<String> trace();
}
