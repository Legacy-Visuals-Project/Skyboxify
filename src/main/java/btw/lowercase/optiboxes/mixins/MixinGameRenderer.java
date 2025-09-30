package btw.lowercase.optiboxes.mixins;

import btw.lowercase.optiboxes.skybox.SkyboxManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

//? >=1.21.6 {
@Mixin(net.minecraft.client.renderer.GameRenderer.class)
//?} else {
/*@Mixin(net.minecraft.client.renderer.LevelRenderer.class)
*///?}
public abstract class MixinGameRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;isFoggyAt(II)Z"))
    private boolean optiboxes$allowNetherSky(DimensionSpecialEffects effects, int x, int y, Operation<Boolean> original) {
        if (SkyboxManager.INSTANCE.isEnabled(this.minecraft.level) &&
                SkyboxManager.INSTANCE.containsEnabled(Level.NETHER) &&
                effects instanceof DimensionSpecialEffects.NetherEffects) {
            return false;
        } else {
            return original.call(effects, x, y);
        }
    }
}
