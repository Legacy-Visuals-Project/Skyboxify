package btw.lowercase.skyboxify.mixins;

import btw.lowercase.skyboxify.skybox.renderer.Geometry;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GameNarrator;destroy()V"))
    private void skyboxify$closeBuffers(final CallbackInfo ci) {
        Geometry.DEFAULT.close();
    }
}
