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

package btw.lowercase.skyboxify.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public final class CodecUtils {
    private CodecUtils() {
    }

    private static Codec<Integer> intRangeWithMessage(final int min, final int max, final Function<Integer, String> onError) {
        return Codec.INT.validate(integer -> integer.compareTo(min) >= 0 && integer.compareTo(max) <= 0 ? DataResult.success(integer) : DataResult.error(() -> onError.apply(integer)));
    }

    public static Codec<Integer> intRange(final int min, final int max) {
        return intRangeWithMessage(min, max, integer -> "Value must be within range [" + min + ";" + max + "]: " + integer);
    }

    public static <E> Codec<E> orCompressed(final Codec<E> left, final Codec<E> right) {
        return new Codec<>() {
            public <T> DataResult<T> encode(final E object, final DynamicOps<T> dynamicOps, final T value) {
                return dynamicOps.compressMaps() ? right.encode(object, dynamicOps, value) : left.encode(object, dynamicOps, value);
            }

            public <T> DataResult<Pair<E, T>> decode(final DynamicOps<T> dynamicOps, final T object) {
                return dynamicOps.compressMaps() ? right.decode(dynamicOps, object) : left.decode(dynamicOps, object);
            }

            @Override
            public String toString() {
                return left + " orCompressed " + right;
            }
        };
    }

    public static <E> Codec<E> idResolverCodec(final ToIntFunction<E> transformer, final IntFunction<E> accepted, final int unknownId) {
        return Codec.INT
                .flatXmap(integer -> Optional.ofNullable(accepted.apply(integer)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown element id: " + integer)),
                        input -> {
                            final int value = transformer.applyAsInt(input);
                            return value == unknownId ? DataResult.error(() -> "Element with unknown id: " + input) : DataResult.success(value);
                        });
    }
}
