package com.github.kjetilv.json.matcher.core;

import java.util.Optional;

public interface StructureExtractor<T> {

    Optional<T> extract(T mask);
}
