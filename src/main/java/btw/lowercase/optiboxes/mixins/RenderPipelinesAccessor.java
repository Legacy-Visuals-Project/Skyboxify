package btw.lowercase.optiboxes.mixins;

import org.spongepowered.asm.mixin.Mixin;

//? >= 1.21.5 {
@Mixin(net.minecraft.client.renderer.RenderPipelines.class)
 //?} else {
/*@Mixin(net.minecraft.client.Minecraft.class)
*///?}
public interface RenderPipelinesAccessor {
    //? >= 1.21.5 {
    @org.spongepowered.asm.mixin.gen.Accessor(
            //? >=1.21.6 {
            "MATRICES_PROJECTION_SNIPPET"
            //?} else {
            /*"MATRICES_COLOR_FOG_SNIPPET"
            *///? }
    )
    static com.mojang.blaze3d.pipeline.RenderPipeline.Snippet optiboxes$getMatricesProjectionSnippet() {
        return null;
    }
    //?}
}