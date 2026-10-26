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
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer_SkyEvents {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Nullable
    private ClientLevel level;

    @Unique
    private SkyExtractor skyboxify$extractor = null;

    @Inject(method = "addSkyPass", at = @At("HEAD"))
    private void skyboxify$extractState(final FrameGraphBuilder frameGraphBuilder, final Camera camera, final float tickDelta, final FogParameters fog, final CallbackInfo ci) {
        if (this.skyboxify$extractor == null) {
            this.skyboxify$extractor = new SkyExtractor(this.minecraft.getMainRenderTarget());
        }

        SkyEvents.EXTRACTION.invoker().accept(this.skyboxify$extractor, this.level, tickDelta);
    }

    @Inject(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER))
    private void skyboxify$renderEndSkybox(final CallbackInfo ci) {
        SkyEvents.END_SKY_AFTER.invoker().accept(this.skyboxify$extractor);
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSkyDisc(FFF)V"))
    private boolean skyboxify$skyDiscEvent$top(final SkyRenderer instance, final float red, final float green, final float blue) {
        return SkyEvents.SKY_DISC.invoker().accept(SkyEvents.DiscType.TOP);
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/Tesselator;FIFFLnet/minecraft/client/renderer/FogParameters;)V"))
    private boolean skyboxify$renderSkyboxes(final SkyRenderer instance, final PoseStack poseStack, final Tesselator tesselator, final float sunAngle, final int moonPhase, final float rainBrightness, final float starBrightness, final FogParameters fogParameters) {
        return SkyEvents.SUN_MOON_STARS.invoker().accept(this.skyboxify$extractor, this.level.dimension().equals(Level.NETHER));
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderDarkDisc(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private boolean skyboxify$skyDiscEvent$bottom(final SkyRenderer instance, final PoseStack poseStack) {
        return SkyEvents.SKY_DISC.invoker().accept(SkyEvents.DiscType.BOTTOM);
    }

    @ModifyExpressionValue(method = "addSkyPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;skyType()Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;"))
    private DimensionSpecialEffects.SkyType skyboxify$skyTypeOverride(final DimensionSpecialEffects.SkyType original) {
        if (SkyboxifyImpl.skyboxManager().isEnabled() && original != DimensionSpecialEffects.SkyType.END) {
            return DimensionSpecialEffects.SkyType.OVERWORLD;
        } else {
            return original;
        }
    }
}
