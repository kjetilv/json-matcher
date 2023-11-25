package com.github.kjetilv.json;

import java.util.List;

public sealed interface NodeProbe<T> extends Probe<T> permits FoundNode{

    List<Probe<T>> branches();

    List<String> trace();
}
