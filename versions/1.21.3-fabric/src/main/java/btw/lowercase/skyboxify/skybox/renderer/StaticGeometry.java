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

import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.vertex.*;

import java.util.function.Consumer;

public class StaticGeometry implements Geometry {
    private final VertexBuffer vertexBuffer;
    private boolean closed;

    StaticGeometry(final VertexBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
    }

    public static StaticGeometry create(final VertexFormat vertexFormat, final VertexFormat.Mode vertexMode, final int vertexCount, final Consumer<VertexConsumer> vertexConsumer) {
        try (final ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(vertexFormat.getVertexSize() * vertexCount)) {
            final BufferBuilder builder = new BufferBuilder(byteBufferBuilder, vertexMode, vertexFormat);
            vertexConsumer.accept(builder);
            try (final MeshData meshData = builder.buildOrThrow()) {
                final VertexBuffer vertexBuffer = new VertexBuffer(BufferUsage.STATIC_WRITE);
                vertexBuffer.bind();
                vertexBuffer.upload(meshData);
                VertexBuffer.unbind();
                return new StaticGeometry(vertexBuffer);
            }
        }
    }

    public VertexBuffer vertexBuffer() {
        return this.vertexBuffer;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.vertexBuffer.close();
        }
    }
}
