package btw.lowercase.optiboxes.mixins;

import btw.lowercase.optiboxes.skybox.OptiFineSkyRenderer;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import btw.lowercase.optiboxes.skybox.SkyboxManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Unique
    private float optiboxes$tickDelta;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void optiboxes$getLocals(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        this.optiboxes$tickDelta = deltaTracker.getGameTimeDeltaPartialTick(false);
    }

    @WrapOperation(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEndSky(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private void optiboxes$renderEndSkybox(LevelRenderer instance, PoseStack poseStack, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) Matrix4f matrix4f) {
        original.call(instance, poseStack);
        List<OptiFineSkybox> activeSkyboxes = SkyboxManager.INSTANCE.getActiveSkyboxes();
        boolean isEnabled = SkyboxManager.INSTANCE.isEnabled(this.level);
        if (isEnabled) {
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
        }

        if (isEnabled) {
            ClientLevel clientLevel = Objects.requireNonNull(this.level);
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.mul(matrix4f);
            modelViewStack.rotate(Axis.YP.rotationDegrees(-90.0F));
            for (OptiFineSkybox optiFineSkybox : activeSkyboxes) {
                OptiFineSkyRenderer.INSTANCE.renderSkybox(optiFineSkybox, modelViewStack, clientLevel, 0.0F);
            }
            modelViewStack.popMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 2, shift = At.Shift.AFTER))
    private void optiboxes$renderSkyboxes(Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (SkyboxManager.INSTANCE.isEnabled(this.level)) {
            List<OptiFineSkybox> activeSkyboxes = SkyboxManager.INSTANCE.getActiveSkyboxes();
            ClientLevel clientLevel = Objects.requireNonNull(this.level);
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.mul(matrix4f);
            modelViewStack.rotate(Axis.YP.rotationDegrees(-90.0F));
            for (OptiFineSkybox optiFineSkybox : activeSkyboxes) {
                OptiFineSkyRenderer.INSTANCE.renderSkybox(optiFineSkybox, modelViewStack, clientLevel, this.optiboxes$tickDelta);
            }
            modelViewStack.popMatrix();
        }
    }
}
