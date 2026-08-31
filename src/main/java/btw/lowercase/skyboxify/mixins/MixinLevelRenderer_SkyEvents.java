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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer_SkyEvents {
    @Shadow
    @Nullable
    private ClientLevel level;

    @Unique
    private static SkyFeatureRenderer skyboxify$skyFeatureRenderer = null;

    @Unique
    private static ClientLevel skyboxify$level = null;

    @Inject(method = "renderSky", at = @At("HEAD"))
    private void skyboxify$getLocals(final Matrix4f frustumMatrix, final Matrix4f projectionMatrix, final float tickDelta, final Camera camera, final boolean isFoggy, final Runnable skyFogSetup, final CallbackInfo ci) {
        if (skyboxify$skyFeatureRenderer == null) {
            skyboxify$skyFeatureRenderer = new SkyFeatureRenderer(Minecraft.getInstance().getMainRenderTarget());
        }

        skyboxify$level = this.level;
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEndSky(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER))
    private void skyboxify$renderEndSkybox(final CallbackInfo ci, @Local(argsOnly = true, ordinal = 0) final Matrix4f skyViewMatrix) {
        Skyboxify.eventManager().dispatch(new SkyRenderEvent.EndSky.After(skyboxify$skyFeatureRenderer, skyboxify$level, skyViewMatrix));
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 0))
    private boolean skyboxify$skyDiscEvent$top(final VertexBuffer instance, final Matrix4f modelViewMatrix, final Matrix4f projectionMatrix, final ShaderInstance shader) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.topDisc()).isCancelled();
    }

    @Unique
    private boolean skyboxify$renderSunMoonStars = true;

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 2, shift = At.Shift.AFTER))
    private void skyboxify$renderSkyboxes(final Matrix4f skyViewMatrix, final Matrix4f projectionMatrix, final float tickDelta, final Camera camera, final boolean isFoggy, final Runnable skyFogSetup, final CallbackInfo ci) {
        skyboxify$renderSunMoonStars = !Skyboxify.eventManager().dispatch(new SkyRenderEvent.SunMoonStars(skyboxify$skyFeatureRenderer, skyboxify$level, skyViewMatrix, tickDelta)).isCancelled();
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;drawWithShader(Lcom/mojang/blaze3d/vertex/MeshData;)V", ordinal = 1))
    private boolean skyboxify$toggleSun(final MeshData meshData) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.sun()).isCancelled() && skyboxify$renderSunMoonStars;
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;drawWithShader(Lcom/mojang/blaze3d/vertex/MeshData;)V", ordinal = 2))
    private boolean skyboxify$toggleMoon(final MeshData meshData) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.moon()).isCancelled() && skyboxify$renderSunMoonStars;
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1))
    private boolean skyboxify$toggleStars(final VertexBuffer instance, final Matrix4f modelViewMatrix, final Matrix4f projectionMatrix, final ShaderInstance shader) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.stars()).isCancelled() && skyboxify$renderSunMoonStars;
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 2))
    private boolean skyboxify$skyDiscEvent$bottom(final VertexBuffer instance, final Matrix4f modelViewMatrix, final Matrix4f projectionMatrix, final ShaderInstance shader) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.bottomDisc()).isCancelled();
    }

    @WrapOperation(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;skyType()Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;", opcode = Opcodes.GETFIELD))
    private DimensionSpecialEffects.SkyType skyboxify$allowNetherSky(final DimensionSpecialEffects instance, final Operation<DimensionSpecialEffects.SkyType> original) {
        //noinspection DataFlowIssue
        if (SkyboxifyImpl.skyboxManager().isEnabled() && SkyboxifyImpl.skyboxManager().containsEnabled(Level.NETHER) && skyboxify$level.dimension().equals(Level.NETHER)) {
            return DimensionSpecialEffects.SkyType.NORMAL;
        } else {
            return original.call(instance);
        }
    }
}
