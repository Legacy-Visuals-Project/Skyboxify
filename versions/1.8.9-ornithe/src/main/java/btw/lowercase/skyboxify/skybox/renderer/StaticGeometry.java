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

import net.minecraft.client.render.platform.GLX;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.vertex.BufferBuilder;
import net.minecraft.client.render.vertex.VertexFormat;
import net.minecraft.client.render.vertex.VertexFormatElement;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

public class StaticGeometry implements Geometry {
    private final VertexFormat vertexFormat;
    private final int vertexMode;
    private final BufferBuilder builder;
    private boolean closed;

    StaticGeometry(final VertexFormat vertexFormat, final int vertexMode, final BufferBuilder builder) {
        this.vertexFormat = vertexFormat;
        this.vertexMode = vertexMode;
        this.builder = builder;
    }

    public static StaticGeometry create(final VertexFormat vertexFormat, final int vertexMode, final int vertexCount, final Consumer<BufferBuilder> vertexConsumer) {
        final BufferBuilder builder = new BufferBuilder(vertexFormat.getVertexSize() * vertexCount);
        builder.begin(vertexMode, vertexFormat);
        vertexConsumer.accept(builder);
        builder.end();
        return new StaticGeometry(vertexFormat, vertexMode, builder);
    }

    @Override
    public void draw() {
        final int vertexCount = this.builder.getVertexCount();
        if (vertexCount > 0) {
            final int vertexSize = this.vertexFormat.getVertexSize();
            final ByteBuffer byteBuffer = this.builder.getBuffer();
            final List<VertexFormatElement> elements = this.vertexFormat.getElements();

            for (int elementIndex = 0; elementIndex < elements.size(); ++elementIndex) {
                final VertexFormatElement vertexFormatElement = elements.get(elementIndex);

                final VertexFormatElement.Usage usage = vertexFormatElement.getUsage();
                final int glCode = vertexFormatElement.getType().getGlCode();
                final int index = vertexFormatElement.getIndex();

                byteBuffer.position(this.vertexFormat.getOffset(elementIndex));
                switch (usage) {
                    case POSITION:
                        GL11.glVertexPointer(vertexFormatElement.getCount(), glCode, vertexSize, byteBuffer);
                        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                        break;
                    case UV:
                        GLX.clientActiveTexture(GLX.GL_TEXTURE0 + index);
                        GL11.glTexCoordPointer(vertexFormatElement.getCount(), glCode, vertexSize, byteBuffer);
                        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                        GLX.clientActiveTexture(GLX.GL_TEXTURE0);
                        break;
                    case COLOR:
                        GL11.glColorPointer(vertexFormatElement.getCount(), glCode, vertexSize, byteBuffer);
                        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                        break;
                    case NORMAL:
                        GL11.glNormalPointer(glCode, vertexSize, byteBuffer);
                        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }

            GL11.glDrawArrays(this.vertexMode, 0, vertexCount);
            for (VertexFormatElement element : elements) {
                final VertexFormatElement.Usage usage = element.getUsage();
                final int index = element.getIndex();
                switch (usage) {
                    case POSITION:
                        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                        break;
                    case UV:
                        GLX.clientActiveTexture(GLX.GL_TEXTURE0 + index);
                        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                        GLX.clientActiveTexture(GLX.GL_TEXTURE0);
                        break;
                    case COLOR:
                        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                        GlStateManager.clearColor();
                        break;
                    case NORMAL:
                        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.builder.clear();
        }
    }
}
