package btw.lowercase.optiboxes.mixins;

import btw.lowercase.optiboxes.skybox.OptiFineSkyRenderer;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import btw.lowercase.optiboxes.skybox.SkyboxManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    @Nullable
    private ClientLevel level;

    @Unique
    private float optiboxes$tickDelta;

    @Inject(method = "addSkyPass", at = @At("HEAD"))
    private void optiboxes$getLocals(
            FrameGraphBuilder frameGraphBuilder,
            Camera camera,
            float tickDelta,
            //? >=1.21.6 {
            com.mojang.blaze3d.buffers.GpuBufferSlice gpuBufferSlice,
            //?} else {
            /*FogParameters gpuBufferSlice,
            *///?}
            CallbackInfo ci) {
        this.optiboxes$tickDelta = tickDelta;
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
    private void optiboxes$renderEndSkybox(
            SkyRenderer instance,
            //? <=1.21.3
            /*PoseStack poseStack,*/
            Operation<Void> original
    ) {
        original.call(
                instance
                //? <=1.21.3
                /*, poseStack*/
        );
        if (SkyboxManager.INSTANCE.isEnabled(this.level)) {
            List<OptiFineSkybox> activeSkyboxes = SkyboxManager.INSTANCE.getActiveSkyboxes();
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.rotate(Axis.YP.rotationDegrees(-90.0F));
            for (OptiFineSkybox optiFineSkybox : activeSkyboxes) {
                //noinspection DataFlowIssue
                OptiFineSkyRenderer.INSTANCE.renderSkybox(optiFineSkybox, modelViewStack, this.level, 0.0F);
            }
            modelViewStack.popMatrix();
        }
    }

    @WrapOperation(
            method = "method_62215",
            at = @At(
                    value = "INVOKE",
                    //? >=1.21.4 {
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunriseAndSunset(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;FI)V"
                    //?} else {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunriseAndSunset(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/Tesselator;FI)V"
                    *///?}
            )
    )
    private void optiboxes$endBatchSunrise(
            SkyRenderer instance,
            PoseStack poseStack,
            //? >=1.21.4
            net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource,
            //? <=1.21.3
            /*com.mojang.blaze3d.vertex.Tesselator tesselator,*/
            float sunAngle,
            int sunriseOrSunsetColor,
            Operation<Void> original
    ) {
        original.call(
                instance,
                poseStack,
                //? >=1.21.4
                bufferSource,
                //? <=1.21.3
                /*tesselator,*/
                sunAngle,
                sunriseOrSunsetColor
        );
        if (SkyboxManager.INSTANCE.isEnabled(this.level)) {
            renderBuffers.bufferSource().endBatch();
        }
    }

    @WrapOperation(
            method = "method_62215",
            at = @At(
                    value = "INVOKE",
                    //? >=1.21.6 {
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;FIFF)V"
                    //?} else >=1.21.4 {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;FIFFLnet/minecraft/client/renderer/FogParameters;)V"
                    *///?} else {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/Tesselator;FIFFLnet/minecraft/client/renderer/FogParameters;)V"
                    *///?}
            )
    )
    private void optiboxes$renderSkyboxes(
            SkyRenderer instance,
            PoseStack poseStack,
            //? >=1.21.4
            MultiBufferSource.BufferSource bufferSource,
            //? <=1.21.3
            /*com.mojang.blaze3d.vertex.Tesselator tesselator,*/
            float timeOfDay,
            int moonPhases,
            float rainLevel,
            float starBrightness,
            //? <1.21.6
            /*FogParameters fog,*/
            Operation<Void> original
    ) {
        if (SkyboxManager.INSTANCE.isEnabled(this.level)) {
            List<OptiFineSkybox> activeSkyboxes = SkyboxManager.INSTANCE.getActiveSkyboxes();
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.rotate(Axis.YP.rotationDegrees(-90.0F));
            for (OptiFineSkybox optiFineSkybox : activeSkyboxes) {
                //noinspection DataFlowIssue
                OptiFineSkyRenderer.INSTANCE.renderSkybox(optiFineSkybox, modelViewStack, this.level, this.optiboxes$tickDelta);
            }
            modelViewStack.popMatrix();
        }

        original.call(
                instance,
                poseStack,
                //? >=1.21.4
                bufferSource,
                //? <=1.21.3
                /*tesselator,*/
                timeOfDay,
                moonPhases,
                rainLevel,
                starBrightness
                //? <1.21.6
                /*, fog*/
        );
    }

    //? >=1.21.4 {
    @com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"))
    private boolean optiboxes$moveEndBatch(net.minecraft.client.renderer.MultiBufferSource.BufferSource instance) {
        return !SkyboxManager.INSTANCE.isEnabled(this.level);
    }
    //?}

    @WrapOperation(method = "addSkyPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;NONE:Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;"))
    private DimensionSpecialEffects.SkyType optiboxes$allowNetherSky(Operation<DimensionSpecialEffects.SkyType> original) {
        //noinspection DataFlowIssue
        if (SkyboxManager.INSTANCE.isEnabled(this.level) && SkyboxManager.INSTANCE.containsEnabled(Level.NETHER) && this.level.dimension().equals(Level.NETHER)) {
            return DimensionSpecialEffects.SkyType.OVERWORLD;
        } else {
            return original.call();
        }
    }
}
