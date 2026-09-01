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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface EnumSerializable {
    @NotNull String serializedName();

    static <E extends Enum<E> & EnumSerializable> EnumCodec<E> of(final Supplier<E[]> supplier) {
        return new EnumCodec<>(supplier.get());
    }

    static <T extends EnumSerializable> Function<String, T> createNameLookup(final T[] array) {
        if (array.length > 16) {
            final Map<String, T> map = Arrays.stream(array)
                    .collect(Collectors.toMap(EnumSerializable::serializedName, it -> it));
            return string -> string == null ? null : map.get(string);
        } else {
            return name -> Arrays.stream(array).filter(it -> it.serializedName().equals(name)).findFirst().orElse(null);
        }
    }

    class EnumCodec<S extends EnumSerializable> implements Codec<S> {
        private final Codec<S> codec;

        public EnumCodec(final S[] array) {
            this.codec = CodecUtils.orCompressed(
                    Codec.stringResolver(EnumSerializable::serializedName, createNameLookup(array)),
                    CodecUtils.idResolverCodec(
                            it -> ((Enum<?>) it).ordinal(),
                            i -> i >= 0 && i < array.length ? array[i] : null,
                            -1
                    )
            );
        }

        @Override
        public <T> DataResult<Pair<S, T>> decode(final DynamicOps<T> ops, final T object) {
            return this.codec.decode(ops, object);
        }

        @Override
        public <T> DataResult<T> encode(final S enumSerializable, final DynamicOps<T> ops, final T object) {
            return this.codec.encode(enumSerializable, ops, object);
        }
    }
}
