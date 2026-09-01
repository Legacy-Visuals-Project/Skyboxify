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

package btw.lowercase.skyboxify.skybox.renderer;

import btw.lowercase.skyboxify.skybox.SkyPart;
import btw.lowercase.skyboxify.skybox.impl.components.UV;
import btw.lowercase.skyboxify.utils.ShaderUtil;
import net.minecraft.client.render.vertex.DefaultVertexFormat;
import org.lwjgl.opengl.GL11;

public interface Geometry extends AutoCloseable {
    StaticGeometry DEFAULT = StaticGeometry.create(
            DefaultVertexFormat.POSITION_TEX,
            GL11.GL_QUADS,
            SkyPart.COUNT * 4,
            vertexConsumer -> {
                for (final SkyPart part : SkyPart.VALUES) {
                    final UV uv = part.getUv();
                    final float size = 100.0F;
                    ShaderUtil.addVertex(vertexConsumer, ShaderUtil.transform(part.getRotationMatrix(), -size, -size, -size)).texture(uv.minU(), uv.minV()).nextVertex();
                    ShaderUtil.addVertex(vertexConsumer, ShaderUtil.transform(part.getRotationMatrix(), -size, -size, size)).texture(uv.minU(), uv.maxV()).nextVertex();
                    ShaderUtil.addVertex(vertexConsumer, ShaderUtil.transform(part.getRotationMatrix(), size, -size, size)).texture(uv.maxU(), uv.maxV()).nextVertex();
                    ShaderUtil.addVertex(vertexConsumer, ShaderUtil.transform(part.getRotationMatrix(), size, -size, -size)).texture(uv.maxU(), uv.minV()).nextVertex();
                }
            });

    boolean isClosed();

    void close();
}
