package com.github.kjetilv.json.matcher.core;

public record Rate(int num, int den) {

    public static Rate SUCCESS = new Rate(1, 1);

    public static Rate FAILURE = new Rate(0, 1);

    public static Rate of(int num, int den) {
        return num == den ? SUCCESS : new Rate(num, den);
    }

    public Rate(int num, int den) {
        if (num < 0) {
            throw new IllegalArgumentException("Negative rate: " + num);
        }
        if (den < 1) {
            throw new IllegalArgumentException("Invalid denominator: " + den);
        }
        int div = gcd(num, den);
        this.num = num / div;
        this.den = den / div;
    }

    public Rate plus(Rate rate) {
        if (num() == 0) {
            return rate;
        }
        if (rate.num() == 0) {
            return this;
        }
        return new Rate(
            this.num() * rate.den() + rate.num() * this.den(),
            this.den() * rate.den());
    }

    boolean is100Percent() {
        return equals(SUCCESS);
    }

    private static int gcd(int v1, int v2) {
        return v2 == 0 ? v1 : gcd(v2, v1 % v2);
    }
}
