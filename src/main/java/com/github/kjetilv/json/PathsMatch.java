package com.github.kjetilv.json;

import java.util.List;

public record PathsMatch<T>(List<? extends Probe<T>> pathways) implements Match<T> {

    @Override
    public boolean matches() {
        return pathways().stream().allMatch(Probe::found);
    }
}
