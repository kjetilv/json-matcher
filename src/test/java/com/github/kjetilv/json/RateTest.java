package com.github.kjetilv.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateTest {

    @Test
    public void test() {
        assertEquals(
            new Rate(7, 72),
            new Rate(2, 48).plus(new Rate(1, 18)));
    }
}
