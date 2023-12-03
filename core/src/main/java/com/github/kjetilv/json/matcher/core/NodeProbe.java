package com.github.kjetilv.json.matcher.core;

import java.util.List;

public sealed interface NodeProbe<T> extends Probe<T> permits FoundNode{

    List<Probe<T>> branches();

    List<String> trace();
}
