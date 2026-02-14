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
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3fc;

import java.text.DecimalFormat;
import java.util.List;

@UtilityClass
public final class CommonUtils {
	public static int normalizeTickTime(final int tickTime) {
		int result = tickTime % 24000;
		if (result < 0) {
			result += 24000;
		}

		return result;
	}

	public static boolean checkRanges(final double value, final List<Range> rangeEntries) {
		return rangeEntries.isEmpty() || rangeEntries.stream()
				.anyMatch(range -> com.google.common.collect.Range.closed(range.min(), range.max()).contains((float) value));
	}

	public static boolean isInTimeInterval(final int currentTime, final int startTime, final int endTime) {
		if (currentTime < 0 || currentTime >= 24000) {
			return false; // Invalid time
		} else if (startTime <= endTime) {
			return currentTime >= startTime && currentTime <= endTime;
		} else {
			return currentTime >= startTime || currentTime <= endTime;
		}
	}

	public static float calculateConditionAlphaValue(final float maxAlpha, final float minAlpha, final float lastAlpha, final int duration, final boolean in) {
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

	public static float getWeatherAlpha(final List<Weather> weatherConditions, final float rainStrength, final float thunderStrength) {
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

	// This method replicates the old Mojang-made Quaternion mulPose method, which was
	// used for the initial sky transformations which did not have any bobbing issues.
	// See https://github.com/sp614x/optifine/issues/7235#issuecomment-1581930719
	public static void rotate(final Matrix4f pose, final float angleRads, final Vector3fc axis) {
		final Quaternionf quat = new Quaternionf(new AxisAngle4f(-angleRads, axis));
		final float qxy = quat.x * quat.y;
		final float qyz = quat.y * quat.z;
		final float qzx = quat.z * quat.x;
		final float qxw = quat.x * quat.w;
		final float qyw = quat.y * quat.w;
		final float qzw = quat.z * quat.w;
		final float j = 2.0F * quat.x * quat.x;
		final float k = 2.0F * quat.y * quat.y;
		final float l = 2.0F * quat.z * quat.z;
		pose.mul0(new Matrix4f()
				.m00(1.0F - k - l)
				.m11(1.0F - l - j)
				.m22(1.0F - j - k)
				.m33(1.0F)
				.m10(2.0F * (qxy + qzw))
				.m01(2.0F * (qxy - qzw))
				.m20(2.0F * (qzx - qyw))
				.m02(2.0F * (qzx + qyw))
				.m21(2.0F * (qyz + qxw))
				.m12(2.0F * (qyz - qxw)));
	}

	private static final DecimalFormat VECTOR_FORMAT = new DecimalFormat("# .##");

	public static String vectorToString(final Vector3fc vector3fc) {
		final String x = VECTOR_FORMAT.format(vector3fc.x()).trim();
		final String y = VECTOR_FORMAT.format(vector3fc.y()).trim();
		final String z = VECTOR_FORMAT.format(vector3fc.z()).trim();
		return String.format("[x=%s, y=%s, z=%s]", x, y, z);
	}
}
