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

import btw.lowercase.skyboxify.utils.BlendFunction;
import com.mojang.serialization.Codec;
import lombok.Getter;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public enum Blend implements StringRepresentable {
    ADD(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), new BlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE)),
    SUBTRACT(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO)),
    MULTIPLY(alpha -> new Vector4f(alpha, alpha, alpha, alpha), new BlendFunction(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA)),
    DODGE(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ONE, GL11.GL_ONE)),
    BURN(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR)),
    SCREEN(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR)),
    REPLACE(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), null),
    OVERLAY(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR)),
    ALPHA(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), new BlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA));

    public static final Codec<Blend> CODEC = StringRepresentable.fromEnum(Blend::values).orElse(Blend.ADD);

    private final Function<Float, Vector4f> blendConsumer;
    @Getter
    private final BlendFunction blendFunction;

    Blend(Function<Float, Vector4f> blendConsumer, BlendFunction blendFunction) {
        this.blendConsumer = blendConsumer;
        this.blendFunction = blendFunction;
    }

    public Vector4f getShaderColor(final float value) {
        return this.blendConsumer.apply(value);
    }

    //? <=1.21.4 {
    /*public void apply(final float value) {
        if (this.blendFunction != null) {
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.blendFunc(this.blendFunction.srcFactor(), this.blendFunction.dstFactor());
        } else {
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        }
    }
    *///?}

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
