package com.github.kjetilv.json;

import java.util.List;

public interface Search {

    default boolean found() {
        return successRate().is100Percent();
    }

    Rate successRate();

    List<String> trace();
}
