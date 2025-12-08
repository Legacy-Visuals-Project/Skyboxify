/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 Contributors to the project retain their copyright
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * "MINECRAFT" LINKING EXCEPTION TO THE GPL
 */

package btw.lowercase.skyboxify.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventManager {
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> listeners = new HashMap<>();

    public <T extends Event> void listen(Class<T> eventClass, Consumer<T> consumer) {
        listeners.computeIfAbsent(eventClass, __ -> new ArrayList<>()).add(consumer);
    }

    public <T extends Event> T dispatch(T event) {
        final Class<? extends Event> eventClass = event.getClass();
        if (listeners.containsKey(eventClass)) {
            for (Consumer<? extends Event> consumer : listeners.get(eventClass)) {
                ((Consumer<Event>) consumer).accept(event);
            }
        }

        return event;
    }
}
