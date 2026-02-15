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

package btw.lowercase.skyboxify.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? >= 1.21.5 {
@Mixin(net.minecraft.client.renderer.RenderPipelines.class)
 //?} else {
/*@Mixin(net.minecraft.client.Minecraft.class)
*///?}
public interface RenderPipelinesAccessor {
    //? >= 1.21.5 {
    @org.spongepowered.asm.mixin.gen.Accessor(
            //? >=1.21.6 {
            "MATRICES_PROJECTION_SNIPPET"
            //?} else {
            /*"MATRICES_COLOR_FOG_SNIPPET"
            *///?}
    )
    static com.mojang.blaze3d.pipeline.RenderPipeline.Snippet skyboxify$getMatricesProjectionSnippet() {
        throw new UnsupportedOperationException();
    }
    //?}
}