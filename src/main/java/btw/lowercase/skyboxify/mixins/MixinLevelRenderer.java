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
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? >=1.21.11 {
import net.minecraft.world.level.dimension.DimensionType;
 //?} else {
/*import net.minecraft.client.renderer.DimensionSpecialEffects;
*///?}

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer {
    @Unique
    private static float skyboxify$tickDelta;
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method = "addSkyPass", at = @At("HEAD"))
    private void skyboxify$getLocals(
            FrameGraphBuilder frameGraphBuilder,
            Camera camera,
            //? <=1.21.8
            /*float tickDelta,*/
            //? >=1.21.6 {
            com.mojang.blaze3d.buffers.GpuBufferSlice gpuBufferSlice,
            //?} else {
            /*net.minecraft.client.renderer.FogParameters gpuBufferSlice,
             *///?}
            CallbackInfo ci) {
        skyboxify$tickDelta =
                //? >=1.21.9 {
                net.minecraft.client.Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
                 //?} else {
                /*tickDelta;
        *///?}
    }

    @WrapOperation(
            method = "method_62215",
            at = @At(
                    value = "INVOKE",
                    //? >=1.21.4 {
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky()V"
                    //?} else {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderEndSky(Lcom/mojang/blaze3d/vertex/PoseStack;)V"
                    *///?}
            )
    )
    private
        //? >=1.21.11
        static
    void skyboxify$renderEndSkybox(
            net.minecraft.client.renderer.SkyRenderer instance,
            //? <=1.21.3
            /*PoseStack poseStack,*/
            Operation<Void> original
    ) {
        original.call(
                instance
                //? <=1.21.3
                /*, poseStack*/
        );
        Skyboxify.getEventManager().dispatch(new SkyRenderEvent.EndSky.After(
                //? >=1.21.11 {
                net.minecraft.client.Minecraft.getInstance().level
                //? } else {
                /*this.level
                 *///? }
        ));
    }

    @WrapOperation(
            method = "method_62215",
            at = @At(
                    value = "INVOKE",
                    //? >=1.21.9 {
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunriseAndSunset(Lcom/mojang/blaze3d/vertex/PoseStack;FI)V"
                    //?} else >=1.21.4 {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunriseAndSunset(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;FI)V"
                    *///?} else {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunriseAndSunset(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/Tesselator;FI)V"
                    *///?}
            )
    )
    private
    //? >=1.21.11
    static
    void skyboxify$endBatchSunrise(
            net.minecraft.client.renderer.SkyRenderer instance,
            PoseStack poseStack,
            //? >=1.21.4 <1.21.9
            /*net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource,*/
            //? <=1.21.3
            /*com.mojang.blaze3d.vertex.Tesselator tesselator,*/
            float sunAngle,
            int sunriseOrSunsetColor,
            Operation<Void> original
    ) {
        final var event = Skyboxify.getEventManager().dispatch(new SkyRenderEvent.SunriseSunset(
                //? >=1.21.11 {
                net.minecraft.client.Minecraft.getInstance().level
                //? } else {
                /*this.level
                 *///? }
                //? >=1.21.4 <1.21.9
                /*, bufferSource*/
        ));
        if (!event.isCancelled()) {
            original.call(
                    instance,
                    poseStack,
                    //? >=1.21.4 <1.21.9
                    /*bufferSource,*/
                    //? <=1.21.3
                    /*tesselator,*/
                    sunAngle,
                    sunriseOrSunsetColor
            );
        }
    }

    @WrapOperation(
            method = "method_62215",
            at = @At(
                    value = "INVOKE",
                    //? >=1.21.11 {
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V"
                    //?} else >=1.21.9 {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FIFF)V"
                    *///?} else >=1.21.6 {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;FIFF)V"
                    *///?} else >=1.21.4 {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;FIFFLnet/minecraft/client/renderer/FogParameters;)V"
                    *///?} else {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/Tesselator;FIFFLnet/minecraft/client/renderer/FogParameters;)V"
                    *///?}
            )
    )
    private
        //? >=1.21.11
        static
    void skyboxify$renderSkyboxes(
            net.minecraft.client.renderer.SkyRenderer instance,
            PoseStack poseStack,
            //? >=1.21.4 <1.21.9
            /*net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource,*/
            //? <=1.21.3
            /*com.mojang.blaze3d.vertex.Tesselator tesselator,*/
            float timeOfDay,
            //? <=1.21.10 {
            /*int moonPhase,
            *///?}
            float moonAngle,
            float starAngle,
            //? >=1.21.11 {
            net.minecraft.world.level.MoonPhase moonPhase,
            float rainBrightness,
            float starBrightness,
            //?}
            //? <1.21.6
            /*net.minecraft.client.renderer.FogParameters fog,*/
            Operation<Void> original
    ) {
        final var event = Skyboxify.getEventManager().dispatch(new SkyRenderEvent.SunMoonStars(
                //? >=1.21.11 {
                net.minecraft.client.Minecraft.getInstance().level,
                //? } else {
                /*this.level,
                 *///? }
                skyboxify$tickDelta
        ));
        if (!event.isCancelled()) {
            original.call(
                    instance,
                    poseStack,
                    //? >=1.21.4 <1.21.9
                    /*bufferSource,*/
                    //? <=1.21.3
                    /*tesselator,*/
                    timeOfDay,
                    //? <=1.21.10
                    /*moonPhase,*/
                    moonAngle,
                    starAngle
                    //? >=1.21.11 {
                    , moonPhase, rainBrightness, starBrightness
                     //?}
                    //? <1.21.6
                    /*, fog*/
            );
        }
    }

    @WrapOperation(
            method = "addSkyPass",
            at = @At(
                    //? >=1.21.11 {
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/state/SkyRenderState;skybox:Lnet/minecraft/world/level/dimension/DimensionType$Skybox;"
                    //?} else >=1.21.10 {
                    /*value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/state/SkyRenderState;skyType:Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;"
                    *///?} else {
                    /*value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;skyType()Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;"
                    *///?}
            )
    )
    private
        //? >=1.21.11 {
        DimensionType.Skybox
         //?} else {
    /*DimensionSpecialEffects.SkyType
    *///?}
    skyboxify$allowNetherSky(
            //? >= 1.21.9 {
            net.minecraft.client.renderer.state.SkyRenderState instance,
             //?} else {
            /*DimensionSpecialEffects instance,
            *///?}
            Operation<
                    //? >=1.21.11 {
                    DimensionType.Skybox
                     //?} else {
                    /*DimensionSpecialEffects.SkyType
                    *///?}
                    > original
    ) {
        //noinspection DataFlowIssue
        if (SkyboxManager.INSTANCE.isEnabled(this.level) && SkyboxManager.INSTANCE.containsEnabled(Level.NETHER) && this.level.dimension().equals(Level.NETHER)) {
            //? >=1.21.11 {
            return DimensionType.Skybox.OVERWORLD;
             //?} else {
            /*return DimensionSpecialEffects.SkyType.OVERWORLD;
            *///?}
        } else {
            return original.call(instance);
        }
    }
}
