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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Range(float min, float max) {
	public static final Codec<Range> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("min").forGetter(Range::min),
			Codec.FLOAT.fieldOf("max").forGetter(Range::max)
	).apply(instance, Range::new));

	public Range {
		if (min > max) {
			throw new IllegalStateException("Maximum value is lower than the minimum value: " + this);
		}
	}

	public boolean contains(final float value) {
		return value >= this.min && value <= this.max;
	}

	public static boolean contains(final List<Range> entries, final float value) {
		return entries.stream().anyMatch(range -> range.contains(value));
	}

	@Override
	public @NotNull String toString() {
		return String.format("%s..%s", this.min, this.max);
	}
}
