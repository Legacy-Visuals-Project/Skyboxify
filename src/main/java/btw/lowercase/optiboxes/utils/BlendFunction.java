package btw.lowercase.optiboxes.utils;

//? >=1.21.5 {
import org.lwjgl.opengl.GL11;
//?}

public record BlendFunction(int srcFactor, int dstFactor) {
    //? >=1.21.5 {
    public com.mojang.blaze3d.pipeline.BlendFunction toNative() {
        // TODO: Improve
        return new com.mojang.blaze3d.pipeline.BlendFunction(
                switch (this.srcFactor) {
                    case GL11.GL_SRC_COLOR -> com.mojang.blaze3d.platform.SourceFactor.SRC_COLOR;
                    case GL11.GL_SRC_ALPHA -> com.mojang.blaze3d.platform.SourceFactor.SRC_ALPHA;
                    case GL11.GL_DST_COLOR -> com.mojang.blaze3d.platform.SourceFactor.DST_COLOR;
                    case GL11.GL_DST_ALPHA -> com.mojang.blaze3d.platform.SourceFactor.DST_ALPHA;
                    case GL11.GL_ONE_MINUS_SRC_COLOR -> com.mojang.blaze3d.platform.SourceFactor.ONE_MINUS_SRC_COLOR;
                    case GL11.GL_ONE_MINUS_SRC_ALPHA -> com.mojang.blaze3d.platform.SourceFactor.ONE_MINUS_SRC_ALPHA;
                    case GL11.GL_ONE_MINUS_DST_COLOR -> com.mojang.blaze3d.platform.SourceFactor.ONE_MINUS_DST_COLOR;
                    case GL11.GL_ONE_MINUS_DST_ALPHA -> com.mojang.blaze3d.platform.SourceFactor.ONE_MINUS_DST_ALPHA;
                    case GL11.GL_ONE -> com.mojang.blaze3d.platform.SourceFactor.ONE;
                    case GL11.GL_ZERO -> com.mojang.blaze3d.platform.SourceFactor.ZERO;
                    case GL11.GL_SRC_ALPHA_SATURATE -> com.mojang.blaze3d.platform.SourceFactor.SRC_ALPHA_SATURATE;
                    default -> throw new RuntimeException("Unknown blend src factor " + this.srcFactor);
                },
                switch (this.dstFactor) {
                    case GL11.GL_SRC_COLOR -> com.mojang.blaze3d.platform.DestFactor.SRC_COLOR;
                    case GL11.GL_SRC_ALPHA -> com.mojang.blaze3d.platform.DestFactor.SRC_ALPHA;
                    case GL11.GL_DST_COLOR -> com.mojang.blaze3d.platform.DestFactor.DST_COLOR;
                    case GL11.GL_DST_ALPHA -> com.mojang.blaze3d.platform.DestFactor.DST_ALPHA;
                    case GL11.GL_ONE_MINUS_SRC_COLOR -> com.mojang.blaze3d.platform.DestFactor.ONE_MINUS_SRC_COLOR;
                    case GL11.GL_ONE_MINUS_SRC_ALPHA -> com.mojang.blaze3d.platform.DestFactor.ONE_MINUS_SRC_ALPHA;
                    case GL11.GL_ONE_MINUS_DST_COLOR -> com.mojang.blaze3d.platform.DestFactor.ONE_MINUS_DST_COLOR;
                    case GL11.GL_ONE_MINUS_DST_ALPHA -> com.mojang.blaze3d.platform.DestFactor.ONE_MINUS_DST_ALPHA;
                    case GL11.GL_ONE -> com.mojang.blaze3d.platform.DestFactor.ONE;
                    case GL11.GL_ZERO -> com.mojang.blaze3d.platform.DestFactor.ZERO;
                    default -> throw new RuntimeException("Unknown blend dest factor " + this.dstFactor);
                });
    }
    //?}
}
