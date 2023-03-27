package com.github.kjetilv.json;

import java.util.Optional;

public interface StructureExtractor<T> {

    Optional<T> subset(T mask);
}
