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

import btw.lowercase.skyboxify.Skyboxify;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.Identifier;

//? >=1.21.6 {
//? <=26.1
//import btw.lowercase.skyboxify.mixins.RenderPipelinesAccessor;
import btw.lowercase.skyboxify.utils.*;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Map;
import java.util.HashMap;
//?}

public final class SkyStorage {
	public static final Identifier CUSTOM_SKYBOX_LOCATION = Skyboxify.locationOrNull("core/custom_skybox");

	//? <=1.21.4 {
	/*public static final net.minecraft.client.renderer.ShaderProgram CUSTOM_SKYBOX_SHADER;

	static {
		CUSTOM_SKYBOX_SHADER = new net.minecraft.client.renderer.ShaderProgram(
				CUSTOM_SKYBOX_LOCATION,
				DefaultVertexFormat.POSITION_TEX,
				net.minecraft.client.renderer.ShaderDefines.EMPTY
		);

		net.minecraft.client.renderer.CoreShaders.getProgramsToPreload().add(CUSTOM_SKYBOX_SHADER);
	}
	*///? }

    //? >=1.21.6 {
    private static final Map<BlendFunction, RenderPipeline> renderPipelineCache = new HashMap<>();

    public static RenderPipeline createSkyboxPipeline(final @org.jetbrains.annotations.Nullable BlendFunction blendFunction) {
        if (renderPipelineCache.containsKey(blendFunction)) {
            return renderPipelineCache.get(blendFunction);
        } else {
            final RenderPipeline.Builder builder = RenderPipeline.builder(
                    //? <=26.1
                    //RenderPipelinesAccessor.skyboxify$getMatricesProjectionSnippet()
            );
            builder.withLocation(Skyboxify.locationOrNull("pipeline/custom_skybox"));
            builder.withVertexShader(CUSTOM_SKYBOX_LOCATION);
            builder.withFragmentShader(CUSTOM_SKYBOX_LOCATION);

            //? >=26.1 {
            final int writeColor = com.mojang.blaze3d.pipeline.ColorTargetState.WRITE_COLOR;
            final com.mojang.blaze3d.pipeline.BlendFunction vanillaBlendFunction = blendFunction == null ? null : blendFunction.vanilla();
            builder.withColorTargetState(new com.mojang.blaze3d.pipeline.ColorTargetState(
                    java.util.Optional.ofNullable(vanillaBlendFunction),
                    //? >=26.2 {
                    com.mojang.blaze3d.GpuFormat.RGBA8_UNORM, writeColor
                    //? } else {
                    /*writeColor
                    *///? }
            ));
            //? } else {
            /*builder.withDepthWrite(false);
            builder.withColorWrite(true, false);
            if (blendFunction != null) {
                builder.withBlend(blendFunction.vanilla());
            }
            *///? }

            //? >=26.2 {
            builder.withBindGroupLayout(net.minecraft.client.renderer.BindGroupLayouts.MATRICES_PROJECTION);
            builder.withBindGroupLayout(net.minecraft.client.renderer.BindGroupLayouts.SAMPLER0);
            //? } else {
            /*builder.withSampler("Sampler0");
            *///? }

            //? >=26.2 {
            builder.withVertexBinding(0, DefaultVertexFormat.POSITION_TEX);
            builder.withPrimitiveTopology(com.mojang.blaze3d.PrimitiveTopology.QUADS);
            //? } else {
            /*builder.withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS);
            *///? }

            final RenderPipeline pipeline = builder.build();
            renderPipelineCache.put(blendFunction, pipeline);
            IrisUtil.assignPipeline(pipeline, IrisPipeline.SKY_TEXTURED);
            return pipeline;
        }
    }
    //?}
}