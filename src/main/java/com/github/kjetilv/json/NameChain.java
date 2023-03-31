package com.github.kjetilv.json;

import java.util.stream.Collectors;
import java.util.stream.Stream;

interface NameChain {

    Stream<String> stream();

    static int compare(NameChain chain, Object other) {
        if (other instanceof NameChain otherChain) {
            return chain.stream().collect(Collectors.joining("/"))
                .compareTo(otherChain.stream().collect(Collectors.joining("/")));
        }
        return 1;
    }
}
