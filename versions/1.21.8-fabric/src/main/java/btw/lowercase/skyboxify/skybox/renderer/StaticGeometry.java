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

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import java.util.function.Consumer;

public class StaticGeometry implements Geometry {
    private final GpuBuffer vertexBuffer;
    private final int indexCount;
    private boolean closed;

    StaticGeometry(final GpuBuffer vertexBuffer, final int indexCount) {
        this.vertexBuffer = vertexBuffer;
        this.indexCount = indexCount;
    }

    public static StaticGeometry create(final VertexFormat vertexFormat, final VertexFormat.Mode vertexMode, final int vertexCount, final Consumer<VertexConsumer> vertexConsumer) {
        try (final ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(vertexFormat.getVertexSize() * vertexCount)) {
            final BufferBuilder builder = new BufferBuilder(byteBufferBuilder, vertexMode, vertexFormat);
            vertexConsumer.accept(builder);
            try (final MeshData meshData = builder.buildOrThrow()) {
                final GpuBuffer vertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Static geometry vertex buffer", GpuBuffer.USAGE_VERTEX, meshData.vertexBuffer());
                return new StaticGeometry(vertexBuffer, meshData.drawState().indexCount());
            }
        }
    }

    public GpuBuffer vertexBuffer() {
        return this.vertexBuffer;
    }

    public int indexCount() {
        return this.indexCount;
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
