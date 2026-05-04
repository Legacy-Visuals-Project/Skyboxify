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

import btw.lowercase.skyboxify.utils.CommonUtils;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class SkyFeatureRenderer extends FeatureRenderer<SkyFeatureRenderer.Submit> {
    private final RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();

    @Override
    protected Submit createSubmit(final RenderUniforms uniforms, final ResourceLocation location) {
        return new Submit(uniforms, Minecraft.getInstance().getTextureManager().getTexture(location).getTexture());
    }

    @Override
    public void endFrame() {
        final GpuTexture colorTexture = this.renderTarget.getColorTexture();
        final GpuTexture depthTexture = this.renderTarget.useDepth ? this.renderTarget.getDepthTexture() : null;
        assert colorTexture != null;
        try (final RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(colorTexture, OptionalInt.empty(), depthTexture, OptionalDouble.empty())) {
            for (final Map.Entry<Key, List<Submit>> entry : this.submits.entrySet()) {
                final Key key = entry.getKey();
                final Geometry geometry = key.geometry();
                if (geometry.isClosed()) {
                    throw new RuntimeException("Cannot render closed geometry!");
                }

                pass.setPipeline(key.pipeline().pipeline());
                if (geometry instanceof ReusableGeometry reusableGeometry) {
                    pass.setVertexBuffer(0, reusableGeometry.vertexBuffer());
                    pass.setIndexBuffer(reusableGeometry.indexBuffer(), reusableGeometry.indexType());
                }

                for (final Submit submit : entry.getValue()) {
                    final Matrix4f modelViewBackup = new Matrix4f(RenderSystem.getModelViewStack());

                    final Vector4f shaderColor = CommonUtils.unpackARGB(submit.uniforms.shaderColor());
                    RenderSystem.setShaderColor(shaderColor.x, shaderColor.y, shaderColor.z, shaderColor.w);
                    RenderSystem.getModelViewStack().set(submit.uniforms.modelViewMatrix());
                    pass.bindSampler("Sampler0", submit.texture);
                    if (geometry instanceof ReusableGeometry reusableGeometry) {
                        pass.drawIndexed(0, reusableGeometry.indexCount());
                    }

                    RenderSystem.getModelViewStack().set(modelViewBackup);
                }
            }
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.clear();
    }

    protected record Submit(RenderUniforms uniforms, GpuTexture texture) implements FeatureRenderer.Submit {
    }
}
