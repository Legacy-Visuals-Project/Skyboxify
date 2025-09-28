package btw.lowercase.optiboxes.utils.components;

import btw.lowercase.optiboxes.utils.BlendFunction;
import com.mojang.serialization.Codec;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.function.Function;

public enum Blend {
    ALPHA(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), new BlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)),
    ADD(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), new BlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE)),
    SUBTRACT(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO)),
    MULTIPLY(alpha -> new Vector4f(alpha, alpha, alpha, alpha), new BlendFunction(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA)),
    DODGE(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ONE, GL11.GL_ONE)),
    BURN(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR)),
    SCREEN(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR)),
    OVERLAY(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR)),
    REPLACE(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), null);

    public static final Codec<Blend> CODEC = Codec.STRING.xmap(Blend::byName, Blend::toString);

    private final Function<Float, Vector4f> blendConsumer;
    private final BlendFunction blendFunction;

    Blend(Function<Float, Vector4f> blendConsumer, BlendFunction blendFunction) {
        this.blendConsumer = blendConsumer;
        this.blendFunction = blendFunction;
    }

    public static Blend byName(String name) {
        return Arrays.stream(Blend.values())
                .filter(blend -> blend.toString().toLowerCase().equals(name))
                .findFirst()
                .orElse(ADD);
    }

    public Vector4f getShaderColor(float value) {
        return this.blendConsumer.apply(value);
    }

    //? <=1.21.4 {
    /*public void apply(float value) {
        final Vector4f shaderColor = getShaderColor(value);
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(shaderColor.x, shaderColor.y, shaderColor.z, shaderColor.w);
        if (this.blendFunction != null) {
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.blendFunc(this.blendFunction.srcFactor(), this.blendFunction.dstFactor());
        } else {
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        }
    }
    *///?}

    public BlendFunction getBlendFunction() {
        return this.blendFunction;
    }
}
