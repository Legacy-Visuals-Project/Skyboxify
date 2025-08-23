package btw.lowercase.optiboxes.mixins;

import btw.lowercase.optiboxes.skybox.SkyboxManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? >=1.21.6 {
@Mixin(net.minecraft.client.renderer.fog.FogRenderer.class)
//?} else {
/*@Mixin(net.minecraft.client.renderer.FogRenderer.class)
*///?}
public abstract class MixinFogRenderer {
    @Inject(method = "computeFogColor", at = @At("HEAD"), cancellable = true)
    private
    //? <1.21.6
    /*static*/
    void optiboxes$disableNetherFogForSkies(
            Camera camera,
            float partialTick,
            ClientLevel clientLevel,
            int renderDistance,
            float darkenWorldAmount,
            //? >=1.21.6
            boolean bl,
            CallbackInfoReturnable<Vector4f> cir) {
        if (SkyboxManager.INSTANCE.isEnabled(clientLevel) && SkyboxManager.INSTANCE.containsEnabled(Level.NETHER)) {
            cir.setReturnValue(new Vector4f(1.0F, 1.0F, 1.0F, 0.0F));
        }
    }
}
