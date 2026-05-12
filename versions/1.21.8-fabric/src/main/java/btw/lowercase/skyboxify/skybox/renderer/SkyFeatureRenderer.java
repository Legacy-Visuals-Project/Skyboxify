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

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class SkyFeatureRenderer extends FeatureRenderer<SkyFeatureRenderer.Submit> {
    public SkyFeatureRenderer(final RenderTarget renderTarget) {
        super(renderTarget);
    }

    @Override
    protected Submit createSubmit(final Pipeline pipeline, final Geometry geometry, final RenderUniforms uniforms, final ResourceLocation location) {
        final GpuTextureView textureView = Minecraft.getInstance().getTextureManager().getTexture(location).getTextureView();
        final GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(uniforms.modelViewMatrix(), uniforms.shaderColor(), new Vector3f(), new Matrix4f(), 1.0F);
        return new Submit(pipeline.pipeline(), geometry, uniforms, textureView, dynamicTransforms);
    }

    @Override
    public void endFrame() {
        if (!this.submits.isEmpty()) {
            final GpuTextureView colorTextureView = this.renderTarget.getColorTextureView();
            final GpuTextureView depthTextureView = this.renderTarget.useDepth ? this.renderTarget.getDepthTextureView() : null;
            assert colorTextureView != null;
            try (final RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sky Feature End Frame", colorTextureView, OptionalInt.empty(), depthTextureView, OptionalDouble.empty())) {
                RenderSystem.bindDefaultUniforms(pass);
                for (final Submit submit : this.submits) {
                    if (submit.geometry.isClosed()) {
                        throw new RuntimeException("Cannot render closed geometry!");
                    }

                    pass.setPipeline(submit.pipeline);
                    if (submit.geometry instanceof StaticGeometry staticGeometry) {
                        pass.setVertexBuffer(0, staticGeometry.vertexBuffer());
                        pass.setIndexBuffer(staticGeometry.indexBuffer(), staticGeometry.indexType());
                    }

                    pass.setUniform("DynamicTransforms", submit.dynamicTransforms);
                    pass.bindSampler("Sampler0", submit.textureView);
                    if (submit.geometry instanceof StaticGeometry staticGeometry) {
                        pass.drawIndexed(0, 0, staticGeometry.indexCount(), 1);
                    }
                }
            }

            super.endFrame();
        }
    }

    protected record Submit(RenderPipeline pipeline, Geometry geometry,
                            RenderUniforms uniforms, GpuTextureView textureView,
                            GpuBufferSlice dynamicTransforms) implements FeatureRenderer.Submit {
    }
}
