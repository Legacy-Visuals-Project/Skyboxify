package btw.lowercase.optiboxes.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;

public final class Renderer {
    private static Color shaderColor = new Color();
    private static Matrix4fStack modelViewMatrix;
    //? >=1.21.5 {
    private static com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline;
    //? }

    public static void setModelViewMatrix(Matrix4fStack matrix4fStack) {
        modelViewMatrix = matrix4fStack;
    }

    public static void setShaderColor(float red, float green, float blue, float alpha) {
        shaderColor = new Color(red, green, blue, alpha);
    }

    public static void setShaderColor(float red, float green, float blue) {
        setShaderColor(red, green, blue, 1.0F);
    }

    //? >=1.21.5 {
    public static void setRenderPipeline(com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline) {
        renderPipeline = renderPipeline;
    }
    //? } else {
    /*public static void setShader(CompiledShaderProgram shaderProgram) {
        RenderSystem.setShader(shaderProgram);
    }
    *///? }

    public static void drawWithShader(
            //? >=1.21.5 {
            com.mojang.blaze3d.buffers.GpuBuffer vertexBuffer
            //? else {
            /*com.mojang.blaze3d.vertex.VertexBuffer vertexBuffer
             *///? }
    ) {
        //? >=1.21.5
        //? >=1.21.6
        com.mojang.blaze3d.buffers.GpuBufferSlice transforms = btw.lowercase.optiboxes.utils.DynamicTransformsBuilder.of()
                .withModelViewMatrix(modelViewMatrix)
                .withShaderColor(shaderColor.vector4f())
                .build();

        com.mojang.blaze3d.pipeline.RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();

        //? >=1.21.6 {
        com.mojang.blaze3d.textures.GpuTextureView colorTexture = renderTarget.getColorTextureView();
        com.mojang.blaze3d.textures.GpuTextureView depthTexture = renderTarget.getDepthTextureView();
        //?} else {
        /*com.mojang.blaze3d.textures.GpuTexture colorTexture = renderTarget.getColorTexture();
        com.mojang.blaze3d.textures.GpuTexture depthTexture = renderTarget.getDepthTexture();
        *///?}

        try (com.mojang.blaze3d.systems.RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                //? >=1.21.6
                () -> "",
                colorTexture,
                java.util.OptionalInt.empty(),
                depthTexture,
                java.util.OptionalDouble.empty()
        )) {
            renderPass.setPipeline(renderPipeline);
            renderPass.setVertexBuffer(0, vertexBuffer);
            for (int i = 0; i < 9; ++i) {
                renderPass.bindSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
            }
            //? >=1.21.6
            renderPass.setUniform("DynamicTransforms", transforms);
            renderPass.draw(0, vertexBuffer.size()); // TODO: wrong
        }

        //? }
        //? } else {
        /*RenderSystem.setShaderColor(shaderColor.red(), shaderColor.green(), shaderColor.blue(), shaderColor.alpha());
        vertexBuffer.bind();
        vertexBuffer.drawWithShader(modelViewMatrix, projectionMatrix, RenderSystem.getShader());
        com.mojang.blaze3d.vertex.VertexBuffer.unbind();
         *///? }
    }

    private record Color(float red, float green, float blue, float alpha) {
        public Color() {
            this(1.0F, 1.0F, 1.0F, 1.0F);
        }

        public Vector4f vector4f() {
            return new Vector4f(red, green, blue, alpha);
        }
    }
}
