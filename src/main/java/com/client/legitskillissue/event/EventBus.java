package com.client.legitskillissue.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ARCHITECT CORE: High-performance Custom Event Bus
 * Bypasses Forge's slow event bus by using direct method invocation caching.
 */
public class EventBus {
    public static final EventBus INSTANCE = new EventBus();
    
    private final Map<Class<?>, List<EventData>> registry = new ConcurrentHashMap<>();

    public void register(Object obj) {
        for (Method m : obj.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(EventTarget.class) && m.getParameterCount() == 1) {
                Class<?> eventClass = m.getParameterTypes()[0];
                if (!registry.containsKey(eventClass)) {
                    registry.put(eventClass, new CopyOnWriteArrayList<>());
                }
                m.setAccessible(true);
                registry.get(eventClass).add(new EventData(obj, m, m.getAnnotation(EventTarget.class).priority()));
                registry.get(eventClass).sort(Comparator.comparingInt(data -> data.priority));
            }
        }
    }

    public void unregister(Object obj) {
        for (List<EventData> list : registry.values()) {
            list.removeIf(data -> data.source == obj);
        }
    }

    public Event post(Event event) {
        List<EventData> list = registry.get(event.getClass());
        if (list != null) {
            for (EventData data : list) {
                try {
                    data.target.invoke(data.source, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return event;
    }

    private static class EventData {
        public final Object source;
        public final Method target;
        public final byte priority;

        public EventData(Object source, Method target, byte priority) {
            this.source = source;
            this.target = target;
            this.priority = priority;
        }
    }
}
