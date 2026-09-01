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

import btw.lowercase.skyboxify.utils.BlendFunction;
import btw.lowercase.skyboxify.utils.EnumSerializable;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.function.Function;

public enum Blend implements EnumSerializable {
    ADD(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), new BlendFunction(BlendFunction.SrcFactor.SRC_ALPHA, BlendFunction.DstFactor.ONE)),
    SUBTRACT(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(BlendFunction.SrcFactor.ONE_MINUS_DST_COLOR, BlendFunction.DstFactor.ZERO)),
    MULTIPLY(alpha -> new Vector4f(alpha, alpha, alpha, alpha), new BlendFunction(BlendFunction.SrcFactor.DST_COLOR, BlendFunction.DstFactor.ONE_MINUS_SRC_ALPHA)),
    DODGE(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(BlendFunction.SrcFactor.ONE, BlendFunction.DstFactor.ONE)),
    BURN(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(BlendFunction.SrcFactor.ZERO, BlendFunction.DstFactor.ONE_MINUS_SRC_COLOR)),
    SCREEN(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(BlendFunction.SrcFactor.ONE, BlendFunction.DstFactor.ONE_MINUS_SRC_COLOR)),
    REPLACE(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), null),
    OVERLAY(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(BlendFunction.SrcFactor.DST_COLOR, BlendFunction.DstFactor.SRC_COLOR)),
    ALPHA(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), new BlendFunction(BlendFunction.SrcFactor.SRC_ALPHA, BlendFunction.DstFactor.ONE_MINUS_SRC_ALPHA));

    public static final Codec<Blend> CODEC = EnumSerializable.of(Blend::values).orElse(Blend.ADD);

    private final Function<Float, Vector4f> colorConsumer;
    private final BlendFunction blendFunction;

    Blend(final Function<Float, Vector4f> colorConsumer, final BlendFunction blendFunction) {
        this.colorConsumer = colorConsumer;
        this.blendFunction = blendFunction;
    }

    public Vector4f getShaderColor(final float alpha) {
        return this.colorConsumer.apply(alpha);
    }

    public @Nullable BlendFunction getBlendFunction() {
        return this.blendFunction;
    }

    @Override
    public @NotNull String serializedName() {
        return this.name().toLowerCase();
    }
}
