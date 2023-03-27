package com.github.kjetilv.json;

@FunctionalInterface
public interface Extract<T> {

    T value();
}
