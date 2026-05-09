package com.client.legitskillissue.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Centralized reflection utility.
 * Tries dev name first, then SRG obfuscated name as fallback.
 * 
 * NOTE: Consider using FieldCache for frequently accessed fields.
 */
public final class ReflectionUtil {

    private static final Logger logger = Logger.getLogger(ReflectionUtil.class);

    private ReflectionUtil() {}

    public static Field findField(Class<?> clazz, String... names) {
        Class<?> current = clazz;
        while (current != null) {
            for (String name : names) {
                try {
                    Field f = current.getDeclaredField(name);
                    f.setAccessible(true);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found field: " + clazz.getSimpleName() + "." + name);
                    }
                    return f;
                } catch (Exception ignored) {}
            }
            current = current.getSuperclass();
        }
        logger.warn("Could not find field in " + clazz.getSimpleName() + " tried: " + java.util.Arrays.toString(names));
        return null;
    }

    public static Method findMethod(Class<?> clazz, String... names) {
        return findMethod(clazz, null, names);
    }

    public static Method findMethod(Class<?> clazz, Class<?>[] params, String... names) {
        Class<?> current = clazz;
        while (current != null) {
            for (String name : names) {
                try {
                    Method m = current.getDeclaredMethod(name, params);
                    m.setAccessible(true);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found method: " + clazz.getSimpleName() + "." + name);
                    }
                    return m;
                } catch (Exception ignored) {}
            }
            current = current.getSuperclass();
        }
        logger.warn("Could not find method in " + clazz.getSimpleName() + " tried: " + java.util.Arrays.toString(names));
        return null;
    }

    public static void setInt(Field f, Object obj, int value) {
        try { 
            if (f != null) f.setInt(obj, value); 
        } catch (Exception e) {
            logger.error("Failed to set int field", e);
        }
    }

    public static void setBoolean(Field f, Object obj, boolean value) {
        try { 
            if (f != null) f.setBoolean(obj, value); 
        } catch (Exception e) {
            logger.error("Failed to set boolean field", e);
        }
    }

    public static int getInt(Field f, Object obj, int fallback) {
        try { 
            return f != null ? f.getInt(obj) : fallback; 
        } catch (Exception e) { 
            logger.error("Failed to get int field", e);
            return fallback; 
        }
    }

    public static void invoke(Method m, Object obj, Object... args) {
        try { 
            if (m != null) m.invoke(obj, args); 
        } catch (Exception e) {
            logger.error("Failed to invoke method", e);
        }
    }
}
