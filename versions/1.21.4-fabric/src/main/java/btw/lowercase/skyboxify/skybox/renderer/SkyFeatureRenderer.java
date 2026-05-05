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

import btw.lowercase.skyboxify.skybox.SkyPart;
import btw.lowercase.skyboxify.skybox.SkyboxResourceHelper;
import btw.lowercase.skyboxify.utils.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import org.joml.Vector4fc;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class SkyFeatureRenderer extends FeatureRenderer<SkyFeatureRenderer.Submit> {
    private static final ShaderProgram CUSTOM_SKYBOX_SHADER_PROGRAM = new ShaderProgram(SkyboxResourceHelper.CUSTOM_SKYBOX_LOCATION, DefaultVertexFormat.POSITION_TEX, ShaderDefines.EMPTY);
    private static final RenderStateShard.ShaderStateShard SKYBOX_SHADER = new RenderStateShard.ShaderStateShard(CUSTOM_SKYBOX_SHADER_PROGRAM);

    private final BiFunction<ResourceLocation, BlendFunction, RenderType> RENDER_TYPE = Util.memoize((location, blendFunction) -> {
        final RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder();
        builder.setShaderState(SKYBOX_SHADER);
        builder.setTextureState(new RenderStateShard.TextureStateShard(location, TriState.FALSE, false));
        builder.setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false));
        builder.setOutputState(new RenderStateShard.OutputStateShard("dynamic_target", () -> Objects.requireNonNullElseGet(this.renderTarget, () -> Minecraft.getInstance().getMainRenderTarget()).bindWrite(false), () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false)));
        if (blendFunction != null) {
            builder.setTransparencyState(new RenderStateShard.TransparencyStateShard("Dynamic Blend Function", () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(blendFunction.srcFactor(), blendFunction.dstFactor());
            }, () -> {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }));
        } else {
            builder.setTransparencyState(RenderStateShard.NO_TRANSPARENCY);
        }

        return RenderType.create("skyboxify_skybox", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX.getVertexSize() * SkyPart.COUNT * 4, builder.createCompositeState(false));
    });

    public SkyFeatureRenderer(final RenderTarget renderTarget) {
        super(renderTarget);
    }

    @Override
    protected Submit createSubmit(final Key key, final RenderUniforms uniforms, final ResourceLocation location) {
        return new Submit(RENDER_TYPE.apply(location, key.pipeline().blendFunction()), uniforms);
    }

    @Override
    public void endFrame() {
        for (final Map.Entry<Key, List<Submit>> entry : this.submits.entrySet()) {
            final Key key = entry.getKey();
            final Geometry geometry = key.geometry();
            if (geometry.isClosed()) {
                throw new RuntimeException("Cannot render closed geometry!");
            }

            final VertexBuffer vertexBuffer = ((StaticGeometry) geometry).vertexBuffer();
            for (final Submit submit : entry.getValue()) {
                final Vector4fc shaderColor = submit.uniforms.shaderColor();
                RenderSystem.setShaderColor(shaderColor.x(), shaderColor.y(), shaderColor.z(), shaderColor.w());

                submit.renderType.setupRenderState();
                vertexBuffer.bind();
                vertexBuffer.drawWithShader(submit.uniforms.modelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
                VertexBuffer.unbind();
                submit.renderType.clearRenderState();
            }
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.clear();
    }

    protected record Submit(RenderType renderType, RenderUniforms uniforms) implements FeatureRenderer.Submit {
    }

    static {
        CoreShaders.getProgramsToPreload().add(CUSTOM_SKYBOX_SHADER_PROGRAM);
    }
}
