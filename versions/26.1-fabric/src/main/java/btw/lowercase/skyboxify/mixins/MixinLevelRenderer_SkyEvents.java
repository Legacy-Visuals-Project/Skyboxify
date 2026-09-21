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
import btw.lowercase.skyboxify.events.SkyEvents;
import btw.lowercase.skyboxify.skybox.renderer.SkyFeatureRenderer;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType;
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
    @Final
    private LevelRenderState levelRenderState;

    @Unique
    private static final RenderStateDataKey<Boolean> skyboxify$IS_IN_NETHER = RenderStateDataKey.create(() -> "Is In Nether");

    @Unique
    private static ClientLevel skyboxify$level;

    @Unique
    private static SkyFeatureRenderer skyboxify$skyFeatureRenderer = null;

    @Inject(method = "addSkyPass", at = @At("HEAD"))
    private void skyboxify$getLocals(final FrameGraphBuilder frame, final CameraRenderState cameraState, final GpuBufferSlice skyFog, final CallbackInfo ci) {
        if (skyboxify$skyFeatureRenderer == null) {
            skyboxify$skyFeatureRenderer = new SkyFeatureRenderer(this.minecraft.getMainRenderTarget());
        }

        skyboxify$level = this.minecraft.level;
        this.levelRenderState.skyRenderState.setData(skyboxify$IS_IN_NETHER, skyboxify$level.dimension().equals(Level.NETHER));
        Skyboxify.eventManager().dispatch(new SkyEvents.Extraction(skyboxify$skyFeatureRenderer, skyboxify$level, minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false)));
    }

    @Inject(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V", shift = At.Shift.AFTER))
    private static void skyboxify$renderEndSkybox(final CallbackInfo ci) {
        Skyboxify.eventManager().dispatch(new SkyEvents.EndSky.After(skyboxify$skyFeatureRenderer));
    }

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSkyDisc(I)V"))
    private static boolean skyboxify$skyDiscEvent$top(final SkyRenderer instance, final int color) {
        return !Skyboxify.eventManager().dispatch(new SkyEvents.Disc(SkyEvents.Disc.Type.TOP)).isCancelled();
    }

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V"))
    private static boolean skyboxify$renderSkyboxes(final SkyRenderer instance, final PoseStack poseStack, final float sunAngle, final float moonAngle, final float starAngle, final MoonPhase moonPhase, final float rainBrightness, final float starBrightness, @Local(argsOnly = true, name = "state") final SkyRenderState skyRenderState) {
        return !Skyboxify.eventManager().dispatch(new SkyEvents.SunMoonStars(skyboxify$skyFeatureRenderer, skyRenderState.getDataOrDefault(skyboxify$IS_IN_NETHER, false))).isCancelled();
    }

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderDarkDisc()V"))
    private static boolean skyboxify$skyDiscEvent$bottom(final SkyRenderer instance) {
        return !Skyboxify.eventManager().dispatch(new SkyEvents.Disc(SkyEvents.Disc.Type.BOTTOM)).isCancelled();
    }

    @WrapOperation(method = "addSkyPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/SkyRenderState;skybox:Lnet/minecraft/world/level/dimension/DimensionType$Skybox;", opcode = Opcodes.GETFIELD))
    private DimensionType.Skybox skyboxify$allowNetherSky(final SkyRenderState instance, final Operation<DimensionType.Skybox> original) {
        //noinspection DataFlowIssue
        if (SkyboxifyImpl.skyboxManager().isEnabled() && SkyboxifyImpl.skyboxManager().containsEnabled(Level.NETHER) && skyboxify$level.dimension().equals(Level.NETHER)) {
            return DimensionType.Skybox.OVERWORLD;
        } else {
            return original.call(instance);
        }
    }
}
