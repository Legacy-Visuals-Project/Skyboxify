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

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class DynamicTransformsBuilder {
    private Matrix4f modelViewMatrix = null;
    private Vector4f colorModulator = null;

    public static DynamicTransformsBuilder of() {
        return new DynamicTransformsBuilder();
    }

    public DynamicTransformsBuilder withModelViewMatrix(final  Matrix4f matrix4f) {
        this.modelViewMatrix = matrix4f;
        return this;
    }

    public DynamicTransformsBuilder withShaderColor(final Vector4f vector4f) {
        this.colorModulator = vector4f;
        return this;
    }

    //? >=1.21.6 {
    public com.mojang.blaze3d.buffers.GpuBufferSlice build() {
        return com.mojang.blaze3d.systems.RenderSystem.getDynamicUniforms().writeTransform(
                orElse(this.modelViewMatrix, com.mojang.blaze3d.systems.RenderSystem.getModelViewMatrix()),
                orElse(this.colorModulator, new Vector4f(1.0F)),
				new Vector3f(),
				//? >=1.21.11 {
				new org.joml.Matrix4f()
				//?} else {
				/*com.mojang.blaze3d.systems.RenderSystem.getTextureMatrix(),
				com.mojang.blaze3d.systems.RenderSystem.getShaderLineWidth()
				 *///?}
        );
    }
    //?}

	private static <T> T orElse(final T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}
}
