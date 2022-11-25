package com.exchange.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

public class Utils {

    private Utils() {
    }

    public static <T> Set<T> from(List<T> list) {
        return list == null ? Collections.emptySet() : new HashSet<>(list);
    }

    public static boolean equalsOneOf(Map.Entry<String, BigDecimal> entry, String str, Set<String> set) {
        return StringUtils.equalsIgnoreCase(entry.getKey(), str) || set.contains(entry.getKey());
    }
}
