package btw.lowercase.optiboxes.skybox;

import btw.lowercase.optiboxes.utils.CommonUtils;
import btw.lowercase.optiboxes.utils.UVRange;
import btw.lowercase.optiboxes.utils.components.Blend;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

public final class OptiFineSkyRenderer {
    public static final OptiFineSkyRenderer INSTANCE = new OptiFineSkyRenderer();

    private VertexBuffer skyBuffer = null;

    private OptiFineSkyRenderer() {
        Minecraft.getInstance().tell(this::buildSky);
    }

    private void buildSky() {
        VertexFormat vertexFormat = DefaultVertexFormat.POSITION_TEX;
        VertexFormat.Mode vertexFormatMode = VertexFormat.Mode.QUADS;

        ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(vertexFormat.getVertexSize() * 24);
        BufferBuilder builder = new BufferBuilder(byteBufferBuilder, vertexFormatMode, vertexFormat);
        for (int face = 0; face < 6; ++face) {
            UVRange uvRange = CommonUtils.getUvRangeForFace(face);
            Matrix4f matrix4f = CommonUtils.getRotationMatrixForFace(face);
            final float quadSize = 100.0F;
            builder.addVertex(CommonUtils.getMatrixTransform(matrix4f, -quadSize, -quadSize, -quadSize)).setUv(uvRange.minU(), uvRange.minV());
            builder.addVertex(CommonUtils.getMatrixTransform(matrix4f, -quadSize, -quadSize, quadSize)).setUv(uvRange.minU(), uvRange.maxV());
            builder.addVertex(CommonUtils.getMatrixTransform(matrix4f, quadSize, -quadSize, quadSize)).setUv(uvRange.maxU(), uvRange.maxV());
            builder.addVertex(CommonUtils.getMatrixTransform(matrix4f, quadSize, -quadSize, -quadSize)).setUv(uvRange.maxU(), uvRange.minV());
        }

        this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.skyBuffer.bind();
        try (MeshData meshData = builder.buildOrThrow()) {
            this.skyBuffer.upload(meshData);
        }
        VertexBuffer.unbind();
    }

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

        Blend.ADD.apply(1.0F - rainLevel);
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

            RenderSystem.setShaderTexture(0, optiFineSkyLayer.source());
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            Blend blend = optiFineSkyLayer.blend();
            blend.apply(finalAlpha);
            if (blend.getBlendFunction() != null) {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(blend.getBlendFunction().sourceFactor(), blend.getBlendFunction().destFactor());
            } else {
                RenderSystem.disableBlend();
            }

            this.skyBuffer.bind();
            this.skyBuffer.drawWithShader(modelViewStack, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
            VertexBuffer.unbind();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
}
