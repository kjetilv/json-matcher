package com.github.kjetilv.json;

import java.util.List;

public interface Match {

    List<? extends Probe> pathways();

    boolean matches();
}
