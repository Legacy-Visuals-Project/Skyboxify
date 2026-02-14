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

public record Fade(int startIn, int endIn, int startOut, int endOut, boolean alwaysOn) {
	public static final Fade DEFAULT = new Fade(0, 0, 0, 0, true);

	public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("startFadeIn", 0).forGetter(Fade::startIn),
			Codec.INT.optionalFieldOf("endFadeIn", 0).forGetter(Fade::endIn),
			Codec.INT.optionalFieldOf("startFadeOut", 0).forGetter(Fade::startOut),
			Codec.INT.optionalFieldOf("endFadeOut", 0).forGetter(Fade::endOut),
			Codec.BOOL.optionalFieldOf("alwaysOn", false).forGetter(Fade::alwaysOn)
	).apply(instance, Fade::new));

	public Fade(final int startIn, final int endIn, final int startOut, final int endOut, final boolean alwaysOn) {
		this.startIn = normalizeIfNot(startIn, alwaysOn);
		this.endIn = normalizeIfNot(endIn, alwaysOn);
		this.startOut = normalizeIfNot(startOut, alwaysOn);
		this.endOut = normalizeIfNot(endOut, alwaysOn);
		this.alwaysOn = alwaysOn;
	}

	public float getAlpha(final int timeOfDay) {
		if (this.alwaysOn || CommonUtils.isInTimeInterval(timeOfDay, this.endIn, this.startOut)) {
			return 1.0F;
		} else if (CommonUtils.isInTimeInterval(timeOfDay, this.startIn, this.endIn)) {
			final int fadeInDuration = calculateCyclicTimeDistance(this.startIn, this.endIn);
			final int timePassedSinceFadeInStart = calculateCyclicTimeDistance(this.startIn, timeOfDay);
			return (float) timePassedSinceFadeInStart / fadeInDuration;
		} else if (CommonUtils.isInTimeInterval(timeOfDay, this.startOut, this.endOut)) {
			final int fadeOutDuration = calculateCyclicTimeDistance(this.startOut, this.endOut);
			final int timePassedSinceFadeOutStart = calculateCyclicTimeDistance(this.startOut, timeOfDay);
			return 1.0F - ((float) timePassedSinceFadeOutStart / fadeOutDuration);
		} else {
			return 0.0F;
		}
	}

	private static int normalizeIfNot(final int time, final boolean ignore) {
		return ignore ? time : CommonUtils.normalizeTickTime(time);
	}

	private static int calculateCyclicTimeDistance(final int startTime, final int endTime) {
		return (endTime - startTime + 24000) % 24000;
	}
}