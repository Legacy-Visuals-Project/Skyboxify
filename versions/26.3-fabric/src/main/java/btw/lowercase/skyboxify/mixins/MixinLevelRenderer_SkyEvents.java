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

import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.skybox.SkyEvents;
import btw.lowercase.skyboxify.skybox.renderer.SkyExtractor;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.commands.RenderPass;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class MixinLevelRenderer_SkyEvents {
    @Unique
    private static final RenderStateDataKey<Boolean> skyboxify$IS_IN_NETHER = RenderStateDataKey.create(() -> "Is In Nether");

    @Unique
    private SkyExtractor skyboxify$extractor = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void skyboxify$initialize(final TextureManager textureManager, final AtlasManager atlasManager, final RenderTarget renderTarget, final CallbackInfo ci) {
        this.skyboxify$extractor = new SkyExtractor(renderTarget);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void skyboxify$getLocals(final ClientLevel level, final float tickDelta, final Camera camera, final SkyRenderState state, final CallbackInfo ci) {
        state.setData(skyboxify$IS_IN_NETHER, level.dimension().equals(Level.NETHER));
        SkyEvents.EXTRACTION.invoker().accept(this.skyboxify$extractor, level, tickDelta);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky(Lcom/mojang/renderpearl/api/commands/RenderPass;)V", shift = At.Shift.AFTER))
    private void skyboxify$renderEndSkybox(final CallbackInfo ci, @Local(name = "renderPass") final RenderPass pass) {
        SkyEvents.END_SKY_AFTER.invoker().accept(this.skyboxify$extractor, pass);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSkyDisc(Lcom/mojang/renderpearl/api/commands/RenderPass;Lorg/joml/Vector3fc;)V"))
    private boolean skyboxify$skyDiscEvent$top(final SkyRenderer instance, final RenderPass renderPass, final Vector3fc color) {
        return SkyEvents.SKY_DISC.invoker().accept(SkyEvents.DiscType.TOP);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V"))
    private boolean skyboxify$renderSkyboxes(final SkyRenderer instance, final RenderPass pass, final PoseStack poseStack, final float sunAngle, final float moonAngle, final float starAngle, final MoonPhase moonPhase, final float rainBrightness, final float starBrightness, @Local(argsOnly = true, name = "state") final SkyRenderState state) {
        return SkyEvents.SUN_MOON_STARS.invoker().accept(this.skyboxify$extractor, state.getDataOrDefault(skyboxify$IS_IN_NETHER, false), pass);
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderDarkDisc(Lcom/mojang/renderpearl/api/commands/RenderPass;)V"))
    private boolean skyboxify$skyDiscEvent$bottom(final SkyRenderer instance, final RenderPass pass) {
        return SkyEvents.SKY_DISC.invoker().accept(SkyEvents.DiscType.BOTTOM);
    }

    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;skybox()Lnet/minecraft/world/level/dimension/DimensionType$Skybox;"))
    private DimensionType.Skybox skyboxify$skyTypeOverride(final DimensionType.Skybox original) {
        if (SkyboxifyImpl.skyboxManager().isEnabled() && original != DimensionType.Skybox.END) {
            return DimensionType.Skybox.OVERWORLD;
        } else {
            return original;
        }
    }
}
