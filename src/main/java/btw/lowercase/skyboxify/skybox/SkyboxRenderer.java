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

package btw.lowercase.skyboxify.skybox;

import btw.lowercase.skyboxify.skybox.renderer.Geometry;
import btw.lowercase.skyboxify.skybox.renderer.RenderUniforms;
import btw.lowercase.skyboxify.skybox.renderer.SkyFeatureRenderer;
import btw.lowercase.skyboxify.skybox.renderer.StaticGeometry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.joml.*;

//? >=1.21.6 {
import btw.lowercase.skyboxify.utils.*;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.renderer.texture.AbstractTexture;
//?} else {
/*import btw.lowercase.skyboxify.Skyboxify;
*///? }

@Deprecated
public final class SkyboxRenderer {
    //? =1.21.4 {
	/*private static final net.minecraft.client.renderer.ShaderProgram CUSTOM_SKYBOX_SHADER;

	static {
		CUSTOM_SKYBOX_SHADER = new net.minecraft.client.renderer.ShaderProgram(
				Skyboxify.locationOrNull("core/custom_skybox"),
				DefaultVertexFormat.POSITION_TEX,
				net.minecraft.client.renderer.ShaderDefines.EMPTY
		);

		net.minecraft.client.renderer.CoreShaders.getProgramsToPreload().add(CUSTOM_SKYBOX_SHADER);
	}
	*///? }

    @Deprecated
    public static void renderLayer(final SkyFeatureRenderer.Pipeline pipeline, final Geometry inGeometry, final RenderUniforms uniforms, final Identifier texture) {
        final StaticGeometry geometry = (StaticGeometry) inGeometry;
        if (geometry.isClosed()) {
            throw new RuntimeException("Cannot render layer as geometry is closed!");
        }

        //? >=1.21.6 {
        final GpuBufferSlice transforms =
                //? >=26.2 {
                RenderSystem.getDynamicUniforms().writeTransform(uniforms.modelViewMatrix(),  (Vector4f) uniforms.shaderColor());
                //? } else >=1.21.11 {
                /*RenderSystem.getDynamicUniforms().writeTransform(uniforms.modelViewMatrix(), uniforms.shaderColor(), new Vector3f(), new Matrix4f());
                *///? } else {
                /*RenderSystem.getDynamicUniforms().writeTransform(uniforms.modelViewMatrix(), uniforms.shaderColor(), new Vector3f(), new Matrix4f(), 1.0F);
                *///? }

        final Minecraft minecraft = Minecraft.getInstance();
        final RenderTarget renderTarget =
                //? >=26.2 {
                minecraft.gameRenderer.mainRenderTarget();
                //? } else {
                /*minecraft.getMainRenderTarget();
                 *///? }

        final AbstractTexture skyTexture = minecraft.getTextureManager().getTexture(texture);
        final GpuTextureView colorTexture = renderTarget.getColorTextureView();
        final GpuTextureView depthTexture = renderTarget.useDepth ? renderTarget.getDepthTextureView() : null;
        assert colorTexture != null;
        try (final RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(
                        () -> "Custom Sky Rendering",
                        colorTexture,
                        //? >=26.2 {
                        java.util.Optional.empty(),
                        //? } else {
                        /*java.util.OptionalInt.empty(),
                         *///? }
                        depthTexture,
                        java.util.OptionalDouble.empty()
                )) {
            renderPass.setPipeline(pipeline.pipeline());
            renderPass.setVertexBuffer(
                    0,
                    geometry.vertexBuffer()
                    //? >=26.2
                    .slice()
            );
            renderPass.setIndexBuffer(geometry.indexBuffer(), geometry.indexType());

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", transforms);

            //? >=1.21.11 {
            renderPass.bindTexture("Sampler0", skyTexture.getTextureView(), skyTexture.getSampler());
            //?} else {
            /*renderPass.bindSampler("Sampler0", skyTexture.getTextureView());
             *///?}

            //? >=26.2 {
            renderPass.drawIndexed(geometry.indexCount(), 1, 0, 0, 0);
            //? } else {
            /*renderPass.drawIndexed(0, 0, geometry.indexCount(), 1);
            *///? }
        }
        //?} else {
        /*RenderSystem.setShader(CUSTOM_SKYBOX_SHADER);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.depthMask(false);
        RenderSystem.colorMask(true, true, true, false);

        final var blendFunction = pipeline.blendFunction();
        final var color = uniforms.shaderColor();
        RenderSystem.setShaderColor(color.x(), color.y(), color.z(), color.w());
        if (blendFunction != null) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(blendFunction.srcFactor().vanilla(), blendFunction.dstFactor().vanilla());
        } else {
            RenderSystem.disableBlend();
        }

        final var skyBuffer = geometry.vertexBuffer();
        skyBuffer.bind();
        skyBuffer.drawWithShader(uniforms.modelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        com.mojang.blaze3d.vertex.VertexBuffer.unbind();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.colorMask(true, true, true, false);
        RenderSystem.depthMask(true);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        *///?}
    }
}