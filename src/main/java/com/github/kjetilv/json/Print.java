package com.github.kjetilv.json;

import java.util.List;

final class Print {

    private Print() {

    }

    static String trace(List<String> tr) {
        return "/" + (tr == null ||  tr.isEmpty() ? "" : String.join("/", tr));
    }
}