package btw.lowercase.optiboxes.mixins;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.skybox.SkyboxManager;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class MixinClientLevel {
    @Inject(method = "tick", at = @At("TAIL"))
    private void optiboxes$tick(CallbackInfo ci) {
        if (OptiBoxesClient.getConfig().enabled.isEnabled()) {
            SkyboxManager.INSTANCE.tick((ClientLevel) (Object) this);
        }
    }
}
