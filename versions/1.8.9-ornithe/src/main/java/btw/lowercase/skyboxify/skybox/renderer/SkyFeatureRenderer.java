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

import btw.lowercase.skyboxify.Skyboxify;
import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.skybox.SkyPart;
import btw.lowercase.skyboxify.utils.BlendFunction;
import btw.lowercase.skyboxify.utils.FilteringMode;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import org.joml.Vector4fc;

import java.util.Objects;
import java.util.function.Function;

public class SkyFeatureRenderer extends FeatureRenderer<SkyFeatureRenderer.Submit> {
    private final Function<RenderData, RenderType> RENDER_TYPE = Util.memoize(renderData -> {
        final RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder();
        builder.setShaderState(RenderStateShard.POSITION_TEX_SHADER);
        builder.setWriteMaskState(RenderStateShard.COLOR_WRITE);
        builder.setTextureState(new RenderStateShard.TextureStateShard(renderData.location, renderData.blur ? TriState.TRUE : TriState.FALSE, false));
        builder.setOutputState(new RenderStateShard.OutputStateShard("Dynamic Output Target", () -> Objects.requireNonNullElseGet(this.renderTarget, () -> Minecraft.getInstance().getMainRenderTarget()).bindWrite(false), () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false)));

        final BlendFunction blendFunction = renderData.blend;
        if (blendFunction != null) {
            builder.setTransparencyState(new RenderStateShard.TransparencyStateShard("Dynamic Blend Function", () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(blendFunction.srcFactor().vanilla(), blendFunction.dstFactor().vanilla());
            }, () -> {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }));
        }

        return RenderType.create(
                Skyboxify.locationOrNull("skybox").toString(),
                DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX.getVertexSize() * SkyPart.COUNT * 4,
                builder.createCompositeState(false)
        );
    });

    public SkyFeatureRenderer(final RenderTarget renderTarget) {
        super(renderTarget);
    }

    @Override
    protected Submit createSubmit(final Pipeline pipeline, final Geometry geometry, final RenderUniforms uniforms, final ResourceLocation location) {
        return new Submit(RENDER_TYPE.apply(new RenderData(location, pipeline.blendFunction(), SkyboxifyImpl.config().filteringMode.getValue() == FilteringMode.LINEAR)), geometry, uniforms);
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
                RenderSystem.setShaderColor(shaderColor.x(), shaderColor.y(), shaderColor.z(), shaderColor.w());

                submit.renderType.setupRenderState();
                vertexBuffer.bind();
                vertexBuffer.drawWithShader(submit.uniforms.modelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
                VertexBuffer.unbind();
                submit.renderType.clearRenderState();
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            super.endFrame();
        }
    }

    private record RenderData(ResourceLocation location, BlendFunction blend, boolean blur) {
    }

    protected record Submit(RenderType renderType, Geometry geometry,
                            RenderUniforms uniforms) implements FeatureRenderer.Submit {
    }
}
