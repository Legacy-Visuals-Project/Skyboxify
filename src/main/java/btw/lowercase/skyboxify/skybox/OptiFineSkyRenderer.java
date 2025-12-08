/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 Contributors to the project retain their copyright
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

import btw.lowercase.skyboxify.utils.*;
import btw.lowercase.skyboxify.mixins.RenderPipelinesAccessor;
import btw.lowercase.skyboxify.skybox.components.UVRange;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

public final class OptiFineSkyRenderer {
    public static final OptiFineSkyRenderer INSTANCE = new OptiFineSkyRenderer();

    //? >=1.21.5 {
    private com.mojang.blaze3d.buffers.GpuBuffer skyBuffer;
    private RenderSystem.AutoStorageIndexBuffer skyBufferIndices;
    private int skyBufferIndexCount;
    //?} else {
    /*private com.mojang.blaze3d.vertex.VertexBuffer skyBuffer;
     *///?}

    private OptiFineSkyRenderer() {
        Minecraft.getInstance().schedule(this::buildSky);
    }

    private void buildSky() {
        final VertexFormat vertexFormat = DefaultVertexFormat.POSITION_TEX;
        try (ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(vertexFormat.getVertexSize() * SkyPart.COUNT * 4)) {
            final VertexFormat.Mode vertexFormatMode = VertexFormat.Mode.QUADS;
            final BufferBuilder builder = new BufferBuilder(byteBufferBuilder, vertexFormatMode, vertexFormat);
            for (final SkyPart skyPart : SkyPart.VALUES) {
                final Matrix4f matrix4f = skyPart.getRotationMatrix();
                final UVRange uvRange = skyPart.getUVRange();
                final float quadSize = 30.0F;
                builder.addVertex(matrix4f, -quadSize, -quadSize, -quadSize).setUv(uvRange.minU(), uvRange.minV());
                builder.addVertex(matrix4f, -quadSize, -quadSize, quadSize).setUv(uvRange.minU(), uvRange.maxV());
                builder.addVertex(matrix4f, quadSize, -quadSize, quadSize).setUv(uvRange.maxU(), uvRange.maxV());
                builder.addVertex(matrix4f, quadSize, -quadSize, -quadSize).setUv(uvRange.maxU(), uvRange.minV());
            }

            //? >=1.21.5
            skyBufferIndices = RenderSystem.getSequentialBuffer(vertexFormatMode);
            try (final MeshData meshData = builder.buildOrThrow()) {
                //? >=1.21.5 {
                skyBufferIndexCount = meshData.drawState().indexCount();
                skyBuffer = RenderSystem.getDevice().createBuffer(
                        () -> "OptiFine Skybox",
                        //? >=1.21.6 {
                        com.mojang.blaze3d.buffers.GpuBuffer.USAGE_COPY_DST,
                        //?} else {
                        /*com.mojang.blaze3d.buffers.BufferType.VERTICES, com.mojang.blaze3d.buffers.BufferUsage.STATIC_WRITE,
                         *///?}
                        meshData.vertexBuffer()
                );
                //?} else {
                /*skyBuffer = new com.mojang.blaze3d.vertex.VertexBuffer(com.mojang.blaze3d.buffers.BufferUsage.STATIC_WRITE);
                skyBuffer.bind();
                skyBuffer.upload(meshData);
                com.mojang.blaze3d.vertex.VertexBuffer.unbind();
                *///?}
            }
        }
    }

    //? >=1.21.5 {
    private final java.util.Map<net.minecraft.resources.ResourceLocation, com.mojang.blaze3d.pipeline.RenderPipeline> renderPipelineCache = new java.util.HashMap<>();

    public static com.mojang.blaze3d.pipeline.RenderPipeline getSkyboxPipeline(@org.jetbrains.annotations.Nullable BlendFunction blendFunction) {
        final com.mojang.blaze3d.pipeline.RenderPipeline.Builder builder = com.mojang.blaze3d.pipeline.RenderPipeline.builder(RenderPipelinesAccessor.skyboxify$getMatricesProjectionSnippet());
        builder.withLocation(btw.lowercase.skyboxify.Skyboxify.locationOrNull("pipeline/custom_skybox"));
        builder.withVertexShader(btw.lowercase.skyboxify.Skyboxify.locationOrNull("core/custom_skybox"));
        builder.withFragmentShader(btw.lowercase.skyboxify.Skyboxify.locationOrNull("core/custom_skybox"));
        builder.withDepthWrite(false);
        builder.withColorWrite(true, false);
        if (blendFunction != null) {
            builder.withBlend(blendFunction.toNative());
        }
        builder.withSampler("Sampler0");
        builder.withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS);
        return builder.build();
    }
    //?}

    public void renderSkybox(OptiFineSkybox optiFineSkybox, Matrix4f modelViewMatrix, ClientLevel level, float tickDelta) {
        final long dayTime = level.getDayTime();
        final int clampedTimeOfDay = (int) (dayTime % 24000L);
        final float skyAngle = getTimeOfDay(level);
        final float rainLevel = level.getRainLevel(tickDelta);
        float thunderLevel = level.getThunderLevel(tickDelta);
        if (rainLevel > 0.0F) {
            thunderLevel /= rainLevel;
        }

        for (OptiFineSkyLayer optiFineSkyLayer : optiFineSkybox.getLayers().stream().filter(layer -> layer.isActive(dayTime, clampedTimeOfDay)).toList()) {
            renderSkyLayer(optiFineSkyLayer, modelViewMatrix, level, clampedTimeOfDay, skyAngle, rainLevel, thunderLevel, optiFineSkybox.getConditionAlphaFor(optiFineSkyLayer));
        }
    }

    public void renderSkyLayer(OptiFineSkyLayer optiFineSkyLayer, Matrix4f modelViewMatrix, Level level, int timeOfDay, float skyAngle, float rainLevel, float thunderLevel, float conditionAlpha) {
        final float weatherAlpha = CommonUtils.getWeatherAlpha(optiFineSkyLayer.weatherConditions(), rainLevel, thunderLevel);
        final float fadeAlpha = optiFineSkyLayer.fade().getAlpha(timeOfDay);
        final float finalAlpha = Mth.clamp(conditionAlpha * weatherAlpha * fadeAlpha, 0.0F, 1.0F);
        if (!(finalAlpha < 1.0E-4F) && this.skyBuffer != null) {
            if (optiFineSkyLayer.rotate()) {
                // NOTE: Using `mulPose` directly gives a different result.
                modelViewMatrix.rotate(new Quaternionf(new AxisAngle4f(this.getAngle(level, skyAngle, optiFineSkyLayer.speed()), optiFineSkyLayer.axis())));
            }

            //? <=1.21.4 {
            /*RenderSystem.setShaderTexture(0, optiFineSkyLayer.source());
            RenderSystem.setShader(net.minecraft.client.renderer.CoreShaders.POSITION_TEX);
            RenderSystem.depthMask(false);
            optiFineSkyLayer.blend().apply(finalAlpha);
            *///?}

            //? >=1.21.5 {
            //? >=1.21.6 {
            com.mojang.blaze3d.buffers.GpuBufferSlice transforms = DynamicTransformsBuilder.of()
                    .withModelViewMatrix(modelViewMatrix)
                    .withShaderColor(optiFineSkyLayer.blend().getShaderColor(finalAlpha))
                    .build();
            //?}

            com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline = this.renderPipelineCache.computeIfAbsent(optiFineSkyLayer.source(), (resourceLocation) -> {
                com.mojang.blaze3d.pipeline.RenderPipeline pipeline = getSkyboxPipeline(optiFineSkyLayer.blend().getBlendFunction());
                IrisUtil.assignPipeline(pipeline, IrisPipeline.SKY_TEXTURED);
                return pipeline;
            });

            Minecraft minecraft = Minecraft.getInstance();

            com.mojang.blaze3d.pipeline.RenderTarget renderTarget = minecraft.getMainRenderTarget();
            net.minecraft.client.renderer.texture.AbstractTexture skyTexture = minecraft.getTextureManager().getTexture(optiFineSkyLayer.source());

            //? >=1.21.6 {
            com.mojang.blaze3d.textures.GpuTextureView colorTexture = renderTarget.getColorTextureView();
            com.mojang.blaze3d.textures.GpuTextureView depthTexture = renderTarget.getDepthTextureView();
            //?} else {
            /*com.mojang.blaze3d.textures.GpuTexture colorTexture = renderTarget.getColorTexture();
            com.mojang.blaze3d.textures.GpuTexture depthTexture = renderTarget.getDepthTexture();
            *///?}

            com.mojang.blaze3d.buffers.GpuBuffer indexBuffer = this.skyBufferIndices.getBuffer(this.skyBufferIndexCount);
            try (com.mojang.blaze3d.systems.RenderPass renderPass = RenderSystem.getDevice()
                    .createCommandEncoder()
                    .createRenderPass(
                            //? >=1.21.6
                            () -> "Custom Sky Rendering",
                            colorTexture,
                            java.util.OptionalInt.empty(),
                            depthTexture,
                            java.util.OptionalDouble.empty()
                    )) {
                renderPass.setPipeline(renderPipeline);
                renderPass.setVertexBuffer(0, this.skyBuffer);
                renderPass.setIndexBuffer(indexBuffer, this.skyBufferIndices.type());

                //? >=1.21.6 {
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("DynamicTransforms", transforms);
                //?}

                //? >=1.21.11 {
                /*renderPass.bindTexture("Sampler0", skyTexture.getTextureView(), skyTexture.getSampler());
                 *///?} else >= 1.21.6 {
                renderPass.bindSampler("Sampler0", skyTexture.getTextureView());
                //?} else {
                /*renderPass.bindSampler("Sampler0", skyTexture.getTexture());
                 *///?}

                //? >=1.21.6 {
                renderPass.drawIndexed(0, 0, this.skyBufferIndexCount, 1);
                //?} else {
                /*renderPass.drawIndexed(0, this.skyBufferIndexCount);
                 *///?}
            }
            //?} else {
            /*this.skyBuffer.bind();
            this.skyBuffer.drawWithShader(modelViewMatrix, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
            com.mojang.blaze3d.vertex.VertexBuffer.unbind();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            *///?}
        }
    }

    private float getAngle(Level level, float skyAngle, float speed) {
        float angleDayStart = 0.0F;
        if (speed != (float) Math.round(speed)) {
            final long currentWorldDay = (level.getDayTime() + 18000L) / 24000L;
            final double anglePerDay = speed % 1.0F;
            final double currentAngle = (double) currentWorldDay * anglePerDay;
            angleDayStart = (float) (currentAngle % 1.0D);
        }

        return (float) Math.toRadians(360.0F * (angleDayStart + skyAngle * speed));
    }

    public void clearCache() {
        //? >=1.21.5 {
        this.renderPipelineCache.clear();
        //?}
    }

    private float getTimeOfDay(Level level) {
        //? >=1.21.11 {
        /*long fixedTime = level.getDayTime();
        if (level.dimensionType().hasFixedTime()) {
            if (level.dimension().equals(Level.NETHER)) {
                fixedTime = 18000L;
            } else if (level.dimension().equals(Level.END)) {
                fixedTime = 6000L;
            }
        }

        final double frac = Mth.frac(fixedTime / 24000.0 - 0.25);
        final double mul = 0.5 - Math.cos(frac * Math.PI) / 2.0;
        return (float)(frac * 2.0 + mul) / 3.0F;
        *///?} else {
        return level.getTimeOfDay(1.0F);
        //?}
    }
}