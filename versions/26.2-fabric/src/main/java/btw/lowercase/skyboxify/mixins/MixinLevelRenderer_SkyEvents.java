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
    private LevelRenderState levelRenderState;

    @Unique
    private static final RenderStateDataKey<Boolean> skyboxify$IS_IN_NETHER = RenderStateDataKey.create(() -> "Is In Nether");

    @Unique
    private static SkyExtractor skyboxify$extractor = null;

    @Inject(method = "addSkyPass", at = @At("HEAD"))
    private void skyboxify$extractState(final FrameGraphBuilder frame, final CameraRenderState cameraState, final GpuBufferSlice skyFog, final CallbackInfo ci) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (skyboxify$extractor == null) {
            skyboxify$extractor = new SkyExtractor(minecraft.gameRenderer.mainRenderTarget());
        }

        final ClientLevel level = minecraft.level;
        if (level != null) {
            this.levelRenderState.skyRenderState.setData(skyboxify$IS_IN_NETHER, level.dimension().equals(Level.NETHER));
            SkyEvents.EXTRACTION.invoker().accept(skyboxify$extractor, level, minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false));
        }
    }

    @Inject(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V", shift = At.Shift.AFTER))
    private static void skyboxify$renderEndSkybox(final CallbackInfo ci) {
        SkyEvents.END_SKY_AFTER.invoker().accept(skyboxify$extractor);
    }

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSkyDisc(I)V"))
    private static boolean skyboxify$skyDiscEvent$top(final SkyRenderer instance, final int color) {
        return SkyEvents.SKY_DISC.invoker().accept(SkyEvents.DiscType.TOP);
    }

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V"))
    private static boolean skyboxify$renderSkyboxes(final SkyRenderer instance, final PoseStack poseStack, final float sunAngle, final float moonAngle, final float starAngle, final MoonPhase moonPhase, final float rainBrightness, final float starBrightness, @Local(argsOnly = true, name = "state") final SkyRenderState state) {
        return SkyEvents.SUN_MOON_STARS.invoker().accept(skyboxify$extractor, state.getDataOrDefault(skyboxify$IS_IN_NETHER, false));
    }

    @WrapWithCondition(method = "lambda$addSkyPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderDarkDisc()V"))
    private static boolean skyboxify$skyDiscEvent$bottom(final SkyRenderer instance) {
        return SkyEvents.SKY_DISC.invoker().accept(SkyEvents.DiscType.BOTTOM);
    }

    @ModifyExpressionValue(method = "addSkyPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/SkyRenderState;skybox:Lnet/minecraft/world/level/dimension/DimensionType$Skybox;", opcode = Opcodes.GETFIELD))
    private DimensionType.Skybox skyboxify$skyTypeOverride(final DimensionType.Skybox original) {
        if (SkyboxifyImpl.skyboxManager().isEnabled() && original != DimensionType.Skybox.END) {
            return DimensionType.Skybox.OVERWORLD;
        } else {
            return original;
        }
    }
}
