package btw.lowercase.optiboxes.skybox;

import btw.lowercase.optiboxes.OptiBoxesClient;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class SkyboxResourceHelper implements IdentifiableResourceReloadListener {
    private ResourceManager resourceManager;

    @Override
    public @NotNull CompletableFuture<Void> reload(
            //? >=1.21.9
            SharedState sharedState,
            //? <1.21.9
            /*PreparationBarrier preparationBarrier,*/
            //? <1.21.9
            /*ResourceManager resourceManager,*/
            Executor backgroundExecutor,
            //? >=1.21.9
            PreparationBarrier preparationBarrier,
            Executor gameExecutor
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

    @Override
    public Identifier getFabricId() {
        return OptiBoxesClient.id("skybox_reader");
    }

    public Stream<Identifier> searchIn(String parent) {
        return this.resourceManager.listResources(parent, path -> true).keySet().stream();
    }

    public InputStream getInputStream(Identifier resourceLocation) {
        try {
            Resource resource = this.resourceManager.getResource(resourceLocation).orElse(null);
            return resource == null ? null : resource.open();
        } catch (IOException e) {
            return null;
        }
    }
}