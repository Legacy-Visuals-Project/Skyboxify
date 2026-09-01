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

import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.utils.BlendFunction;
import btw.lowercase.skyboxify.utils.FilteringMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.pipeline.RenderTarget;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.client.render.vertex.VertexBuffer;
import net.minecraft.resource.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL11;

public class SkyFeatureRenderer extends FeatureRenderer<SkyFeatureRenderer.Submit> {
    public SkyFeatureRenderer(final RenderTarget renderTarget) {
        super(renderTarget);
    }

    @Override
    protected Submit createSubmit(final Pipeline pipeline, final Geometry geometry, final RenderUniforms uniforms, final Identifier location) {
        return new Submit(
                location,
                pipeline.blendFunction(),
                SkyboxifyImpl.config().filteringMode.getValue() == FilteringMode.LINEAR,
                geometry,
                uniforms
        );
    }

    @Override
    public void endFrame() {
        if (!this.submits.isEmpty()) {
            for (final Submit submit : this.submits) {
                if (submit.geometry.isClosed()) {
                    throw new RuntimeException("Cannot render closed geometry!");
                }

                final VertexBuffer vertexBuffer = ((StaticGeometry) submit.geometry).vertexBuffer();
                final Vector4fc shaderColor = submit.uniforms.shaderColor();
                GlStateManager.color4f(shaderColor.x(), shaderColor.y(), shaderColor.z(), shaderColor.w());

                this.setupGlState(submit);
                vertexBuffer.bind();
                vertexBuffer.draw(GL11.GL_QUADS);
                vertexBuffer.unbind();
                this.resetGlState(submit);
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            super.endFrame();
        }
    }

    private void setupGlState(final Submit submit) {
        final Minecraft minecraft = Minecraft.getInstance();
        final Texture texture = minecraft.getTextureManager().get(submit.location);
        GlStateManager.bindTexture(texture.getGlId());
        texture.pushFilter(submit.blur, false);

        GlStateManager.depthMask(false);

        final BlendFunction blendFunction = submit.blend;
        if (blendFunction != null) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(blendFunction.srcFactor().vanilla(), blendFunction.dstFactor().vanilla());
        }

        this.renderTarget.bindWrite(true);
    }

    private void resetGlState(final Submit submit) {
        this.renderTarget.unbindWrite();

        final BlendFunction blendFunction = submit.blend;
        if (blendFunction != null) {
            GlStateManager.disableBlend();
            GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        }

        GlStateManager.depthMask(true);

        final Minecraft minecraft = Minecraft.getInstance();
        final Texture texture = minecraft.getTextureManager().get(submit.location);
        GlStateManager.bindTexture(0);
        texture.popFilter();
    }

    protected record Submit(
            Identifier location,
            @Nullable BlendFunction blend,
            boolean blur,
            Geometry geometry,
            RenderUniforms uniforms
    ) implements FeatureRenderer.Submit {
    }
}
