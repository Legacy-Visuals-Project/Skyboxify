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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;
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
    @Nullable
    private ClientLevel level;

    @Unique
    private static float optiboxes$tickDelta;

    @Inject(method = "addSkyPass", at = @At("HEAD"))
    private void optiboxes$getLocals(
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
        optiboxes$tickDelta =
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
    /*static*/
    void optiboxes$renderEndSkybox(
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
        Minecraft minecraft = Minecraft.getInstance();
        if (SkyboxManager.INSTANCE.isEnabled(minecraft.level)) {
            List<OptiFineSkybox> activeSkyboxes = SkyboxManager.INSTANCE.getActiveSkyboxes();
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.rotate(Axis.YP.rotationDegrees(-90.0F));
            for (OptiFineSkybox optiFineSkybox : activeSkyboxes) {
                //noinspection DataFlowIssue
                OptiFineSkyRenderer.INSTANCE.renderSkybox(optiFineSkybox, modelViewStack, minecraft.level, 0.0F);
            }
            modelViewStack.popMatrix();
        }
    }

    // TODO: Fix later
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
    /*static*/
    void optiboxes$endBatchSunrise(
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
        //? >=1.21.4 <1.21.9 {
        /*if (SkyboxManager.INSTANCE.isEnabled(this.level)) {
            bufferSource.endBatch();
        }
        *///?}
    }

    @WrapOperation(
            method = "method_62215",
            at = @At(
                    value = "INVOKE",
                    //? >=1.21.11 {
                    /*target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FLnet/minecraft/world/level/MoonPhase;FF)V"
                    *///?} else >=1.21.9 {
                    target = "Lnet/minecraft/client/renderer/SkyRenderer;renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FIFF)V"
                    //?} else >=1.21.6 {
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
    /*static*/
    void optiboxes$renderSkyboxes(
            net.minecraft.client.renderer.SkyRenderer instance,
            PoseStack poseStack,
            //? >=1.21.4 <1.21.9
            /*net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource,*/
            //? <=1.21.3
            /*com.mojang.blaze3d.vertex.Tesselator tesselator,*/
            float timeOfDay,
            //? >=1.21.11 {
            /*net.minecraft.world.level.MoonPhase moonPhase,
            *///?} else {
            int moonPhase,
            //?}
            float rainBrightness,
            float starBrightness,
            //? <1.21.6
            /*net.minecraft.client.renderer.FogParameters fog,*/
            Operation<Void> original
    ) {
        ClientLevel level = Minecraft.getInstance().level;
        if (SkyboxManager.INSTANCE.isEnabled(level)) {
            List<OptiFineSkybox> activeSkyboxes = SkyboxManager.INSTANCE.getActiveSkyboxes();
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.rotate(Axis.YP.rotationDegrees(-90.0F));
            for (OptiFineSkybox optiFineSkybox : activeSkyboxes) {
                //noinspection DataFlowIssue
                OptiFineSkyRenderer.INSTANCE.renderSkybox(optiFineSkybox, modelViewStack, level, optiboxes$tickDelta);
            }
            modelViewStack.popMatrix();
        }

        // Disable Sun, Moon, & Stars in the Nether
        //noinspection DataFlowIssue
        if (!SkyboxManager.INSTANCE.isEnabled(level) ||
                !SkyboxManager.INSTANCE.containsEnabled(Level.NETHER) ||
                !(level.effects() instanceof DimensionSpecialEffects.NetherEffects)) {
            original.call(
                    instance,
                    poseStack,
                    //? >=1.21.4 <1.21.9
                    /*bufferSource,*/
                    //? <=1.21.3
                    /*tesselator,*/
                    timeOfDay,
                    moonPhase,
                    rainBrightness,
                    starBrightness
                    //? <1.21.6
                    /*, fog*/
            );
        }
    }

    //? >=1.21.4 <1.21.9 {
    /*@com.llamalad7.mixinextras.injector.v2.WrapWithCondition(method = "method_62215", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"))
    private boolean optiboxes$moveEndBatch(net.minecraft.client.renderer.MultiBufferSource.BufferSource instance) {
        return !SkyboxManager.INSTANCE.isEnabled(this.level);
    }
    *///?}

    @WrapOperation(
            method = "addSkyPass",
            at = @At(
                    //? >=1.21.9 {
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/state/SkyRenderState;skyType:Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;"
                    //?} else {
                    /*value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;skyType()Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;"
                    *///?}
            )
    )
    private DimensionSpecialEffects.SkyType optiboxes$allowNetherSky(
            //? >= 1.21.9 {
            net.minecraft.client.renderer.state.SkyRenderState instance,
             //?} else {
            /*DimensionSpecialEffects instance,
            *///?}
            Operation<DimensionSpecialEffects.SkyType> original
    ) {
        //noinspection DataFlowIssue
        if (SkyboxManager.INSTANCE.isEnabled(this.level) && SkyboxManager.INSTANCE.containsEnabled(Level.NETHER) && this.level.dimension().equals(Level.NETHER)) {
            return DimensionSpecialEffects.SkyType.OVERWORLD;
        } else {
            return original.call(instance);
        }
    }
}
