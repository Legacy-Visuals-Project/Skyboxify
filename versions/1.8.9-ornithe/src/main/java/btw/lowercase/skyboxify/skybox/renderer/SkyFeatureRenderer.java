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
import btw.lowercase.skyboxify.utils.Id;
import btw.lowercase.skyboxify.utils.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.pipeline.RenderTarget;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.client.render.texture.TextureManager;
import net.minecraft.resource.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class SkyFeatureRenderer extends FeatureRenderer<SkyFeatureRenderer.Submit> {
    public SkyFeatureRenderer(final RenderTarget renderTarget) {
        super(renderTarget);
    }

    @Override
    protected Submit createSubmit(final Pipeline pipeline, final Geometry geometry, final RenderUniforms uniforms, final Id location) {
        return new Submit(
                location.vanilla(),
                pipeline.blendFunction(),
                SkyboxifyImpl.config().filteringMode.getValue() == FilteringMode.LINEAR,
                geometry,
                uniforms
        );
    }

    @Override
    public void endFrame() {
        if (!this.submits.isEmpty()) {
            final TextureManager textureManager = Minecraft.getInstance().getTextureManager();
            final Matrix4f backupModelView = ShaderUtil.captureModelView();
            for (final Submit submit : this.submits) {
                if (submit.geometry.isClosed()) {
                    throw new RuntimeException("Cannot render closed geometry!");
                }

                this.setupGlState(textureManager, submit);
                submit.geometry.draw();
                this.resetGlState(textureManager, submit);
            }

            ShaderUtil.applyWhite();
            ShaderUtil.applyModelView(backupModelView);
            super.endFrame();
        }
    }

    private void setupGlState(final TextureManager textureManager, final Submit submit) {
        final Texture texture = textureManager.get(submit.location);
        GlStateManager.bindTexture(texture.getGlId());
        texture.pushFilter(submit.blur, false);

        ShaderUtil.applyColor(submit.uniforms.shaderColor());
        GlStateManager.depthMask(false);

        final BlendFunction blendFunction = submit.blend;
        if (blendFunction != null) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(blendFunction.srcFactor().vanilla(), blendFunction.dstFactor().vanilla());
        } else {
            GlStateManager.disableBlend();
        }

        this.renderTarget.bindWrite(false);
        ShaderUtil.applyModelView(submit.uniforms.modelViewMatrix());
    }

    private void resetGlState(final TextureManager textureManager, final Submit submit) {
        Minecraft.getInstance().getRenderTarget().bindWrite(false); // Restore Main Render Target
        ShaderUtil.applyWhite(); // Set Color Modulator to White
        textureManager.get(submit.location).popFilter(); // Pop Filter (blur)
        GlStateManager.bindTexture(0); // Clear Active Texture
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
