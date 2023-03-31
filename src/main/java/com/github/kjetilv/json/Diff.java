package com.github.kjetilv.json;

import java.util.Objects;

public record Diff<T>(T expected, T found) {

    boolean isDiff() {
        return !Objects.equals(expected, found);
    }
}
