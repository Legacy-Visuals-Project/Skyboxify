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

package btw.lowercase.skyboxify.skybox.components;

import btw.lowercase.skyboxify.utils.CommonUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Fade(int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut, boolean alwaysOn) {
    public static final Fade DEFAULT = new Fade(0, 0, 0, 0, true);
    public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("startFadeIn", 0).forGetter(Fade::startFadeIn),
            Codec.INT.optionalFieldOf("endFadeIn", 0).forGetter(Fade::endFadeIn),
            Codec.INT.optionalFieldOf("startFadeOut", 0).forGetter(Fade::startFadeOut),
            Codec.INT.optionalFieldOf("endFadeOut", 0).forGetter(Fade::endFadeOut),
            Codec.BOOL.optionalFieldOf("alwaysOn", false).forGetter(Fade::alwaysOn)
    ).apply(instance, Fade::new));

    public Fade(int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut, boolean alwaysOn) {
        this.startFadeIn = normalizeIfNot(startFadeIn, alwaysOn);
        this.endFadeIn = normalizeIfNot(endFadeIn, alwaysOn);
        this.startFadeOut = normalizeIfNot(startFadeOut, alwaysOn);
        this.endFadeOut = normalizeIfNot(endFadeOut, alwaysOn);
        this.alwaysOn = alwaysOn;
    }

    private static int normalizeIfNot(int time, boolean ignore) {
        return ignore ? time : CommonUtils.normalizeTickTime(time);
    }

    public float getAlpha(int timeOfDay) {
        if (!alwaysOn) {
            return CommonUtils.calculateFadeAlphaValue(1.0F, 0.0F, timeOfDay, startFadeIn, endFadeIn, startFadeOut, endFadeOut);
        } else {
            return 1.0F;
        }
    }
}