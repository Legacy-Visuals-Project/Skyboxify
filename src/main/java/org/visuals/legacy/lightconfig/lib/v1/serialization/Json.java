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

package org.visuals.legacy.lightconfig.lib.v1.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class Json {
    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .create();

    public static class Serializer extends ConfigSerializer<JsonElement> {
        private JsonObject object = new JsonObject();

        public void withObject(final JsonObject object) {
            this.object = object;
        }

        @Override
        public byte[] serialize(final JsonElement value) {
            if (value == null) {
                throw new RuntimeException("Cannot serialize null!");
            } else {
                return GSON.toJson(value).getBytes();
            }
        }

        public JsonObject object() {
            return this.object;
        }
    }

    public static class Deserializer extends ConfigDeserializer<JsonElement> {
        private JsonObject object = new JsonObject();

        public void withObject(final JsonObject object) {
            this.object = object;
        }

        @Override
        public @Nullable JsonElement deserialize(final Object value) {
            if (!(value instanceof String string)) {
                return null;
            } else {
                return GSON.fromJson(string, JsonElement.class);
            }
        }

        public JsonObject object() {
            return this.object;
        }
    }
}
