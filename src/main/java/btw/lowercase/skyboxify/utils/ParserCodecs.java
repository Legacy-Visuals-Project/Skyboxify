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
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JavaOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class ParserCodecs {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParserCodecs.class);
    private static final Pattern OPTIFINE_RANGE_SEPARATOR = Pattern.compile("(\\d|\\))-(\\d|\\()");

    public static final Codec<String> TRIMMED_STRING = Codec.STRING.xmap(String::trim, ParserCodecs::emptyCodecString);
    public static final Codec<List<String>> SPLIT_SPACE_TRIMMED = TRIMMED_STRING.xmap(input -> Arrays.stream(input.split(" ")).map(String::trim).filter(s -> !s.isEmpty()).toList(), ParserCodecs::emptyCodecString);
	public static final Codec<Float> SAFE_FLOAT = TRIMMED_STRING.comapFlatMap(input -> {
		try {
			return DataResult.success(Float.parseFloat(input));
		} catch (NumberFormatException exception) {
			return DataResult.error(exception::getMessage);
		}
	}, String::valueOf);
	public static final Codec<Integer> SAFE_INTEGER = TRIMMED_STRING.comapFlatMap(input -> {
		try {
			return DataResult.success(Integer.parseInt(input));
		} catch (NumberFormatException exception) {
			return DataResult.error(exception::getMessage);
		}
	}, String::valueOf);

    public static final Codec<List<Weather>> WEATHER = SPLIT_SPACE_TRIMMED.xmap(input -> {
        if (!input.isEmpty()) {
            return Weather.CODEC.listOf().parse(JavaOps.INSTANCE, input).getOrThrow();
        } else {
            return List.of(Weather.CLEAR);
        }
    }, list -> list.stream().map(Weather::getSerializedName).toList());

    public static final Codec<Vector3fc> AXIS = TRIMMED_STRING.xmap(input -> {
        final List<String> parts = SPLIT_SPACE_TRIMMED.parse(JavaOps.INSTANCE, input.replaceAll(" +", " ")).getOrThrow();
        if (parts.size() == 3) {
            final Vector3f vector3f = new Vector3f(safeParseFloat(parts.get(0), Float.MIN_VALUE), safeParseFloat(parts.get(1), Float.MIN_VALUE), safeParseFloat(parts.get(2), Float.MIN_VALUE));
            if (vector3f.lengthSquared() > Mth.EPSILON) {
                return new Vector3f(vector3f.z, vector3f.y, -vector3f.x);
            }
        }

        LOGGER.warn("Invalid axis provided in skybox, returning default axis (Mth.X_AXIS).");
        return Mth.X_AXIS;
    }, ParserCodecs::emptyCodecString);

    private static final Codec<Range> RANGE_ENTRY = TRIMMED_STRING.xmap(input -> {
		if (input.contains("-")) {
			final String[] parts = input.split("-");
			if (parts.length == 2) {
				final int min = safeParseInteger(parts[0], -1);
				final int max = safeParseInteger(parts[1], -1);
				if (min >= 0 && max >= 0) {
					return new Range(min, max);
				}
			}
		} else {
			final int value = safeParseInteger(input, -1);
			if (value >= 0) {
				return new Range(value, value);
			}
		}

		return null;
    }, ParserCodecs::emptyCodecString);

    private static final Codec<Range> NEGATIVE_RANGE_ENTRY = TRIMMED_STRING.xmap(input -> {
        final String mapped = OPTIFINE_RANGE_SEPARATOR.matcher(input).replaceAll("$1=$2");
        if (mapped.contains("=")) {
            final String[] parts = mapped.split("=");
            if (parts.length == 2) {
                final int j = safeParseInteger(parts[0], Integer.MIN_VALUE);
                final int k = safeParseInteger(parts[1], Integer.MIN_VALUE);
                if (j != Integer.MIN_VALUE && k != Integer.MIN_VALUE) {
                    return new Range(Math.min(j, k), Math.max(j, k));
                }
            }
        } else {
            final String strippedBracketsInput = input.startsWith("(") && input.endsWith(")") ? input.substring(1, input.length() - 1) : input;
            final int i = safeParseInteger(strippedBracketsInput, Integer.MIN_VALUE);
            if (i != Integer.MIN_VALUE) {
                return new Range(i, i);
            }
        }

        return null;
    }, ParserCodecs::emptyCodecString);

    public static Codec<List<Range>> getRangeEntriesCodec(boolean allowNegative) {
        return TRIMMED_STRING.xmap(input -> {
            final List<Range> entries = new ArrayList<>();
            for (String part : input.split("\\s*,\\s*|\\s+")) {
                final Range range = (allowNegative ? NEGATIVE_RANGE_ENTRY : RANGE_ENTRY).parse(JavaOps.INSTANCE, part).getOrThrow();
				if (range != null) {
					entries.add(range);
				}
            }

            return entries;
        }, ParserCodecs::emptyCodecString);
    }

	public static Codec<ResourceLocation> getSourceTextureCodec(final ResourceLocation propertiesLocation) {
		return Codec.STRING.comapFlatMap(input -> {
			if (input == null) {
				return DataResult.success(propertiesLocation.withPath(propertiesLocation.getPath().replace(".properties", ".png")));
			} else if (input.startsWith("./")) {
				final String fileName = propertiesLocation.getPath().split("/")[propertiesLocation.getPath().split("/").length - 1];
				return DataResult.success(propertiesLocation.withPath(propertiesLocation.getPath().replace(fileName, input.substring(2))));
			} else {
				final String[] parts = input.split("/", 3);
				if (parts.length == 3 && parts[0].equals("assets")) {
					return DataResult.success(ResourceLocation.fromNamespaceAndPath(parts[1], parts[2]));
				} else {
					final ResourceLocation result = ResourceLocation.tryParse(input);
					if (result != null) {
						return DataResult.success(result);
					} else {
						return DataResult.error(() -> String.format("Failed to read texture source '%s' as resource location", input));
					}
				}
			}
		}, ParserCodecs::emptyCodecString);
	}

    public static float safeParseFloat(String value, float defaultValue) {
        return SAFE_FLOAT.orElse(defaultValue).parse(JavaOps.INSTANCE, value).getOrThrow();
    }

    public static int safeParseInteger(String value, int defaultValue) {
        return SAFE_INTEGER.orElse(defaultValue).parse(JavaOps.INSTANCE, value).getOrThrow();
    }

	public static <T> String emptyCodecString(T output) {
		return "";
	}
}
