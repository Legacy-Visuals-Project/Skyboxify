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

package btw.lowercase.skyboxify.utils;

import btw.lowercase.skyboxify.skybox.components.Range;
import btw.lowercase.skyboxify.skybox.components.Weather;
import lombok.experimental.UtilityClass;
import net.minecraft.util.Mth;

import java.util.List;

@UtilityClass
public final class CommonUtils {
	public static int normalizeTickTime(int tickTime) {
		int result = tickTime % 24000;
		if (result < 0) {
			result += 24000;
		}

		return result;
	}

	public static boolean checkRanges(double value, List<Range> rangeEntries) {
		return rangeEntries.isEmpty() || rangeEntries.stream()
				.anyMatch(range -> com.google.common.collect.Range.closed(range.min(), range.max()).contains((float) value));
	}

	public static boolean isInTimeInterval(int currentTime, int startTime, int endTime) {
		if (currentTime < 0 || currentTime >= 24000) {
			return false; // Invalid time
		} else if (startTime <= endTime) {
			return currentTime >= startTime && currentTime <= endTime;
		} else {
			return currentTime >= startTime || currentTime <= endTime;
		}
	}

	public static float calculateFadeAlphaValue(float maxAlpha, float minAlpha, int currentTime, int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut) {
		if (isInTimeInterval(currentTime, endFadeIn, startFadeOut)) {
			return maxAlpha;
		} else if (isInTimeInterval(currentTime, startFadeIn, endFadeIn)) {
			final int fadeInDuration = calculateCyclicTimeDistance(startFadeIn, endFadeIn);
			final int timePassedSinceFadeInStart = calculateCyclicTimeDistance(startFadeIn, currentTime);
			return minAlpha + ((float) timePassedSinceFadeInStart / fadeInDuration) * (maxAlpha - minAlpha);
		} else if (isInTimeInterval(currentTime, startFadeOut, endFadeOut)) {
			final int fadeOutDuration = calculateCyclicTimeDistance(startFadeOut, endFadeOut);
			final int timePassedSinceFadeOutStart = calculateCyclicTimeDistance(startFadeOut, currentTime);
			return maxAlpha + ((float) timePassedSinceFadeOutStart / fadeOutDuration) * (minAlpha - maxAlpha);
		} else {
			return minAlpha;
		}
	}

	public static int calculateCyclicTimeDistance(int startTime, int endTime) {
		return (endTime - startTime + 24000) % 24000;
	}

	public static float calculateConditionAlphaValue(float maxAlpha, float minAlpha, float lastAlpha, int duration, boolean in) {
		if (duration == 0) {
			return lastAlpha;
		} else if (in && maxAlpha == lastAlpha) {
			return maxAlpha;
		} else if (!in && lastAlpha == minAlpha) {
			return minAlpha;
		} else {
			final float alphaChange = (maxAlpha - minAlpha) / duration;
			final float result = in ? lastAlpha + alphaChange : lastAlpha - alphaChange;
			return Mth.clamp(result, minAlpha, maxAlpha);
		}
	}

	public static float getWeatherAlpha(List<Weather> weatherConditions, float rainStrength, float thunderStrength) {
		final float alpha = 1.0F - rainStrength;
		final float calculatedRainStrength = rainStrength - thunderStrength;

		float weatherAlpha = 0.0F;
		if (weatherConditions.contains(Weather.CLEAR)) {
			weatherAlpha += alpha;
		}

		if (weatherConditions.contains(Weather.RAIN)) {
			weatherAlpha += calculatedRainStrength;
		}

		if (weatherConditions.contains(Weather.THUNDER)) {
			weatherAlpha += thunderStrength;
		}

		return Mth.clamp(weatherAlpha, 0.0F, 1.0F);
	}
}
