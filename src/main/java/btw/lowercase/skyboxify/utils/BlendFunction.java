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

package btw.lowercase.skyboxify.utils;

//? >=1.21.6 {
import org.lwjgl.opengl.GL11;
//?}

public record BlendFunction(int srcFactor, int dstFactor) {
    //? >=1.21.6 {
    public com.mojang.blaze3d.pipeline.BlendFunction vanilla() {
        return new com.mojang.blaze3d.pipeline.BlendFunction(
                switch (this.srcFactor) {
                    //? >=26.2 {
                    case GL11.GL_SRC_COLOR -> com.mojang.blaze3d.platform.BlendFactor.SRC_COLOR;
                    case GL11.GL_SRC_ALPHA -> com.mojang.blaze3d.platform.BlendFactor.SRC_ALPHA;
                    case GL11.GL_DST_COLOR -> com.mojang.blaze3d.platform.BlendFactor.DST_COLOR;
                    case GL11.GL_DST_ALPHA -> com.mojang.blaze3d.platform.BlendFactor.DST_ALPHA;
                    case GL11.GL_ONE_MINUS_SRC_COLOR -> com.mojang.blaze3d.platform.BlendFactor.ONE_MINUS_SRC_COLOR;
                    case GL11.GL_ONE_MINUS_SRC_ALPHA -> com.mojang.blaze3d.platform.BlendFactor.ONE_MINUS_SRC_ALPHA;
                    case GL11.GL_ONE_MINUS_DST_COLOR -> com.mojang.blaze3d.platform.BlendFactor.ONE_MINUS_DST_COLOR;
                    case GL11.GL_ONE_MINUS_DST_ALPHA -> com.mojang.blaze3d.platform.BlendFactor.ONE_MINUS_DST_ALPHA;
                    case GL11.GL_ONE -> com.mojang.blaze3d.platform.BlendFactor.ONE;
                    case GL11.GL_ZERO -> com.mojang.blaze3d.platform.BlendFactor.ZERO;
                    case GL11.GL_SRC_ALPHA_SATURATE -> com.mojang.blaze3d.platform.BlendFactor.SRC_ALPHA_SATURATE;
                    //? } else {
                    /*case GL11.GL_SRC_COLOR -> com.mojang.blaze3d.platform.SourceFactor.SRC_COLOR;
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
                    *///? }
                    default -> throw new RuntimeException("Unknown blend src factor " + this.srcFactor);
                },
                switch (this.dstFactor) {
                    //? >=26.2 {
                    case GL11.GL_SRC_COLOR -> com.mojang.blaze3d.platform.BlendFactor.SRC_COLOR;
                    case GL11.GL_SRC_ALPHA -> com.mojang.blaze3d.platform.BlendFactor.SRC_ALPHA;
                    case GL11.GL_DST_COLOR -> com.mojang.blaze3d.platform.BlendFactor.DST_COLOR;
                    case GL11.GL_DST_ALPHA -> com.mojang.blaze3d.platform.BlendFactor.DST_ALPHA;
                    case GL11.GL_ONE_MINUS_SRC_COLOR -> com.mojang.blaze3d.platform.BlendFactor.ONE_MINUS_SRC_COLOR;
                    case GL11.GL_ONE_MINUS_SRC_ALPHA -> com.mojang.blaze3d.platform.BlendFactor.ONE_MINUS_SRC_ALPHA;
                    case GL11.GL_ONE_MINUS_DST_COLOR -> com.mojang.blaze3d.platform.BlendFactor.ONE_MINUS_DST_COLOR;
                    case GL11.GL_ONE_MINUS_DST_ALPHA -> com.mojang.blaze3d.platform.BlendFactor.ONE_MINUS_DST_ALPHA;
                    case GL11.GL_ONE -> com.mojang.blaze3d.platform.BlendFactor.ONE;
                    case GL11.GL_ZERO -> com.mojang.blaze3d.platform.BlendFactor.ZERO;
                    //? } else {
                    /*case GL11.GL_SRC_COLOR -> com.mojang.blaze3d.platform.DestFactor.SRC_COLOR;
                    case GL11.GL_SRC_ALPHA -> com.mojang.blaze3d.platform.DestFactor.SRC_ALPHA;
                    case GL11.GL_DST_COLOR -> com.mojang.blaze3d.platform.DestFactor.DST_COLOR;
                    case GL11.GL_DST_ALPHA -> com.mojang.blaze3d.platform.DestFactor.DST_ALPHA;
                    case GL11.GL_ONE_MINUS_SRC_COLOR -> com.mojang.blaze3d.platform.DestFactor.ONE_MINUS_SRC_COLOR;
                    case GL11.GL_ONE_MINUS_SRC_ALPHA -> com.mojang.blaze3d.platform.DestFactor.ONE_MINUS_SRC_ALPHA;
                    case GL11.GL_ONE_MINUS_DST_COLOR -> com.mojang.blaze3d.platform.DestFactor.ONE_MINUS_DST_COLOR;
                    case GL11.GL_ONE_MINUS_DST_ALPHA -> com.mojang.blaze3d.platform.DestFactor.ONE_MINUS_DST_ALPHA;
                    case GL11.GL_ONE -> com.mojang.blaze3d.platform.DestFactor.ONE;
                    case GL11.GL_ZERO -> com.mojang.blaze3d.platform.DestFactor.ZERO;
                    *///? }
                    default -> throw new RuntimeException("Unknown blend dest factor " + this.dstFactor);
                });
    }
    //?}
}
