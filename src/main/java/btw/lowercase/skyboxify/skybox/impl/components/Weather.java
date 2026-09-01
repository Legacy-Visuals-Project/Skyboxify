/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025-2026 lowercasebtw
 * Copyright (C) 2025-2026 Contributors to the project retain their copyright
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

package btw.lowercase.skyboxify.skybox.impl.components;

import btw.lowercase.skyboxify.utils.EnumSerializable;
import btw.lowercase.skyboxify.utils.ParserCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Weather(List<Condition> conditions) {
    public static final Weather CLEAR = new Weather(List.of(Condition.CLEAR));

    public static final Codec<Weather> CODEC = ParserCodecs.SPLIT_SPACE_TRIMMED.xmap(input -> {
        if (!input.isEmpty()) {
            return new Weather(Condition.CODEC.listOf().parse(JavaOps.INSTANCE, input).getOrThrow());
        } else {
            return CLEAR;
        }
    }, weather -> weather.conditions.stream().map(Condition::serializedName).toList());

    public float getAlpha(final float rainStrength, final float thunderStrength) {
        final float alpha = 1.0F - rainStrength;
        final float calculatedRainStrength = rainStrength - thunderStrength;

        float weatherAlpha = 0.0F;
        if (this.conditions.contains(Condition.CLEAR)) {
            weatherAlpha += alpha;
        }

        if (this.conditions.contains(Condition.RAIN)) {
            weatherAlpha += calculatedRainStrength;
        }

        if (this.conditions.contains(Condition.THUNDER)) {
            weatherAlpha += thunderStrength;
        }

        return Math.clamp(weatherAlpha, 0.0F, 1.0F);
    }

    public enum Condition implements EnumSerializable {
        CLEAR,
        RAIN,
        THUNDER;

        public static final Codec<Condition> CODEC = EnumSerializable.of(Condition::values);

        @Override
        public @NotNull String serializedName() {
            return this.name().toLowerCase();
        }
    }
}
