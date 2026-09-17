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
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.world.level.Level;
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

    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @Unique
    private static final RenderStateDataKey<Boolean> skyboxify$IS_IN_NETHER = RenderStateDataKey.create(() -> "Is In Nether");

    @Unique
    private SkyFeatureRenderer skyboxify$skyFeatureRenderer = null;

    @Inject(method = "addSkyPass", at = @At("HEAD"))
    private void skyboxify$getLocals(final FrameGraphBuilder frameGraphBuilder, final Camera camera, final GpuBufferSlice shaderFog, final CallbackInfo ci) {
        if (this.skyboxify$skyFeatureRenderer == null) {
            this.skyboxify$skyFeatureRenderer = new SkyFeatureRenderer(this.minecraft.getMainRenderTarget());
        }

        this.levelRenderState.skyRenderState.setData(skyboxify$IS_IN_NETHER, this.level.dimension().equals(Level.NETHER));
        Skyboxify.getGlobalEventManager().dispatch(new SkyEvents.Extraction(this.skyboxify$skyFeatureRenderer, this.level, this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false)));
    }

    @Inject(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V", shift = At.Shift.AFTER))
    private void skyboxify$renderEndSkybox(final CallbackInfo ci) {
        Skyboxify.getGlobalEventManager().dispatch(new SkyEvents.EndSky.After(this.skyboxify$skyFeatureRenderer));
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSkyDisc(FFF)V"))
    private boolean skyboxify$skyDiscEvent$top(final SkyRenderer instance, final float red, final float green, final float blue) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyEvents.Disc(SkyEvents.Disc.Type.TOP)).isCancelled();
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FIFF)V"))
    private boolean skyboxify$renderSkyboxes(final SkyRenderer instance, final PoseStack poseStack, final float sunAngle, final int moonPhase, final float rainBrightness, final float starBrightness, @Local(argsOnly = true, ordinal = 0) final SkyRenderState skyRenderState) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyEvents.SunMoonStars(this.skyboxify$skyFeatureRenderer, skyRenderState.getDataOrDefault(skyboxify$IS_IN_NETHER, false))).isCancelled();
    }

    @WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderDarkDisc()V"))
    private boolean skyboxify$skyDiscEvent$bottom(final SkyRenderer instance) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyEvents.Disc(SkyEvents.Disc.Type.BOTTOM)).isCancelled();
    }

    @WrapOperation(method = "addSkyPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/SkyRenderState;skyType:Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;", opcode = Opcodes.GETFIELD))
    private DimensionSpecialEffects.SkyType skyboxify$allowNetherSky(final SkyRenderState instance, final Operation<DimensionSpecialEffects.SkyType> original) {
        //noinspection DataFlowIssue
        if (SkyboxifyImpl.skyboxManager().isEnabled() && SkyboxifyImpl.skyboxManager().containsEnabled(Level.NETHER) && this.level.dimension().equals(Level.NETHER)) {
            return DimensionSpecialEffects.SkyType.OVERWORLD;
        } else {
            return original.call(instance);
        }
    }
}
