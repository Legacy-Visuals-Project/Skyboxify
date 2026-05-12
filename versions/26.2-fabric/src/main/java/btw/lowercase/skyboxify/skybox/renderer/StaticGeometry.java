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

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class StaticGeometry implements Geometry {
    private final GpuBuffer vertexBuffer;
    private final GpuBuffer indexBuffer;
    private final IndexType indexType;
    private final int indexCount;
    private boolean closed;

    StaticGeometry(final GpuBuffer vertexBuffer, final GpuBuffer indexBuffer, final IndexType indexType, final int indexCount) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.indexType = indexType;
        this.indexCount = indexCount;
    }

    public static StaticGeometry create(final VertexFormat vertexFormat, final PrimitiveTopology topology, final int vertexCount, final Consumer<VertexConsumer> vertexConsumer) {
        try (final ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(vertexFormat.getVertexSize() * vertexCount)) {
            final BufferBuilder builder = new BufferBuilder(byteBufferBuilder, topology, vertexFormat);
            vertexConsumer.accept(builder);
            try (final MeshData meshData = builder.buildOrThrow()) {
                final GpuDevice device = RenderSystem.getDevice();

                final GpuBuffer vertexBuffer = device.createBuffer(() -> "Static geometry vertex buffer", GpuBuffer.USAGE_VERTEX, meshData.vertexBuffer());
                final int indexCount = meshData.drawState().indexCount();

                GpuBuffer indexBuffer;
                IndexType indexType;

                final ByteBuffer indexByteBuffer = meshData.indexBuffer();
                if (indexByteBuffer == null) {
                    final RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(topology);
                    final GpuBuffer autoIndexBuffer = autoStorageIndexBuffer.getBuffer(indexCount);
                    indexBuffer = device.createBuffer(() -> "Static geometry auto index buffer", GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST, autoIndexBuffer.size());
                    device.createCommandEncoder().copyToBuffer(autoIndexBuffer.slice(), indexBuffer.slice());
                    indexType = autoStorageIndexBuffer.type();
                } else {
                    indexBuffer = device.createBuffer(() -> "Static geometry index buffer", GpuBuffer.USAGE_INDEX, indexByteBuffer);
                    indexType = meshData.drawState().indexType();
                }

                return new StaticGeometry(vertexBuffer, indexBuffer, indexType, indexCount);
            }
        }
    }

    public GpuBuffer vertexBuffer() {
        return this.vertexBuffer;
    }

    public GpuBuffer indexBuffer() {
        return this.indexBuffer;
    }

    public IndexType indexType() {
        return this.indexType;
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
            this.indexBuffer.close();
        }
    }
}
