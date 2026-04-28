package btw.lowercase.skyboxify.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EventManager {
    private final Map<Class<?>, List<Consumer<? super Event>>> listeners = new ConcurrentHashMap<>();

    public <T extends Event> void listen(Class<T> eventClass, Consumer<? super T> consumer) {
        listeners.computeIfAbsent(eventClass, __ -> new CopyOnWriteArrayList<>()).add((Consumer<? super Event>) consumer);
    }

    public <T extends Event> T dispatch(T event) {
        final Class<?> eventClass = event.getClass();
        for (var entry : listeners.entrySet()) {
            if (!entry.getKey().isAssignableFrom(eventClass)) {
                continue;
            }

            for (Consumer<? super Event> consumer : entry.getValue()) {
                consumer.accept(event);
                if (event instanceof CancellableEvent cancellableEvent && cancellableEvent.isCancelled()) {
                    return event;
                }
            }
        }

        return event;
    }
}

