package com.example.ablabla.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Centralized reflection utility.
 * Tries dev name first, then SRG obfuscated name as fallback.
 */
public final class ReflectionUtil {

    private ReflectionUtil() {}

    public static Field findField(Class<?> clazz, String... names) {
        for (String name : names) {
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (Exception ignored) {}
        }
        System.out.println("[Ablabla] ReflectionUtil: could not find field in " + clazz.getSimpleName() + " tried: " + java.util.Arrays.toString(names));
        return null;
    }

    public static Method findMethod(Class<?> clazz, String... names) {
        for (String name : names) {
            try {
                Method m = clazz.getDeclaredMethod(name);
                m.setAccessible(true);
                return m;
            } catch (Exception ignored) {}
        }
        System.out.println("[Ablabla] ReflectionUtil: could not find method in " + clazz.getSimpleName() + " tried: " + java.util.Arrays.toString(names));
        return null;
    }

    public static void setInt(Field f, Object obj, int value) {
        try { if (f != null) f.setInt(obj, value); } catch (Exception ignored) {}
    }

    public static void setBoolean(Field f, Object obj, boolean value) {
        try { if (f != null) f.setBoolean(obj, value); } catch (Exception ignored) {}
    }

    public static int getInt(Field f, Object obj, int fallback) {
        try { return f != null ? f.getInt(obj) : fallback; } catch (Exception e) { return fallback; }
    }

    public static void invoke(Method m, Object obj, Object... args) {
        try { if (m != null) m.invoke(obj, args); } catch (Exception ignored) {}
    }
}
