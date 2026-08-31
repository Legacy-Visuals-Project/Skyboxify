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

package org.visuals.legacy.lightconfig.lib.v1.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigDeserializer;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigSerializer;
import org.visuals.legacy.lightconfig.lib.v1.serialization.Json;

import java.util.Arrays;

public final class Types {
    public static final Type<Boolean> BOOLEAN_TYPE = new Type<>() {
        @Override
        public @Nullable Boolean read(final ConfigDeserializer<?> deserializer, final String name) {
            final JsonElement element = ((Json.Deserializer) deserializer).object().get(name);
            if (element == null || !element.isJsonPrimitive() || (element instanceof final JsonPrimitive primitive && !primitive.isBoolean())) {
                return null;
            } else {
                return element.getAsBoolean();
            }
        }

        @Override
        public void write(final ConfigSerializer<?> serializer, final String name, final Boolean value) {
            ((Json.Serializer) serializer).object().addProperty(name, value);
        }
    };

    public static final Type<Integer> INTEGER_TYPE = new Type<>() {
        @Override
        public @Nullable Integer read(final ConfigDeserializer<?> deserializer, final String name) {
            final JsonElement element = ((Json.Deserializer) deserializer).object().get(name);
            if (element == null || !element.isJsonPrimitive() || (element instanceof final JsonPrimitive primitive && !primitive.isNumber())) {
                return null;
            } else {
                return element.getAsNumber().intValue();
            }
        }

        @Override
        public void write(final ConfigSerializer<?> serializer, final String name, final Integer value) {
            ((Json.Serializer) serializer).object().addProperty(name, value);
        }
    };

    public static final Type<Short> SHORT_TYPE = new Type<>() {
        @Override
        public @Nullable Short read(final ConfigDeserializer<?> deserializer, final String name) {
            final JsonElement element = ((Json.Deserializer) deserializer).object().get(name);
            if (element == null || !element.isJsonPrimitive() || (element instanceof final JsonPrimitive primitive && !primitive.isNumber())) {
                return null;
            } else {
                return element.getAsNumber().shortValue();
            }
        }

        @Override
        public void write(final ConfigSerializer<?> serializer, final String name, final Short value) {
            ((Json.Serializer) serializer).object().addProperty(name, value);
        }
    };

    public static final Type<Double> DOUBLE_TYPE = new Type<>() {
        @Override
        public @Nullable Double read(final ConfigDeserializer<?> deserializer, final String name) {
            final JsonElement element = ((Json.Deserializer) deserializer).object().get(name);
            if (element == null || !element.isJsonPrimitive() || (element instanceof final JsonPrimitive primitive && !primitive.isNumber())) {
                return null;
            } else {
                return element.getAsNumber().doubleValue();
            }
        }

        @Override
        public void write(final ConfigSerializer<?> serializer, final String name, final Double value) {
            ((Json.Serializer) serializer).object().addProperty(name, value);
        }
    };

    public static final Type<Float> FLOAT_TYPE = new Type<>() {
        @Override
        public @Nullable Float read(final ConfigDeserializer<?> deserializer, final String name) {
            final JsonElement element = ((Json.Deserializer) deserializer).object().get(name);
            if (element == null || !element.isJsonPrimitive() || (element instanceof final JsonPrimitive primitive && !primitive.isNumber())) {
                return null;
            } else {
                return element.getAsNumber().floatValue();
            }
        }

        @Override
        public void write(final ConfigSerializer<?> serializer, final String name, final Float value) {
            ((Json.Serializer) serializer).object().addProperty(name, value);
        }
    };

    public static final Type<Long> LONG_TYPE = new Type<>() {
        @Override
        public @Nullable Long read(final ConfigDeserializer<?> deserializer, final String name) {
            final JsonElement element = ((Json.Deserializer) deserializer).object().get(name);
            if (element == null || !element.isJsonPrimitive() || (element instanceof final JsonPrimitive primitive && !primitive.isNumber())) {
                return null;
            } else {
                return element.getAsNumber().longValue();
            }
        }

        @Override
        public void write(final ConfigSerializer<?> serializer, final String name, final Long value) {
            ((Json.Serializer) serializer).object().addProperty(name, value);
        }
    };

    public static final Type<String> STRING_TYPE = new Type<>() {
        @Override
        public @Nullable String read(final ConfigDeserializer<?> deserializer, final String name) {
            final JsonElement element = ((Json.Deserializer) deserializer).object().get(name);
            if (element == null || !element.isJsonPrimitive() || (element instanceof final JsonPrimitive primitive && !primitive.isString())) {
                return null;
            } else {
                return element.getAsString();
            }
        }

        @Override
        public void write(final ConfigSerializer<?> serializer, final String name, final String value) {
            ((Json.Serializer) serializer).object().addProperty(name, value);
        }
    };

    public static <T extends Enum<T>> Type<T> enumType(final Class<T> enumClass) {
        return new Type<>() {
            @Override
            public @Nullable T read(final ConfigDeserializer<?> deserializer, final String name) {
                final JsonElement element = ((Json.Deserializer) deserializer).object().get(name);
                if (element == null || !element.isJsonPrimitive() || (element instanceof final JsonPrimitive primitive && !primitive.isString())) {
                    return null;
                } else {
                    return Arrays.stream(enumClass.getEnumConstants())
                            .filter(cons -> cons.name().equals(element.getAsString()))
                            .findFirst()
                            .orElse(null);
                }
            }

            @Override
            public void write(final ConfigSerializer<?> serializer, final String name, final T value) {
                ((Json.Serializer) serializer).object().addProperty(name, value.name());
            }
        };
    }

    private Types() {
    }
}
