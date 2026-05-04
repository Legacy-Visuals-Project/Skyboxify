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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

//? <1.21.6 {
/*import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.BufferType;
*///? }

//? >=1.21.5 {
import com.mojang.blaze3d.buffers.GpuBuffer;
//? }

//? <=1.21.4
/*import com.mojang.blaze3d.vertex.VertexBuffer;*/

public class ReusableGeometry implements Geometry {
    //? >=1.21.5 {
    private final GpuBuffer vertexBuffer;
    private final GpuBuffer indexBuffer;
    private final VertexFormat.IndexType indexType;
    private final int indexCount;
    //? } else {
    /*private final VertexBuffer vertexBuffer;
     *///? }
    private boolean closed;

    ReusableGeometry(
            //? >=1.21.5 {
            final GpuBuffer vertexBuffer,
            final GpuBuffer indexBuffer,
            final VertexFormat.IndexType indexType,
            final int indexCount
            //? } else {
            /*final VertexBuffer vertexBuffer
             *///? }
    ) {
        this.vertexBuffer = vertexBuffer;
        //? >=1.21.5 {
        this.indexBuffer = indexBuffer;
        this.indexType = indexType;
        this.indexCount = indexCount;
        //? }
    }

    //? >=1.21.5 {
    public GpuBuffer vertexBuffer() { return this.vertexBuffer; }

    public GpuBuffer indexBuffer() { return this.indexBuffer; }

    public VertexFormat.IndexType indexType() { return this.indexType; }

    public int indexCount() { return this.indexCount; }
    //? } else {
    /*public VertexBuffer vertexBuffer() { return this.vertexBuffer; }
    *///? }

    public static ReusableGeometry create(final VertexFormat vertexFormat, final VertexFormat.Mode vertexMode, final int vertexCount, final Consumer<VertexConsumer> vertexConsumer) {
        try (final ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(vertexFormat.getVertexSize() * vertexCount)) {
            final BufferBuilder builder = new BufferBuilder(byteBufferBuilder, vertexMode, vertexFormat);
            vertexConsumer.accept(builder);
            try (final MeshData meshData = builder.buildOrThrow()) {
                //? >=1.21.5 {
                final GpuBuffer vertexBuffer = createBuffer("Sky geometry vertex buffer", meshData.vertexBuffer(), true);
                final int indexCount = meshData.drawState().indexCount();

                GpuBuffer indexBuffer;
                VertexFormat.IndexType indexType;

                final ByteBuffer indexByteBuffer = meshData.indexBuffer();
                if (indexByteBuffer == null) {
                    final RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(meshData.drawState().mode());
                    final GpuBuffer autoIndexBuffer = autoStorageIndexBuffer.getBuffer(indexCount);
                    indexBuffer = RenderSystem.getDevice().createBuffer(() -> "Sky geometry index buffer", GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST, autoIndexBuffer.size());
                    RenderSystem.getDevice().createCommandEncoder().copyToBuffer(autoIndexBuffer.slice(), indexBuffer.slice());
                    indexType = autoStorageIndexBuffer.type();
                } else {
                    indexBuffer = RenderSystem.getDevice().createBuffer(() -> "Sky geometry index buffer", GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST, indexByteBuffer);
                    indexType = meshData.drawState().indexType();
                }
                //? } else {
                /*final VertexBuffer vertexBuffer = new VertexBuffer(BufferUsage.STATIC_WRITE);
                vertexBuffer.bind();
                vertexBuffer.upload(meshData);
                VertexBuffer.unbind();
                *///? }
                return new ReusableGeometry(
                        vertexBuffer
                        //? >=1.21.5 {
                        , indexBuffer,
                        indexType,
                        indexCount
                        //? }
                );
            }
        }
    }

    //? >=1.21.5 {
    private static GpuBuffer createBuffer(final String name, final java.nio.ByteBuffer buffer, final boolean isVertex) {
        return RenderSystem.getDevice().createBuffer(
                () -> name,
                //? >=1.21.6 {
                (isVertex ? GpuBuffer.USAGE_VERTEX : GpuBuffer.USAGE_INDEX) | GpuBuffer.USAGE_COPY_DST,
                //? } else {
                /*isVertex ? BufferType.VERTICES : BufferType.INDICES,
                BufferUsage.STATIC_WRITE,
                *///? }
                buffer
        );
    }
    //? }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.vertexBuffer.close();
            //? >=1.21.5 {
            this.indexBuffer.close();
            //? }
        }
    }
}