package com.and1ss.onlinechat.utils;

import lombok.Data;

@Data
public class Pair<X, Y> {
    private final X first;
    private final Y second;
}
