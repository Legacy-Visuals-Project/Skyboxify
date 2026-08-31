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
import btw.lowercase.skyboxify.utils.FilteringMode;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassDescriptor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

public class SkyFeatureRenderer extends FeatureRenderer<SkyFeatureRenderer.Submit> {
    public SkyFeatureRenderer(final RenderTarget renderTarget) {
        super(renderTarget);
    }

    @Override
    protected Submit createSubmit(final Pipeline pipeline, final Geometry geometry, final RenderUniforms uniforms, final Identifier location) {
        final GpuTextureView textureView = Minecraft.getInstance().getTextureManager().getTexture(location).getTextureView();
        final GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(uniforms.modelViewMatrix(), (Vector4f) uniforms.shaderColor());
        return new Submit(pipeline.pipeline(), geometry, uniforms, textureView, dynamicTransforms);
    }

    @Override
    public void endFrame() {
        if (!this.submits.isEmpty()) {
            final RenderSystem.AutoStorageIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);

            final FilteringMode filteringMode = SkyboxifyImpl.config().filteringMode;
            final GpuSampler sampler = RenderSystem.getSamplerCache().getClampToEdge(filteringMode == FilteringMode.LINEAR ? FilterMode.LINEAR : FilterMode.NEAREST);

            try (final RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(this.createPassDescriptor())) {
                RenderSystem.bindDefaultUniforms(pass);
                for (final Submit submit : this.submits) {
                    if (submit.geometry.isClosed()) {
                        throw new RuntimeException("Cannot render closed geometry!");
                    }

                    pass.setPipeline(submit.pipeline);
                    if (submit.geometry instanceof StaticGeometry staticGeometry) {
                        pass.setVertexBuffer(0, staticGeometry.vertexBuffer().slice());
                        pass.setIndexBuffer(indexBuffer.getBuffer(staticGeometry.indexCount()), indexBuffer.type());
                    }


                    pass.setUniform("DynamicTransforms", submit.dynamicTransforms);
                    pass.bindTexture("Sampler0", submit.textureView, sampler);
                    if (submit.geometry instanceof StaticGeometry staticGeometry) {
                        pass.drawIndexed(staticGeometry.indexCount(), 1, 0, 0, 0);
                    }
                }
            }

            super.endFrame();
        }
    }

    private @NonNull RenderPassDescriptor createPassDescriptor() {
        final RenderPassDescriptor descriptor = RenderPassDescriptor.create((() -> "Sky Feature End Frame"));

        final GpuTextureView colorTextureView = this.renderTarget.getColorTextureView();
        if (colorTextureView != null) {
            descriptor.withColorAttachment(colorTextureView);
        }

        final GpuTextureView depthTextureView = this.renderTarget.getDepthTextureView();
        if (this.renderTarget.useDepth && depthTextureView != null) {
            descriptor.withDepthAttachment(depthTextureView);
        }

        descriptor.withRenderArea(new RenderPass.RenderArea(0, 0, this.renderTarget.width, this.renderTarget.height));
        return descriptor;
    }

    protected record Submit(RenderPipeline pipeline, Geometry geometry,
                            RenderUniforms uniforms, GpuTextureView textureView,
                            GpuBufferSlice dynamicTransforms) implements FeatureRenderer.Submit {
    }
}
