package btw.lowercase.optiboxes.skybox.components;

import btw.lowercase.optiboxes.utils.BlendFunction;
import com.mojang.serialization.Codec;
import lombok.Getter;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public enum Blend implements StringRepresentable {
    ADD(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), new BlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE)),
    SUBTRACT(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO)),
    MULTIPLY(alpha -> new Vector4f(alpha, alpha, alpha, alpha), new BlendFunction(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA)),
    DODGE(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ONE, GL11.GL_ONE)),
    BURN(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR)),
    SCREEN(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR)),
    REPLACE(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), null),
    OVERLAY(alpha -> new Vector4f(alpha, alpha, alpha, 1.0F), new BlendFunction(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR)),
    ALPHA(alpha -> new Vector4f(1.0F, 1.0F, 1.0F, alpha), new BlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA));

    public static final Codec<Blend> CODEC = StringRepresentable.fromEnum(Blend::values);

    private final Function<Float, Vector4f> blendConsumer;
    @Getter
    private final BlendFunction blendFunction;

    Blend(Function<Float, Vector4f> blendConsumer, BlendFunction blendFunction) {
        this.blendConsumer = blendConsumer;
        this.blendFunction = blendFunction;
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

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase();
    }
}
