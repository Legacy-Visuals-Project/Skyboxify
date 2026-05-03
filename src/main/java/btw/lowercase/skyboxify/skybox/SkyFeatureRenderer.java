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

package btw.lowercase.skyboxify.skybox;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.Identifier;
import btw.lowercase.skyboxify.utils.CommonUtils;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.*;
import java.util.function.Consumer;

//? >=1.21.11 {
import org.jspecify.annotations.NonNull;
//? } else {
/*import lombok.NonNull;
*///? }

//? >=1.21.5 {
//? >=1.21.11
import com.mojang.blaze3d.textures.FilterMode;
//? >=1.21.6 {
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.textures.GpuTextureView;
import org.joml.Vector3f;
//? } else {
/*import com.mojang.blaze3d.textures.GpuTexture;
*///? }
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.buffers.GpuBuffer;
//? } else {
/*import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.vertex.VertexBuffer;
import btw.lowercase.skyboxify.utils.BlendFunction;
*///? }

//? =1.21.5 {
/*import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
*///? }

public class SkyFeatureRenderer {
    public static final SkyFeatureRenderer.ReusableGeometry DEFAULT_GEOMETRY = SkyFeatureRenderer.ReusableGeometry.create(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS,SkyPart.COUNT * 4, vertexConsumer -> {
        for (final SkyPart part : SkyPart.VALUES) {
            // 400
            part.getUv().face(vertexConsumer, part.getRotationMatrix(), 100.0F); // Bigger the value, the less view-bobbing affects it, but it starts clipping which is bad
        }
    });

    private final Map<Pipeline, List<Submit>> submits = new HashMap<>();

    public void submit(final Pipeline pipeline, @NonNull final Geometry geometry, final Transforms transforms, final Identifier location) {
        if (!geometry.isClosed()) {
            this.submits.computeIfAbsent(pipeline, it -> new ArrayList<>()).add(new Submit(geometry, transforms, location));
        } else {
            throw new IllegalStateException("Cannot call submit with closed geometry!");
        }
    }

    public void endFrame() {
        for (final Map.Entry<Pipeline, List<Submit>> entry : this.submits.entrySet()) {
            final Pipeline pipeline = entry.getKey();
            final List<Submit> submits = entry.getValue();
            //? >=1.21.5 {
            endFrame1_21_5(pipeline, submits);
            //? } else {
            /*for (final Submit submit : submits) {
                endFrame1_21_4(submit, pipeline.blendFunction);
            }
            *///? }
        }

        this.clear();
    }

    //? >=1.21.5 {
    private void endFrame1_21_5(final Pipeline pipeline, final List<Submit> submits) {
        final Minecraft minecraft = Minecraft.getInstance();
        final RenderTarget renderTarget =
                //? >=26.2 {
                minecraft.gameRenderer.mainRenderTarget();
                //? } else {
                /*minecraft.getMainRenderTarget();
                *///? }

        //? >=1.21.6 {
        final GpuTextureView colorTexture = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : renderTarget.getColorTextureView();
        final GpuTextureView depthTexture = renderTarget.useDepth ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : renderTarget.getDepthTextureView()) : null;
        //? } else {
        /*final GpuTexture colorTexture = renderTarget.getColorTexture();
        final GpuTexture depthTexture = renderTarget.useDepth ? renderTarget.getDepthTexture() : null;
        *///? }

        int i = 0;
        final PassData[] submitPassData = new PassData[submits.size()];
        for (final Submit submit : submits) {
            final Vector4f shaderColor = CommonUtils.unpackARGB(submit.transforms.shaderColor);
            final AbstractTexture abstractTexture = Minecraft.getInstance().getTextureManager().getTexture(submit.texture);

            //? >=1.21.6 {
            final GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(
                    submit.transforms.modelViewMatrix,
                    shaderColor,
                    new Vector3f(),
                    new Matrix4f()
                    //? <=1.21.10
                    //, 1.0F
            );

            final GpuTextureView texture = abstractTexture.getTextureView();
            //? } else {
            /*final GpuTexture texture = abstractTexture.getTexture();
            *///? }

            submitPassData[i++] = new PassData(
                    //? >=1.21.6
                    dynamicTransforms,
                    texture
                    //? <=1.21.5
                    //, shaderColor
            );
        }

        try (final RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                //? >=1.21.6
                () -> "Sky frame draw for " + pipeline,
                colorTexture, OptionalInt.empty(),
                depthTexture, OptionalDouble.empty()
        )) {
            pass.setPipeline(pipeline.pipeline);

            //? >=1.21.6
            RenderSystem.bindDefaultUniforms(pass);

            i = 0;
            for (final Submit submit : submits) {
                if (submit.geometry.isClosed()) {
                    throw new IllegalStateException("Failed to draw as geometry is already closed!");
                }

                final PassData data = submitPassData[i++];
                //? >=1.21.6
                pass.setUniform("DynamicTransforms", data.dynamicTransforms);

                //? >=1.21.11 {
                pass.bindTexture("Sampler0", data.texture, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
                //? } else {
                /*pass.bindSampler("Sampler0", data.texture);
                *///? }

                //? <=1.21.5
                //RenderSystem.setShaderColor(data.shaderColor.x, data.shaderColor.y, data.shaderColor.z, data.shaderColor.w);

                if (submit.geometry instanceof ReusableGeometry reusableGeometry) {
                    pass.setVertexBuffer(0, reusableGeometry.vertexBuffer);
                    pass.setIndexBuffer(reusableGeometry.indexBuffer, reusableGeometry.indexType);
                    //? >=1.21.6 {
                    pass.drawIndexed(0, 0, reusableGeometry.indexCount, 1);
                    //? } else {
                    /*pass.drawIndexed(0, reusableGeometry.indexCount);
                    *///? }
                }

                //? <=1.21.5
                //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
    //? } else {
    /*private void endFrame1_21_4(final Submit submit, final BlendFunction blendFunction) {
        if (submit.geometry instanceof ReusableGeometry reusableGeometry) {
            final Vector4f shaderColor = CommonUtils.unpackARGB(submit.transforms.shaderColor);
            RenderSystem.setShaderColor(shaderColor.x, shaderColor.y, shaderColor.z, shaderColor.w);

            RenderSystem.setShader(SkyStorage.CUSTOM_SKYBOX_SHADER);
            RenderSystem.setShaderTexture(0, submit.texture);
            RenderSystem.depthMask(false);
            RenderSystem.colorMask(true, true, true, false);
            if (blendFunction != null) {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(blendFunction.srcFactor(), blendFunction.dstFactor());
            } else {
                RenderSystem.disableBlend();
            }

            reusableGeometry.vertexBuffer.bind();
            reusableGeometry.vertexBuffer.drawWithShader(submit.transforms.modelViewMatrix, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
            VertexBuffer.unbind();
            if (blendFunction != null) {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }

            RenderSystem.colorMask(true, true, true, false);
            RenderSystem.depthMask(true);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
    *///? }

    public void clear() {
        this.submits.clear();
    }

    public interface Geometry extends AutoCloseable {
        boolean isClosed();

        void close();
    }

    public static class ReusableGeometry implements Geometry {
        //? >=1.21.5 {
        private final GpuBuffer vertexBuffer;
        private final GpuBuffer indexBuffer;
        private final VertexFormat.IndexType indexType;
        private final boolean ownsIndexBuffer;
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
                final boolean ownsIndexBuffer,
                final int indexCount
                //? } else {
                /*final VertexBuffer vertexBuffer
                *///? }
        ) {
            this.vertexBuffer = vertexBuffer;
            //? >=1.21.5 {
            this.indexBuffer = indexBuffer;
            this.indexType = indexType;
            this.ownsIndexBuffer = ownsIndexBuffer;
            this.indexCount = indexCount;
            //? }
        }

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
                    boolean ownsIndexBuffer;

                    final java.nio.ByteBuffer indexByteBuffer = meshData.indexBuffer();
                    if (indexByteBuffer == null) {
                        final RenderSystem.AutoStorageIndexBuffer autoStorageIndexBuffer = RenderSystem.getSequentialBuffer(meshData.drawState().mode());
                        indexBuffer = autoStorageIndexBuffer.getBuffer(indexCount);
                        indexType = autoStorageIndexBuffer.type();
                        ownsIndexBuffer = false;
                    } else {
                        indexBuffer = createBuffer("Sky geometry index buffer", indexByteBuffer, false);
                        indexType = meshData.drawState().indexType();
                        ownsIndexBuffer = true;
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
                            ownsIndexBuffer,
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
                if (this.ownsIndexBuffer) {
                    this.indexBuffer.close();
                }
                //? }
            }
        }
    }

    public record Submit(Geometry geometry, Transforms transforms, Identifier texture) {
    }

    public record Pipeline(
            //? >=1.21.5 {
            RenderPipeline pipeline
            //? } else {
            /*BlendFunction blendFunction
            *///? }
    ) {
    }

    public record Transforms(Matrix4f modelViewMatrix, int shaderColor) {
    }

    //? >=1.21.5 {
    private record PassData(
            //? >=1.21.6
            GpuBufferSlice dynamicTransforms,
            //? >=1.21.6 {
            GpuTextureView texture
            //? } else {
            /*GpuTexture texture
            *///? }
            //? <=1.21.5
            //, Vector4f shaderColor
    ) {
    }
    //? }
}
