package com.client.legitskillissue.event;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

/**
 * ARCHITECT CORE: High-performance Custom Event Bus
 * Optimized using LambdaMetafactory to eliminate reflection overhead.
 */
public class EventBus {
    public static final EventBus INSTANCE = new EventBus();
    
    private final Map<Class<?>, List<Handler>> registry = new ConcurrentHashMap<>();
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public void register(Object obj) {
        for (Method m : obj.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(EventTarget.class) && m.getParameterCount() == 1) {
                Class<?> eventClass = m.getParameterTypes()[0];
                m.setAccessible(true);
                
                try {
                    MethodHandle methodHandle = lookup.unreflect(m);
                    MethodType invokedType = MethodType.methodType(BiConsumer.class);
                    MethodType functionType = MethodType.methodType(void.class, Object.class, Object.class);
                    CallSite callSite = LambdaMetafactory.metafactory(
                        lookup,
                        "accept",
                        invokedType,
                        functionType,
                        methodHandle,
                        methodHandle.type()
                    );
                    
                    BiConsumer<Object, Object> lambda = (BiConsumer<Object, Object>) callSite.getTarget().invokeExact();
                    
                    registry.computeIfAbsent(eventClass, k -> new ArrayList<>())
                            .add(new Handler(obj, lambda, m.getAnnotation(EventTarget.class).priority()));
                    
                    registry.get(eventClass).sort(Comparator.comparingInt(h -> h.priority));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public void unregister(Object obj) {
        for (List<Handler> list : registry.values()) {
            list.removeIf(h -> h.source == obj);
        }
    }

    public Event post(Event event) {
        List<Handler> list = registry.get(event.getClass());
        if (list != null) {
            for (Handler handler : list) {
                handler.lambda.accept(handler.source, event);
            }
        }
        return event;
    }

    private static class Handler {
        public final Object source;
        public final BiConsumer<Object, Object> lambda;
        public final byte priority;

        public Handler(Object source, BiConsumer<Object, Object> lambda, byte priority) {
            this.source = source;
            this.lambda = lambda;
            this.priority = priority;
        }
    }
}
