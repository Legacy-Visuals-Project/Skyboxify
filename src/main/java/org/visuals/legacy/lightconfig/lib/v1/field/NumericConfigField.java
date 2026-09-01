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
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigDeserializer;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigSerializer;
import org.visuals.legacy.lightconfig.lib.v1.type.Type;

public class NumericConfigField<T extends Number> extends AbstractConfigField<T> {
    private final Type<T> type;

    public NumericConfigField(final Config config, final Type<T> type, final String name, final T defaultValue) {
        super(config, name, defaultValue);
        this.type = type;
    }

    @Override
    public void load(final ConfigDeserializer<?> deserializer) throws Exception {
        final T value = this.type.read(deserializer, this.name);
        if (value == null) {
            throw new Exception("Failed to load value for '" + this.name + "'");
        } else {
            this.setValue(value);
        }
    }

    @Override
    public void save(final ConfigSerializer<?> serializer) {
        this.type.write(serializer, this.name, this.value);
    }

    @Override
    public GuiElement createWidget() {
        return null; // TODO
    }
}