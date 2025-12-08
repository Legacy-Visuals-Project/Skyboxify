/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 Contributors to the project retain their copyright
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

package btw.lowercase.skyboxify.skybox;

import btw.lowercase.skyboxify.Skyboxify;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

public class SkyboxResourceHelper implements
        //? >=1.21.10 {
        net.minecraft.server.packs.resources.PreparableReloadListener
        //?} else {
        /*net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
         *///?}
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
            if (Skyboxify.getConfig().enabled.isEnabled()) {
                Skyboxify.getLogger().info("Looking for OptiFine/MCPatcher Skies...");
                Skyboxify.convert(this);
            }
        }).thenCompose(preparationBarrier::wait);
    }

    //? <=1.21.8 {
    /*@Override
    public ResourceLocation getFabricId() {
        return Skyboxify.locationOrNull("skybox_reader");
    }
    *///?}

    public Stream<ResourceLocation> searchIn(String parent) {
        return this.resourceManager.listResources(parent, path -> true).keySet().stream();
    }

    public InputStream getInputStream(ResourceLocation resourceLocation) {
        try {
            return this.resourceManager.getResource(resourceLocation).orElseThrow().open();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}