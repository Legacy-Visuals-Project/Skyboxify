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

package org.visuals.legacy.lightconfig.lib.v1;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.visuals.legacy.lightconfig.lib.v1.field.*;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigDeserializer;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigSerializer;
import org.visuals.legacy.lightconfig.lib.v1.serialization.Json;
import org.visuals.legacy.lightconfig.lib.v1.type.Type;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class Config {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final List<AbstractConfigField<?>> configFields = new ArrayList<>();
    protected final String id;
    protected final File configFile;
    protected final Json.Serializer serializer;
    protected final Json.Deserializer deserializer;

    public Config(final String id, final ConfigSerializer<?> serializer, final ConfigDeserializer<?> deserializer) {
        this.id = id;
        this.configFile = FabricLoader.getInstance().getConfigDir().resolve(id + ".json").toFile();
        if (!(serializer instanceof Json.Serializer && deserializer instanceof Json.Deserializer)) {
            throw new RuntimeException("Only json serialization is currently supported! Please use Json.SERIALIZER/Json.DESERIALIZER!");
        }

        this.serializer = (Json.Serializer) serializer;
        this.deserializer = (Json.Deserializer) deserializer;
    }

    public Config(final String id) {
        this(id, new Json.Serializer(), new Json.Deserializer());
    }

    public BooleanConfigField booleanFieldOf(final String name, final boolean defaultValue) {
        final BooleanConfigField field = new BooleanConfigField(this, name, defaultValue);
        this.configFields.add(field);
        return field;
    }

    public StringConfigField stringFieldOf(final String name, final String defaultValue) {
        final StringConfigField field = new StringConfigField(this, name, defaultValue);
        this.configFields.add(field);
        return field;
    }

    public <T extends Number> NumericConfigField<T> numericFieldOf(final String name, final Type<T> type, final T defaultValue) {
        final NumericConfigField<T> field = new NumericConfigField<>(this, type, name, defaultValue);
        this.configFields.add(field);
        return field;
    }

    public <T extends Enum<T>> EnumConfigField<T> enumFieldOf(final String name, final T defaultValue) {
        final EnumConfigField<T> field = new EnumConfigField<>(this, name, defaultValue);
        this.configFields.add(field);
        return field;
    }

    public void load() {
        if (!this.configFile.exists()) {
            this.logger.info("Config file doesn't exist! Creating one...");
            this.save();
            return;
        }

        boolean success = true;
        try {
            final String json = Files.readString(this.configFile.toPath());
            final JsonObject object = this.deserializer.deserialize(json).getAsJsonObject();
            if (object == null) {
                this.logger.warn("Failed to load config! Defaulting to original settings.");
                success = false;
            } else {
                this.deserializer.withObject(object);
                this.configFields.forEach(field -> {
                    try {
                        if (!object.has(field.getName())) {
                            throw new Exception("Failed to load value for '" + field.getName() + "', object didn't contain a value for it.");
                        } else {
                            field.load(this.deserializer);
                        }
                    } catch (Exception exception) {
                        this.logger.warn(exception.getMessage());
                    }
                });
            }
        } catch (Exception ignored) {
            this.logger.warn("Failed to load config! Error occured when reading file.");
            return;
        }

        if (success) {
            this.logger.info("Config successfully loaded!");
        }
    }

    public void save() {
        final JsonObject object = new JsonObject();
        this.serializer.withObject(object);
        this.configFields.forEach(field -> {
            try {
                field.save(this.serializer);
            } catch (Exception ignored) {
                this.logger.warn("Failed to save config field '{}'!", field.getName());
            }
        });

        try {
            Files.write(this.configFile.toPath(), this.serializer.serialize(object));
        } catch (Exception ignored) {
            this.logger.warn("Failed to save config!");
            return;
        }

        this.logger.info("Config successfully saved!");
    }

    public void reset() {
        this.configFields.forEach(AbstractConfigField::restore);
        this.save();
    }

    public Logger getLogger() {
        return logger;
    }

    public List<AbstractConfigField<?>> getConfigFields() {
        return configFields;
    }

    public String getId() {
        return this.id;
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public abstract Screen getConfigScreen(@Nullable Screen parent);
}
