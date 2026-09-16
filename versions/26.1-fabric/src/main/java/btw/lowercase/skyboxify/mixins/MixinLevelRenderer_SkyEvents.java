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
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
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
    private static float skyboxify$tickDelta = 0.0F;

    @Unique
    private static ClientLevel skyboxify$level;

    @Unique
    private static SkyFeatureRenderer skyboxify$skyFeatureRenderer = null;

    @Inject(method = "addSkyPass", at = @At("HEAD"))
    private void skyboxify$getLocals(final FrameGraphBuilder frame, final CameraRenderState cameraState, final GpuBufferSlice skyFog, final CallbackInfo ci) {
        if (skyboxify$skyFeatureRenderer == null) {
            skyboxify$skyFeatureRenderer = new SkyFeatureRenderer(this.minecraft.getMainRenderTarget());
        }

        skyboxify$tickDelta = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        skyboxify$level = this.level;
    }

    @Inject(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V", shift = At.Shift.AFTER))
    private static void skyboxify$renderEndSkybox(final CallbackInfo ci) {
        Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.EndSky.After(skyboxify$skyFeatureRenderer, skyboxify$level));
    }

    @WrapOperation(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunriseAndSunset(Lcom/mojang/blaze3d/vertex/PoseStack;FI)V"))
    private static void skyboxify$endBatchSunrise(final SkyRenderer instance, final PoseStack poseStack, final float sunAngle, final int sunriseAndSunsetColor, final Operation<Void> original) {
        if (!Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.SunriseSunset()).isCancelled()) {
            original.call(instance, poseStack, sunAngle, sunriseAndSunsetColor);
            Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.SunriseSunset.After());
        }
    }

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSkyDisc(I)V"))
    private static boolean skyboxify$skyDiscEvent$top(final SkyRenderer instance, final int color) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.Disc(SkyRenderEvent.Disc.Type.TOP)).isCancelled();
    }

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V"))
    private static boolean skyboxify$renderSkyboxes(final SkyRenderer instance, final PoseStack poseStack, final float sunAngle, final float moonAngle, final float starAngle, final MoonPhase moonPhase, final float rainBrightness, final float starBrightness) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.SunMoonStars(skyboxify$skyFeatureRenderer, skyboxify$level, skyboxify$tickDelta)).isCancelled();
    }

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderDarkDisc()V"))
    private static boolean skyboxify$skyDiscEvent$bottom(final SkyRenderer instance) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.Disc(SkyRenderEvent.Disc.Type.BOTTOM)).isCancelled();
    }

    @WrapOperation(method = "addSkyPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/SkyRenderState;skybox:Lnet/minecraft/world/level/dimension/DimensionType$Skybox;", opcode = Opcodes.GETFIELD))
    private static DimensionType.Skybox skyboxify$allowNetherSky(final SkyRenderState instance, final Operation<DimensionType.Skybox> original) {
        //noinspection DataFlowIssue
        if (SkyboxifyImpl.skyboxManager().isEnabled() && SkyboxifyImpl.skyboxManager().containsEnabled(Level.NETHER) && skyboxify$level.dimension().equals(Level.NETHER)) {
            return DimensionType.Skybox.OVERWORLD;
        } else {
            return original.call(instance);
        }
    }
}
