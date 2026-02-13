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

package btw.lowercase.skyboxify.skybox;

import btw.lowercase.skyboxify.Skyboxify;
import btw.lowercase.skyboxify.skybox.components.Range;
import btw.lowercase.skyboxify.utils.ParserCodecs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public final class SkyboxParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(SkyboxParser.class);

	private SkyboxParser() {
	}

	// TODO: Finish converting to CODEC's
	public static @Nullable JsonObject parseSkyProperties(final Properties properties, final ResourceLocation propertiesResourceLocation, final PackResources packResources) {
		final JsonObject output = new JsonObject();

		final String sourceTexturePath = parseSourceTexture(properties, propertiesResourceLocation, packResources);
		if (sourceTexturePath == null) {
			return null;
		}

		output.addProperty("source", sourceTexturePath);

		// Convert fade
		parseFade(properties, output);

		// Speed
		if (properties.containsKey("speed")) {
			final float value = ParserCodecs.safeParseFloat(properties.getProperty("speed", null), 1.0F);
			if (value != Float.MIN_VALUE) {
				output.addProperty("speed", value);
			} else {
				LOGGER.warn("Invalid speed provided in skybox.");
			}
		}

		// Heights
		if (properties.containsKey("heights")) {
			parseHeights(properties.getProperty("heights"), output);
		}

		// Days Loop -> Loop
		if (properties.containsKey("days")) {
			parseDaysLoop(properties.getProperty("days"), properties.getProperty("daysLoop", null), output);
		}

		// Blend
		if (properties.containsKey("blend")) {
			output.addProperty("blend", String.valueOf(properties.getProperty("blend")));
		}

		// Rotation
		if (properties.containsKey("rotate")) {
			output.addProperty("rotate", Boolean.parseBoolean(properties.getProperty("rotate")));
		}

		// Transition
		if (properties.containsKey("transition")) {
			output.addProperty("transition", Integer.parseInt(properties.getProperty("transition")));
		}

		// Axis
		if (properties.containsKey("axis")) {
			output.addProperty("axis", properties.getProperty("axis"));
		}

		// Weather
		if (properties.containsKey("weather")) {
			output.addProperty("weather", String.valueOf(properties.getProperty("weather")));
		}

		// Biomes
		if (properties.containsKey("biomes")) {
			output.addProperty("biomes", String.valueOf(properties.getProperty("biomes")));
		}

		return output;
	}

	private static String parseSourceTexture(final Properties properties, final ResourceLocation propertiesResourceLocation, final PackResources packResources) {
		final String source = properties.getProperty("source", null);
		if (source == null) {
			LOGGER.error("Failed to load source texture \"{}\"", "No source provided or was null");
			return null;
		}

		final DataResult<ResourceLocation> sourceResult = ParserCodecs.getSourceTextureCodec(propertiesResourceLocation).parse(JavaOps.INSTANCE, source);
		if (sourceResult.isError()) {
			LOGGER.error("{}", sourceResult.error().get());
			return null;
		}

		final ResourceLocation sourceTextureIdentifier = sourceResult.getOrThrow();
		try {
			if (!Skyboxify.getConfig().debug.isEnabled()) {
				final IoSupplier<InputStream> sourceTextureStream = packResources.getResource(PackType.CLIENT_RESOURCES, sourceTextureIdentifier);
				if (sourceTextureStream == null && !Skyboxify.getConfig().debug.isEnabled()) {
					LOGGER.error("Failed to load texture '{}', missing or corrupt image?", sourceTextureIdentifier);
					return null;
				}

				sourceTextureStream.get().close();
			}
		} catch (final Exception ignored) {
		}

		return sourceTextureIdentifier.toString();
	}

	private static int toTickTime(final String time) {
		final String[] parts = time.trim().split(":");
		if (parts.length == 2) {
			final int m = ParserCodecs.safeParseInteger(parts[1], -1);
			int h = ParserCodecs.safeParseInteger(parts[0], -1);
			if (h >= 0 && h <= 23 && m >= 0 && m <= 59) {
				h -= 6;
				if (h < 0) {
					h += 24;
				}

				return h * 1000 + (int) (m / 60.0F * 1000.0F);
			}
		}

		LOGGER.warn("Invalid time: \"{}\" in skybox.", time);
		return -1;
	}

	private static void parseFade(final Properties properties, final JsonObject output) {
		final JsonObject fade = new JsonObject();
		if (properties.containsKey("startFadeIn") && properties.containsKey("endFadeIn") && properties.containsKey("endFadeOut")) {
			final int startFadeIn = toTickTime(properties.getProperty("startFadeIn"));
			final int endFadeIn = toTickTime(properties.getProperty("endFadeIn"));
			final int endFadeOut = toTickTime(properties.getProperty("endFadeOut"));
			int startFadeOut;
			if (properties.containsKey("startFadeOut")) {
				startFadeOut = toTickTime(properties.getProperty("startFadeOut"));
			} else {
				startFadeOut = endFadeOut - (endFadeIn - startFadeIn);
				if (startFadeIn <= startFadeOut && endFadeIn >= startFadeOut) {
					startFadeOut = endFadeOut;
				}
			}

			fade.addProperty("startFadeIn", startFadeIn);
			fade.addProperty("endFadeIn", endFadeIn);
			fade.addProperty("startFadeOut", startFadeOut);
			fade.addProperty("endFadeOut", endFadeOut);
		} else {
			fade.addProperty("alwaysOn", true);
		}

		output.add("fade", fade);
	}

	private static void parseDaysLoop(final String days, final String daysLoop, final JsonObject output) {
		final List<Range> rangeEntries = ParserCodecs.getRangeEntriesCodec(false).orElse(List.of()).parse(JavaOps.INSTANCE, days).getOrThrow();
		if (!rangeEntries.isEmpty()) {
			final JsonObject loop = new JsonObject();
			loop.addProperty("days", daysLoop == null ? 8 : ParserCodecs.safeParseInteger(daysLoop, 8));

			final JsonArray ranges = new JsonArray();
			rangeEntries.stream().map(range -> Range.CODEC.encode(range, JsonOps.INSTANCE, new JsonObject()).getOrThrow()).forEach(ranges::add);
			loop.add("ranges", ranges);

			output.add("loop", loop);
		}
	}

	private static void parseHeights(final String input, final JsonObject output) {
		final List<Range> rangeEntries = ParserCodecs.getRangeEntriesCodec(true).orElse(List.of()).parse(JavaOps.INSTANCE, input).getOrThrow();
		if (!rangeEntries.isEmpty()) {
			final JsonArray heights = new JsonArray();
			rangeEntries.stream().map(range -> Range.CODEC.encode(range, JsonOps.INSTANCE, new JsonObject()).getOrThrow()).forEach(heights::add);
			output.add("heights", heights);
		}
	}
}
