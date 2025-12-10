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

import btw.lowercase.skyboxify.utils.ParserCodecs;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record Biomes(ImmutableList<ResourceLocation> locations, boolean inclusion) {
	public static Biomes DEFAULT = new Biomes(ImmutableList.of(), true);

	public static Codec<Biomes> CODEC = ParserCodecs.TRIMMED_STRING.xmap(input -> {
		final boolean inclusion = !input.startsWith("!");
		if (!inclusion) {
			input = input.substring(1);
		}

		final List<String> entries = ParserCodecs.SPLIT_SPACE_TRIMMED.parse(JavaOps.INSTANCE, input).getOrThrow();
		if (!entries.isEmpty()) {
			final ImmutableList.Builder<ResourceLocation> builder = new ImmutableList.Builder<>();
			builder.addAll(entries.stream().filter(ResourceLocation::isValidPath).map(ResourceLocation::parse).toList());
			return new Biomes(builder.build(), inclusion);
		} else {
			return Biomes.DEFAULT;
		}
	}, ParserCodecs::emptyCodecString);
}
