package btw.lowercase.optiboxes.skybox;

import btw.lowercase.optiboxes.OptiBoxesClient;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class SkyboxResourceHelper implements
        //? >=1.21.10 {
        net.minecraft.server.packs.resources.PreparableReloadListener
        //? } else {
        /*net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
        *///? }
{
    private ResourceManager resourceManager;

    @Override
    public @NotNull CompletableFuture<Void> reload(
            //? >=1.21.9
            SharedState sharedState,
            //? <1.21.9
            /*PreparationBarrier preparationBarrier,*/
            //? <1.21.9
            /*ResourceManager resourceManager,*/
            @NotNull Executor backgroundExecutor,
            //? >=1.21.9
            PreparationBarrier preparationBarrier,
            @NotNull Executor gameExecutor
    ) {
        this.resourceManager =
            //? >=1.21.9 {
            sharedState.resourceManager();
            //?} else {
            /*resourceManager;
            *///?}
        return CompletableFuture.runAsync(() -> {
            SkyboxManager.INSTANCE.clearSkyboxes();
            if (OptiBoxesClient.getConfig().enabled.isEnabled()) {
                OptiBoxesClient.LOGGER.info("Looking for OptiFine/MCPatcher Skies...");
                OptiBoxesClient.convert(this);
            }
        }).thenCompose(preparationBarrier::wait);
    }

    //? <=1.21.8 {
    /*@Override
    public Identifier getFabricId() {
        return OptiBoxesClient.locationOrNull("skybox_reader");
    }
    *///? }

    public Stream<Identifier> searchIn(String parent) {
        return this.resourceManager.listResources(parent, path -> true).keySet().stream();
    }

    public InputStream getInputStream(Identifier resourceLocation) {
        try {
            return this.resourceManager.getResource(resourceLocation).orElseThrow().open();
        } catch (Exception ignored) {
            return null;
        }
    }
}