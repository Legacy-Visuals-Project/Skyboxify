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
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.commands.RenderPass;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3fc;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class MixinLevelRenderer_SkyEvents {
    @Shadow
    @Final
    private RenderTarget renderTarget;

    @Unique
    private static float skyboxify$tickDelta = 0.0F;

    @Unique
    private static ClientLevel skyboxify$level;

    @Unique
    private static SkyFeatureRenderer skyboxify$skyFeatureRenderer = null;

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void skyboxify$getLocals(final ClientLevel level, final float tickDelta, final Camera camera, final SkyRenderState state, final CallbackInfo ci) {
        if (skyboxify$skyFeatureRenderer == null) {
            skyboxify$skyFeatureRenderer = new SkyFeatureRenderer(this.renderTarget);
        }

        skyboxify$tickDelta = tickDelta;
        skyboxify$level = level;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky(Lcom/mojang/renderpearl/api/commands/RenderPass;)V", shift = At.Shift.AFTER))
    private static void skyboxify$renderEndSkybox(final CallbackInfo ci, @Local(name = "renderPass") final RenderPass pass) {
        Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.EndSky.After(skyboxify$skyFeatureRenderer, skyboxify$level, pass));
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSkyDisc(Lcom/mojang/renderpearl/api/commands/RenderPass;Lorg/joml/Vector3fc;)V"))
    private static boolean skyboxify$skyDiscEvent$top(final SkyRenderer instance, final RenderPass renderPass, final Vector3fc vector3fc) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.Disc(SkyRenderEvent.Disc.Type.TOP)).isCancelled();
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V"))
    private static boolean skyboxify$renderSkyboxes(final SkyRenderer instance, final RenderPass pass, final PoseStack poseStack, final float sunAngle, final float moonAngle, final float starAngle, final MoonPhase moonPhase, final float rainBrightness, final float starBrightness) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.SunMoonStars(skyboxify$skyFeatureRenderer, skyboxify$level, skyboxify$tickDelta, pass)).isCancelled();
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SkyRenderer;renderDarkDisc(Lcom/mojang/renderpearl/api/commands/RenderPass;)V"))
    private static boolean skyboxify$skyDiscEvent$bottom(final SkyRenderer instance, final RenderPass pass) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.Disc(SkyRenderEvent.Disc.Type.BOTTOM)).isCancelled();
    }

    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;skybox()Lnet/minecraft/world/level/dimension/DimensionType$Skybox;", opcode = Opcodes.GETFIELD))
    private static DimensionType.Skybox skyboxify$allowNetherSky(final DimensionType instance, final Operation<DimensionType.Skybox> original) {
        //noinspection DataFlowIssue
        if (SkyboxifyImpl.skyboxManager().isEnabled() && SkyboxifyImpl.skyboxManager().containsEnabled(Level.NETHER) && skyboxify$level.dimension().equals(Level.NETHER)) {
            return DimensionType.Skybox.OVERWORLD;
        } else {
            return original.call(instance);
        }
    }
}
