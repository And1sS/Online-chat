package com.and1ss.onlinechat.utils;

import lombok.Data;

@Data
public class Triple<X, Y, Z> {
    private final X first;
    private final Y second;
    private final Z third;
}
