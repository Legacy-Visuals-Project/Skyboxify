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

package btw.lowercase.skyboxify.skybox.components;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum Weather implements StringRepresentable {
	CLEAR,
	RAIN,
	THUNDER;

	public static final Codec<Weather> CODEC = StringRepresentable.fromEnum(Weather::values);

	public static float getAlpha(final List<Weather> weatherConditions, final float rainStrength, final float thunderStrength) {
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

	@Override
	public @NotNull String getSerializedName() {
		return this.name().toLowerCase();
	}
}
