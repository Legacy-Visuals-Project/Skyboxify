package btw.lowercase.optiboxes.skybox;

import btw.lowercase.optiboxes.utils.CommonUtils;
import btw.lowercase.optiboxes.utils.SkyMapping;
import btw.lowercase.optiboxes.utils.SkyPart;
import btw.lowercase.optiboxes.utils.UVRange;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public final class OptiFineSkyRenderer {
    public static final OptiFineSkyRenderer INSTANCE = new OptiFineSkyRenderer();
    private static final Map<SkyPart, SkyMapping> SKY_MAPPING = new HashMap<>();

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
        VertexFormat vertexFormat = DefaultVertexFormat.POSITION_TEX;
        VertexFormat.Mode vertexFormatMode = VertexFormat.Mode.QUADS;

        ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(vertexFormat.getVertexSize() * SkyPart.COUNT * 4);
        BufferBuilder builder = new BufferBuilder(byteBufferBuilder, vertexFormatMode, vertexFormat);
        for (SkyPart part : SkyPart.VALUES) {
            SkyMapping skyMapping = SKY_MAPPING.get(part);
            Matrix4f matrix4f = skyMapping.rotationMatrix();
            UVRange uvRange = skyMapping.mapping();
            final float quadSize = 100.0F;
            builder.addVertex(CommonUtils.getMatrixTransform(matrix4f, -quadSize, -quadSize, -quadSize)).setUv(uvRange.minU(), uvRange.minV());
            builder.addVertex(CommonUtils.getMatrixTransform(matrix4f, -quadSize, -quadSize, quadSize)).setUv(uvRange.minU(), uvRange.maxV());
            builder.addVertex(CommonUtils.getMatrixTransform(matrix4f, quadSize, -quadSize, quadSize)).setUv(uvRange.maxU(), uvRange.maxV());
            builder.addVertex(CommonUtils.getMatrixTransform(matrix4f, quadSize, -quadSize, -quadSize)).setUv(uvRange.maxU(), uvRange.minV());
        }

        //? >=1.21.5
        skyBufferIndices = RenderSystem.getSequentialBuffer(vertexFormatMode);
        try (MeshData meshData = builder.buildOrThrow()) {
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

    //? >=1.21.5 {
    private final java.util.Map<net.minecraft.resources.ResourceLocation, com.mojang.blaze3d.pipeline.RenderPipeline> renderPipelineCache = new java.util.HashMap<>();

    public static com.mojang.blaze3d.pipeline.RenderPipeline getSkyboxPipeline(@org.jetbrains.annotations.Nullable btw.lowercase.optiboxes.utils.BlendFunction blendFunction) {
        com.mojang.blaze3d.pipeline.RenderPipeline.Builder builder = com.mojang.blaze3d.pipeline.RenderPipeline.builder(btw.lowercase.optiboxes.mixins.RenderPipelinesAccessor.optiboxes$getMatricesProjectionSnippet());
        builder.withLocation(btw.lowercase.optiboxes.OptiBoxesClient.id("pipeline/custom_skybox"));
        builder.withVertexShader(btw.lowercase.optiboxes.OptiBoxesClient.id("core/custom_skybox"));
        builder.withFragmentShader(btw.lowercase.optiboxes.OptiBoxesClient.id("core/custom_skybox"));
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

    public void renderSkybox(OptiFineSkybox optiFineSkybox, Matrix4fStack modelViewStack, Level level, float tickDelta) {
        long dayTime = level.getDayTime();
        int clampedTimeOfDay = (int) (dayTime % 24000L);
        float skyAngle = level.getTimeOfDay(tickDelta);
        float thunderLevel = level.getThunderLevel(tickDelta);
        float rainLevel = level.getRainLevel(tickDelta);
        if (rainLevel > 0.0F) {
            thunderLevel /= rainLevel;
        }

        for (OptiFineSkyLayer optiFineSkyLayer : optiFineSkybox.getLayers().stream().filter(layer -> layer.isActive(dayTime, clampedTimeOfDay)).toList()) {
            renderSkyLayer(optiFineSkyLayer, modelViewStack, level, clampedTimeOfDay, skyAngle, rainLevel, thunderLevel, optiFineSkybox.getConditionAlphaFor(optiFineSkyLayer));
        }

        //? <=1.21.4 {
        /*btw.lowercase.optiboxes.utils.components.Blend.ADD.apply(1.0F - rainLevel);
         *///?}
    }

    public void renderSkyLayer(OptiFineSkyLayer optiFineSkyLayer, Matrix4fStack modelViewStack, Level level, int timeOfDay, float skyAngle, float rainGradient, float thunderGradient, float conditionAlpha) {
        float weatherAlpha = CommonUtils.getWeatherAlpha(optiFineSkyLayer.weatherConditions(), rainGradient, thunderGradient);
        float fadeAlpha = optiFineSkyLayer.fade().getAlpha(timeOfDay);
        float finalAlpha = Mth.clamp(conditionAlpha * weatherAlpha * fadeAlpha, 0.0F, 1.0F);
        if (!(finalAlpha < 1.0E-4F) && this.skyBuffer != null) {
            modelViewStack.pushMatrix();
            if (optiFineSkyLayer.rotate()) {
                // NOTE: Using `mulPose` directly gives a different result.
                modelViewStack.rotate(new Quaternionf(new AxisAngle4f(this.getAngle(level, skyAngle, optiFineSkyLayer.speed()), optiFineSkyLayer.axis())));
            }

            //? <=1.21.4 {
            /*RenderSystem.setShaderTexture(0, optiFineSkyLayer.source());
            RenderSystem.setShader(net.minecraft.client.renderer.CoreShaders.POSITION_TEX); // TODO: Use the custom_skybox shader
            optiFineSkyLayer.blend().apply(finalAlpha);
            *///?}

            //? >=1.21.5 {
            //? >=1.21.6 {
            com.mojang.blaze3d.buffers.GpuBufferSlice transforms = btw.lowercase.optiboxes.utils.DynamicTransformsBuilder.of()
                    .withModelViewMatrix(modelViewStack)
                    .withShaderColor(optiFineSkyLayer.blend().getShaderColor(finalAlpha))
                    .build();
            //?}

            com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline = this.renderPipelineCache.computeIfAbsent(optiFineSkyLayer.source(), (resourceLocation) -> {
                com.mojang.blaze3d.pipeline.RenderPipeline pipeline = getSkyboxPipeline(optiFineSkyLayer.blend().getBlendFunction());
                btw.lowercase.optiboxes.utils.IrisUtil.assignPipeline(pipeline, btw.lowercase.optiboxes.utils.IrisUtil.skyTextured());
                return pipeline;
            });

            com.mojang.blaze3d.pipeline.RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();
            net.minecraft.client.renderer.texture.AbstractTexture skyTexture = Minecraft.getInstance().getTextureManager().getTexture(optiFineSkyLayer.source());

            //? >=1.21.6 {
            com.mojang.blaze3d.textures.GpuTextureView texture = skyTexture.getTextureView();
            com.mojang.blaze3d.textures.GpuTextureView colorTexture = renderTarget.getColorTextureView();
            com.mojang.blaze3d.textures.GpuTextureView depthTexture = renderTarget.getDepthTextureView();
            //?} else {
            /*com.mojang.blaze3d.textures.GpuTexture texture = skyTexture.getTexture();
            com.mojang.blaze3d.textures.GpuTexture colorTexture = renderTarget.getColorTexture();
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
                renderPass.bindSampler("Sampler0", texture);
                //? >=1.21.6 {
                renderPass.drawIndexed(0, 0, this.skyBufferIndexCount, 1);
                //?} else {
                /*renderPass.drawIndexed(0, this.skyBufferIndexCount);
                 *///?}
            }
            //?} else {
            /*this.skyBuffer.bind();
            this.skyBuffer.drawWithShader(modelViewStack, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
            com.mojang.blaze3d.vertex.VertexBuffer.unbind();
            *///?}

            modelViewStack.popMatrix();
        }
    }

    private float getAngle(Level level, float skyAngle, float speed) {
        float angleDayStart = 0.0F;
        if (speed != (float) Math.round(speed)) {
            long currentWorldDay = (level.getDayTime() + 18000L) / 24000L;
            double anglePerDay = speed % 1.0F;
            double currentAngle = (double) currentWorldDay * anglePerDay;
            angleDayStart = (float) (currentAngle % 1.0D);
        }

        return (float) Math.toRadians(360.0F * (angleDayStart + skyAngle * speed));
    }

    public void clearCache() {
        //? >=1.21.5 {
        this.renderPipelineCache.clear();
        //?}
    }

    static {
        SKY_MAPPING.put(SkyPart.BOTTOM, new SkyMapping(
                new Matrix4f().rotateY((float) Math.toRadians(90.0F)),
                new UVRange(0.0F, 0.0F, 0.33333334F, 0.5F)
        ));
        SKY_MAPPING.put(SkyPart.TOP, new SkyMapping(
                new Matrix4f().rotateX((float) Math.toRadians(180.0F)).rotateY((float) Math.toRadians(-90.0F)),
                new UVRange(0.33333334F, 0.0F, 0.6666667F, 0.5F)
        ));
        SKY_MAPPING.put(SkyPart.EAST, new SkyMapping(
                new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(90.0F)),
                new UVRange(0.6666667F, 0.0F, 1.0F, 0.5F)
        ));
        SKY_MAPPING.put(SkyPart.SOUTH, new SkyMapping(
                new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(180.0F)),
                new UVRange(0.0F, 0.5F, 0.33333334F, 1.0F)
        ));
        SKY_MAPPING.put(SkyPart.WEST, new SkyMapping(
                new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(-90.0F)),
                new UVRange(0.33333334F, 0.5F, 0.6666667F, 1.0F)
        ));
        SKY_MAPPING.put(SkyPart.NORTH, new SkyMapping(
                new Matrix4f().rotateX((float) Math.toRadians(90.0F)),
                new UVRange(0.6666667F, 0.5F, 1.0F, 1.0F)
        ));
    }
}
