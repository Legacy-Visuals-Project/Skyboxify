package btw.lowercase.optiboxes.mixins;

import btw.lowercase.optiboxes.OptiBoxesClient;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class MixinSkyRenderer {
    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;drawWithShader(Lcom/mojang/blaze3d/vertex/MeshData;)V", ordinal = 1))
    private boolean optiboxes$toggleSun(MeshData meshData) {
        return !OptiBoxesClient.getConfig().enabled.isEnabled() || OptiBoxesClient.getConfig().renderSunMoon.isEnabled();
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;drawWithShader(Lcom/mojang/blaze3d/vertex/MeshData;)V", ordinal = 2))
    private boolean optiboxes$toggleMoon(MeshData meshData) {
        return !OptiBoxesClient.getConfig().enabled.isEnabled() || OptiBoxesClient.getConfig().renderSunMoon.isEnabled();
    }

    @WrapWithCondition(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1))
    private boolean optiboxes$toggleStars(VertexBuffer instance, Matrix4f matrix4f, Matrix4f matrix4f2, ShaderInstance shaderInstance) {
        return !OptiBoxesClient.getConfig().enabled.isEnabled() || OptiBoxesClient.getConfig().renderStars.isEnabled();
    }
}
