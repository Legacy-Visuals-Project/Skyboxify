package btw.lowercase.optiboxes.utils;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;

public final class Renderer {
    private static final Renderer INSTANCE = new Renderer();

    private Color shaderColor = new Color();
    private Matrix4fStack modelViewMatrix;
    private Matrix4f textureMatrix;

    public static void setModelViewMatrix(Matrix4fStack matrix4fStack) {
        INSTANCE.modelViewMatrix = matrix4fStack;
    }

    public static void setTextureMatrix(Matrix4f matrix4f) {
        INSTANCE.textureMatrix = matrix4f;
    }

    public static void setShaderColor(float red, float green, float blue, float alpha) {
        INSTANCE.shaderColor = new Color(red, green, blue, alpha);
    }

    public static void setShaderColor(float red, float green, float blue) {
        setShaderColor(red, green, blue, 1.0F);
    }

    public static void drawWithShader() {
        //? >=1.21.5
        //? >=1.21.6
        com.mojang.blaze3d.buffers.GpuBufferSlice transforms = btw.lowercase.optiboxes.utils.DynamicTransformsBuilder.of()
                .withModelViewMatrix(INSTANCE.modelViewMatrix)
                .withTextureMatrix(INSTANCE.textureMatrix)
                .withShaderColor(INSTANCE.shaderColor.vector4f())
                .build();
        //? }
        //? } else {
        /*
        RenderSystem.setTextureMatrix(INSTANCE.textureMatrix);
        RenderSystem.setShaderColor(INSTANCE.shaderColor.red(), INSTANCE.shaderColor.green(), INSTANCE.shaderColor.blue(), INSTANCE.shaderColor.alpha());
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
