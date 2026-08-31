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

import btw.lowercase.skyboxify.Skyboxify;
import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.events.SkyRenderEvent;
import btw.lowercase.skyboxify.skybox.renderer.SkyFeatureRenderer;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer_SkyEvents {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Unique
    private static SkyFeatureRenderer skyboxify$skyFeatureRenderer = null;

    @Unique
    private static float skyboxify$tickDelta = 0.0F;

    @Unique
    private static ClientLevel skyboxify$level = null;

    @Inject(method = "addSkyPass", at = @At("HEAD"))
    private void skyboxify$getLocals(final FrameGraphBuilder frame, final Camera camera, final float tickDelta, final FogParameters skyFog, final CallbackInfo ci) {
        if (skyboxify$skyFeatureRenderer == null) {
            skyboxify$skyFeatureRenderer = new SkyFeatureRenderer(Minecraft.getInstance().getMainRenderTarget());
        }

        skyboxify$tickDelta = tickDelta;
        skyboxify$level = this.level;
    }

    @Inject(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V", shift = At.Shift.AFTER))
    private void skyboxify$renderEndSkybox(final CallbackInfo ci) {
        Skyboxify.eventManager().dispatch(new SkyRenderEvent.EndSky.After(skyboxify$skyFeatureRenderer, skyboxify$level));
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSkyDisc(FFF)V"))
    private boolean skyboxify$skyDiscEvent$top(final SkyRenderer instance, final float red, final float green, final float blue) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.topDisc()).isCancelled();
    }

    @WrapOperation(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunriseAndSunset(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;FI)V"))
    private void skyboxify$endBatchSunrise(final SkyRenderer instance, final PoseStack poseStack, final MultiBufferSource.BufferSource bufferSource, final float sunAngle, final int sunriseAndSunsetColor, final Operation<Void> original) {
        if (!Skyboxify.eventManager().dispatch(new SkyRenderEvent.SunriseSunset()).isCancelled()) {
            original.call(instance, poseStack, bufferSource, sunAngle, sunriseAndSunsetColor);
            Skyboxify.eventManager().dispatch(new SkyRenderEvent.SunriseSunset.After(bufferSource));
        }
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;FIFFLnet/minecraft/client/renderer/FogParameters;)V"))
    private boolean skyboxify$renderSkyboxes(final SkyRenderer instance, final PoseStack poseStack, final MultiBufferSource.BufferSource bufferSource, final float sunAngle, final int moonPhase, final float moonAngle, final float starAngle, final FogParameters fog) {
        return !Skyboxify.eventManager().dispatch(new SkyRenderEvent.SunMoonStars(skyboxify$skyFeatureRenderer, skyboxify$level, skyboxify$tickDelta)).isCancelled();
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderDarkDisc(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private boolean skyboxify$skyDiscEvent$bottom(final SkyRenderer instance, final PoseStack poseStack) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.bottomDisc()).isCancelled();
    }

    @WrapOperation(method = "addSkyPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;skyType()Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;", opcode = Opcodes.GETFIELD))
    private DimensionSpecialEffects.SkyType skyboxify$allowNetherSky(final DimensionSpecialEffects instance, final Operation<DimensionSpecialEffects.SkyType> original) {
        //noinspection DataFlowIssue
        if (SkyboxifyImpl.skyboxManager().isEnabled() && SkyboxifyImpl.skyboxManager().containsEnabled(Level.NETHER) && skyboxify$level.dimension().equals(Level.NETHER)) {
            return DimensionSpecialEffects.SkyType.OVERWORLD;
        } else {
            return original.call(instance);
        }
    }
}
