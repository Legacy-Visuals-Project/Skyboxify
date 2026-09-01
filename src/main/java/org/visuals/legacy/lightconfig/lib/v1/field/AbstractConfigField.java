/**
 * LightConfig
 * A config library.
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 mixces
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

package org.visuals.legacy.lightconfig.lib.v1.field;

import net.minecraft.client.gui.GuiElement;
import org.visuals.legacy.lightconfig.lib.v1.Config;
import org.visuals.legacy.lightconfig.lib.v1.events.ConfigFieldReset;
import org.visuals.legacy.lightconfig.lib.v1.events.ConfigValueChanged;
import org.visuals.legacy.lightconfig.lib.v1.events.EventManager;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigDeserializer;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigSerializer;

import java.util.function.BiConsumer;

public abstract class AbstractConfigField<T> {
    protected final Config config;
    protected final String name;
    protected final T defaultValue;
    protected T value;
    protected EventManager eventManager = new EventManager();

    public AbstractConfigField(final Config config, final String name, final T defaultValue) {
        this.config = config;
        this.name = name;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public abstract void load(final ConfigDeserializer<?> deserializer) throws Exception;

    public abstract void save(final ConfigSerializer<?> serializer) throws Exception;

    public abstract GuiElement createWidget();

    public String getTranslationKey() {
        return String.format("options.%s.%s", this.config.getId(), this.getName());
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(final T value) {
        this.eventManager.dispatch(new ConfigValueChanged<>(this.value, value));
        this.value = value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void restore() {
        this.eventManager.dispatch(new ConfigFieldReset(this));
        this.setValue(this.getDefaultValue());
    }

    public void onValueChanged(final BiConsumer<T, T> biConsumer) {
        this.eventManager.listen(ConfigValueChanged.class, event -> biConsumer.accept((T) event.oldValue(), (T) event.newValue()));
    }
}