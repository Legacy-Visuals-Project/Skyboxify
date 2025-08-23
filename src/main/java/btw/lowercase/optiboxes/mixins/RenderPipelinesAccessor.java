package btw.lowercase.optiboxes.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? >= 1.21.5 {
@Mixin(net.minecraft.client.renderer.RenderPipelines.class)
public interface RenderPipelinesAccessor {
    //? >=1.21.6 {
    @Accessor("MATRICES_PROJECTION_SNIPPET")
    //?} else {
    /*@Accessor("MATRICES_COLOR_FOG_SNIPPET")
    *///?}
    static com.mojang.blaze3d.pipeline.RenderPipeline.Snippet optiboxes$getMatricesProjectionSnippet() {
        return null;
    }
}
//?} else {
/*public interface RenderPipelinesAccessor {
}*/
//?}