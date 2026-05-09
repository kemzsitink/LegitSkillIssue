package com.client.legitskillissue.utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton cache for reflected fields to eliminate repeated reflection overhead.
 * Thread-safe implementation using ConcurrentHashMap.
 */
public class FieldCache {
    private static final FieldCache INSTANCE = new FieldCache();
    private final Map<String, Field> cache = new ConcurrentHashMap<>();

    private FieldCache() {}

    public static FieldCache getInstance() {
        return INSTANCE;
    }

    /**
     * Gets a cached field or finds and caches it if not present.
     * 
     * @param clazz The class containing the field
     * @param names Possible field names (obfuscated and deobfuscated)
     * @return The field, or null if not found
     */
    public Field getField(Class<?> clazz, String... names) {
        String cacheKey = clazz.getName() + ":" + String.join(",", names);
        
        return cache.computeIfAbsent(cacheKey, k -> {
            Field field = ReflectionUtil.findField(clazz, names);
            if (field != null) {
                field.setAccessible(true);
            }
            return field;
        });
    }

    /**
     * Gets an integer value from a cached field.
     */
    public int getInt(Object instance, Class<?> clazz, String... names) {
        Field field = getField(clazz, names);
        if (field == null) return 0;
        try {
            return field.getInt(instance);
        } catch (IllegalAccessException e) {
            return 0;
        }
    }

    /**
     * Sets an integer value to a cached field.
     */
    public void setInt(Object instance, int value, Class<?> clazz, String... names) {
        Field field = getField(clazz, names);
        if (field == null) return;
        try {
            field.setInt(instance, value);
        } catch (IllegalAccessException ignored) {}
    }

    /**
     * Gets a float value from a cached field.
     */
    public float getFloat(Object instance, Class<?> clazz, String... names) {
        Field field = getField(clazz, names);
        if (field == null) return 0.0f;
        try {
            return field.getFloat(instance);
        } catch (IllegalAccessException e) {
            return 0.0f;
        }
    }

    /**
     * Sets a float value to a cached field.
     */
    public void setFloat(Object instance, float value, Class<?> clazz, String... names) {
        Field field = getField(clazz, names);
        if (field == null) return;
        try {
            field.setFloat(instance, value);
        } catch (IllegalAccessException ignored) {}
    }

    /**
     * Gets an object value from a cached field.
     */
    public Object get(Object instance, Class<?> clazz, String... names) {
        Field field = getField(clazz, names);
        if (field == null) return null;
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Sets an object value to a cached field.
     */
    public void set(Object instance, Object value, Class<?> clazz, String... names) {
        Field field = getField(clazz, names);
        if (field == null) return;
        try {
            field.set(instance, value);
        } catch (IllegalAccessException ignored) {}
    }

    /**
     * Clears the cache. Should be called on mod disable/reload.
     */
    public void clear() {
        cache.clear();
    }
}
