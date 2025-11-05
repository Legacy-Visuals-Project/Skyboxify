package btw.lowercase.optiboxes.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? >=1.21.2 {
import btw.lowercase.optiboxes.OptiBoxesClient;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.injection.At;
//?}

//? >=1.21.2 {
@Mixin(SkyRenderer.class)
//?} else {
/*@Mixin(net.minecraft.client.Minecraft.class)
*///?}
public abstract class MixinSkyRenderer {
    //? >=1.21.2 {
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
    private boolean uniskies$toggleSun(
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
        return !OptiBoxesClient.getConfig().enabled.isEnabled() || OptiBoxesClient.getConfig().renderSunMoon.isEnabled();
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
    private boolean uniskies$toggleMoon(
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
        return !OptiBoxesClient.getConfig().enabled.isEnabled() || OptiBoxesClient.getConfig().renderSunMoon.isEnabled();
    }

    //? >=1.21.4 <1.21.9 {
    /*@WrapWithCondition(
            method = "renderSunMoonAndStars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"
            )
    )
    private boolean uniskies$disableBatch(net.minecraft.client.renderer.MultiBufferSource.BufferSource instance) {
        return !OptiBoxesClient.getConfig().enabled.isEnabled() || OptiBoxesClient.getConfig().renderSunMoon.isEnabled();
    }
    *///?}

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
    private boolean uniskies$toggleStars(
            SkyRenderer instance,
            //? <1.21.6
            /*net.minecraft.client.renderer.FogParameters fog,*/
            float starBrightness,
            PoseStack poseStack
    ) {
        return !OptiBoxesClient.getConfig().enabled.isEnabled() || OptiBoxesClient.getConfig().renderStars.isEnabled();
    }
    //?}
}
