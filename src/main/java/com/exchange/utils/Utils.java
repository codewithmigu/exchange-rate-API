package com.exchange.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

    private Utils() {
    }

    public static <T> Set<T> from(List<T> list) {
        return list == null ? Collections.emptySet() : new HashSet<>(list);
    }
}
