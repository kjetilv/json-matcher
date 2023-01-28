package com.github.kjetilv.json;

import java.util.List;

public record PathsMatch<T>(List<Pathway<T>> pathways) implements Match {

    @Override
    public boolean matches() {
        return pathways().stream().allMatch(Pathway::found);
    }
}
