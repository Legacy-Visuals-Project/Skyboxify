/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 Contributors to the project retain their copyright
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
import btw.lowercase.skyboxify.events.SkyRenderEvent;
import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SkyRenderer.class)
public abstract class MixinSkyRenderer {
    @WrapWithCondition(
            method = "renderSunMoonAndStars",
            at = @At(
                    value = "INVOKE",
                    //? >=1.21.9 {
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSun(FLcom/mojang/blaze3d/vertex/PoseStack;)V"
                    //?} else >=1.21.4 {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSun(FLnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;)V"
                    *///?} else {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSun(FLcom/mojang/blaze3d/vertex/Tesselator;Lcom/mojang/blaze3d/vertex/PoseStack;)V"
                    *///?}
            )
    )
    private boolean skyboxify$toggleSun(
            SkyRenderer instance,
            float rainLevel,
            //? <1.21.9 {
            /*//? >=1.21.4 {
            net.minecraft.client.renderer.MultiBufferSource multiBufferSource,
            //?} else {
            /^com.mojang.blaze3d.vertex.Tesselator tesselator,
             ^///?}
            *///?}
            PoseStack poseStack
    ) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.Celestial(SkyRenderEvent.Celestial.Type.SUN)).isCancelled();
    }

    @WrapWithCondition(
            method = "renderSunMoonAndStars",
            at = @At(
                    value = "INVOKE",
                    //? >=1.21.11 {
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderMoon(Lnet/minecraft/world/level/MoonPhase;FLcom/mojang/blaze3d/vertex/PoseStack;)V"
                    //?} else >=1.21.9 {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderMoon(IFLcom/mojang/blaze3d/vertex/PoseStack;)V"
                    *///?} else >=1.21.4 {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderMoon(IFLnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;)V"
                    *///?} else {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderMoon(IFLcom/mojang/blaze3d/vertex/Tesselator;Lcom/mojang/blaze3d/vertex/PoseStack;)V"
                    *///?}
            )
    )
    private boolean skyboxify$toggleMoon(
            SkyRenderer instance,
            //? >=1.21.11 {
            net.minecraft.world.level.MoonPhase moonPhases,
             //?} else {
            /*int moonPhases,
            *///?}
            float rainLevel,
            //? <1.21.9 {
            /*//? >=1.21.4 {
            net.minecraft.client.renderer.MultiBufferSource multiBufferSource,
            //?} else {
            /^com.mojang.blaze3d.vertex.Tesselator tesselator,
             ^///?}
            *///?}
            PoseStack poseStack
    ) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.Celestial(SkyRenderEvent.Celestial.Type.MOON)).isCancelled();
    }

    @WrapWithCondition(
            method = "renderSunMoonAndStars",
            at = @At(
                    value = "INVOKE",
                    //? >=1.21.6 {
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderStars(FLcom/mojang/blaze3d/vertex/PoseStack;)V"
                    //?} else {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderStars(Lnet/minecraft/client/renderer/FogParameters;FLcom/mojang/blaze3d/vertex/PoseStack;)V"
                    *///?}
            )
    )
    private boolean skyboxify$toggleStars(
            SkyRenderer instance,
            //? <1.21.6
            /*net.minecraft.client.renderer.FogParameters fog,*/
            float starBrightness,
            PoseStack poseStack
    ) {
        return !Skyboxify.getGlobalEventManager().dispatch(new SkyRenderEvent.Celestial(SkyRenderEvent.Celestial.Type.STARS)).isCancelled();
    }
}
