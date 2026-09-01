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
import btw.lowercase.skyboxify.events.SkyRenderEvent;
import btw.lowercase.skyboxify.skybox.renderer.SkyFeatureRenderer;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.client.render.vertex.VertexBuffer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class, priority = 900)
public abstract class MixinWorldRenderer_SkyEvents {
    @Shadow
    private ClientWorld world;

    @Unique
    private static SkyFeatureRenderer skyboxify$skyFeatureRenderer = null;

    @Inject(method = "renderSky", at = @At("HEAD"))
    private void skyboxify$getLocals(final float tickDelta, final int anaglyphRenderPass, final CallbackInfo ci) {
        if (skyboxify$skyFeatureRenderer == null) {
            skyboxify$skyFeatureRenderer = new SkyFeatureRenderer(Minecraft.getInstance().getRenderTarget());
        }
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/world/WorldRenderer;renderEndSky()V", shift = At.Shift.AFTER))
    private void skyboxify$renderEndSkybox(final float tickDelta, final int anaglyphRenderPass, final CallbackInfo ci) {
        Skyboxify.eventManager().dispatch(new SkyRenderEvent.EndSky.After(skyboxify$skyFeatureRenderer, this.world));
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/vertex/VertexBuffer;draw(I)V", ordinal = 0))
    private boolean skyboxify$skyDiscEvent$top(final VertexBuffer instance, final int mode) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.topDisc()).isCancelled();
    }

    @Unique
    private boolean skyboxify$renderSunMoonStars = true;

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/platform/GlStateManager;blendFuncSeparate(IIII)V", ordinal = 1))
    private void skyboxify$renderSkyboxes(final float tickDelta, final int anaglyphRenderPass, final CallbackInfo ci) {
        skyboxify$renderSunMoonStars = !Skyboxify.eventManager().dispatch(new SkyRenderEvent.SunMoonStars(skyboxify$skyFeatureRenderer, this.world, tickDelta)).isCancelled();
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/vertex/Tesselator;end()V", ordinal = 1))
    private boolean skyboxify$toggleSun(final Tesselator instance) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.sun()).isCancelled() && skyboxify$renderSunMoonStars;
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/vertex/Tesselator;end()V", ordinal = 2))
    private boolean skyboxify$toggleMoon(final Tesselator instance) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.moon()).isCancelled() && skyboxify$renderSunMoonStars;
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/vertex/VertexBuffer;draw(I)V", ordinal = 1))
    private boolean skyboxify$toggleStars(final VertexBuffer instance, final int mode) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.stars()).isCancelled() && skyboxify$renderSunMoonStars;
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/vertex/VertexBuffer;draw(I)V", ordinal = 2))
    private boolean skyboxify$skyDiscEvent$bottom(final VertexBuffer instance, final int mode) {
        return !Skyboxify.eventManager().dispatch(SkyRenderEvent.bottomDisc()).isCancelled();
    }

//    @WrapOperation(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;skyType()Lnet/minecraft/client/renderer/DimensionSpecialEffects$SkyType;", opcode = Opcodes.GETFIELD))
//    private DimensionSpecialEffects.SkyType skyboxify$allowNetherSky(final DimensionSpecialEffects instance, final Operation<DimensionSpecialEffects.SkyType> original) {
//        //noinspection DataFlowIssue
//        if (SkyboxifyImpl.skyboxManager().isEnabled() && SkyboxifyImpl.skyboxManager().containsEnabled(Level.NETHER) && this.world.dimension().equals(Level.NETHER)) {
//            return DimensionSpecialEffects.SkyType.NORMAL;
//        } else {
//            return original.call(instance);
//        }
//    }
}
