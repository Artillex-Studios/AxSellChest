package com.artillexstudios.axsellchest.utils;

import java.util.HashMap;

public class Cooldown<T> {
    private final HashMap<Long, T> BACKING_MAP = new HashMap<>();

    public void add(T value, long time) {
        expire();

        BACKING_MAP.put(System.currentTimeMillis() + time, value);
    }

    public void clear() {
        BACKING_MAP.clear();
    }

    public boolean contains(T value) {
        expire();

        return BACKING_MAP.containsValue(value);
    }

    private void expire() {
        BACKING_MAP.entrySet().removeIf(next -> next.getKey() > System.currentTimeMillis());
    }
}
