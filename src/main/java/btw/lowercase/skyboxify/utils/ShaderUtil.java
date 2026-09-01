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

import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.vertex.BufferBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public final class ShaderUtil {
    private ShaderUtil() {
    }

    public static void applyColor(final int color) {
        final float red = ARGB.redFloat(color);
        final float green = ARGB.greenFloat(color);
        final float blue = ARGB.blueFloat(color);
        final float alpha = ARGB.alphaFloat(color);
        GlStateManager.color4f(red, green, blue, alpha);
    }

    public static void applyWhite() {
        applyColor(0xFFFFFFFF);
    }

    public static Vector3f transform(final Matrix4f matrix4f, final float x, final float y, final float z) {
        return matrix4f.transformPosition(new Vector3f(x, y, z));
    }

    public static BufferBuilder addVertex(final BufferBuilder builder, final Vector3f vector3f) {
        return builder.vertex(vector3f.x, vector3f.y, vector3f.z);
    }

    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public static Matrix4f extractModelView() {
        matrixBuffer.reset();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrixBuffer);
        return new Matrix4f(matrixBuffer);
    }

    public static void applyModelView(final Matrix4f matrix4f) {
        matrixBuffer.reset();
        matrix4f.get(matrixBuffer);
        matrixBuffer.flip();
        GL11.glMultMatrix(matrixBuffer);
    }
}
