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

package btw.lowercase.skyboxify.skybox.renderer;

import btw.lowercase.skyboxify.skybox.SkyStorage;
import btw.lowercase.skyboxify.utils.BlendFunction;
import btw.lowercase.skyboxify.utils.CommonUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

public class SkyFeatureRenderer extends FeatureRenderer<SkyFeatureRenderer.Submit> {
    private final RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();

    @Override
    protected Submit createSubmit(final RenderUniforms uniforms, final ResourceLocation location) {
        return new Submit(uniforms, location);
    }

    @Override
    public void endFrame() {
        this.renderTarget.bindWrite(true);
        RenderSystem.setShader(SkyStorage.CUSTOM_SKYBOX_SHADER);
        RenderSystem.depthMask(false);
        RenderSystem.colorMask(true, true, true, false);
        for (final Map.Entry<Key, List<Submit>> entry : this.submits.entrySet()) {
            final Key key = entry.getKey();
            final Geometry geometry = key.geometry();
            if (geometry.isClosed()) {
                throw new RuntimeException("Cannot render closed geometry!");
            }

            final BlendFunction blendFunction = key.pipeline().blendFunction();
            if (blendFunction != null) {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(blendFunction.srcFactor(), blendFunction.dstFactor());
            } else {
                RenderSystem.disableBlend();
            }

            final VertexBuffer vertexBuffer = ((ReusableGeometry) geometry).vertexBuffer();
            vertexBuffer.bind();
            for (final Submit submit : entry.getValue()) {
                final Vector4f shaderColor = CommonUtils.unpackARGB(submit.uniforms.shaderColor());
                RenderSystem.setShaderColor(shaderColor.x, shaderColor.y, shaderColor.z, shaderColor.w);
                RenderSystem.setShaderTexture(0, submit.texture);
                vertexBuffer.drawWithShader(submit.uniforms.modelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
            }

            VertexBuffer.unbind();
            if (blendFunction != null) {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }
        }

        RenderSystem.colorMask(true, true, true, false);
        RenderSystem.depthMask(true);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderTarget.unbindWrite();

        this.clear();
    }

    protected record Submit(RenderUniforms uniforms, ResourceLocation texture) implements FeatureRenderer.Submit {
    }
}
