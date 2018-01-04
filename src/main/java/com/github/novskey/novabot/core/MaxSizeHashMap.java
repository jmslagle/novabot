package com.github.novskey.novabot.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Paris on 21/12/2017.
 */
public class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public MaxSizeHashMap(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}