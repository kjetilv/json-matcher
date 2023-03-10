package com.github.kjetilv.json;

import java.util.List;

public record PathsMatch<T>(List<? extends Search> pathways) implements Match {

    @Override
    public boolean matches() {
        return pathways().stream().allMatch(Search::found);
    }
}
