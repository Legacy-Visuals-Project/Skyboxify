/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025-2026 lowercasebtw
 * Copyright (C) 2025-2026 Contributors to the project retain their copyright
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

package btw.lowercase.skyboxify;

import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.command.SkyboxifyCommand;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import btw.lowercase.skyboxify.skybox.SkyboxResourceListener;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
//? <1.21.9
//import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Entrypoint
public final class SkyboxifyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Skyboxify.initialize();

        final SkyboxManager skyboxManager = SkyboxifyImpl.skyboxManager();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(new SkyboxifyCommand()));

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private final SkyboxResourceListener listener = new SkyboxResourceListener(skyboxManager);

            @Override
            public @NotNull Identifier getFabricId() {
                return SkyboxResourceListener.SKYBOX_RELOAD_ID;
            }

            @Override
            public @NotNull CompletableFuture<Void> reload(
                    //? >=1.21.9
                    final @NotNull SharedState state,
                    //? <1.21.9 {
                    /*final @NotNull PreparationBarrier preparationBarrier,
                    final @NotNull ResourceManager resourceManager,
                    *///? }
                    final @NotNull Executor preparationExecutor,
                    //? >=1.21.9
                    final @NotNull PreparationBarrier preparationBarrier,
                    final @NotNull Executor reloadExecutor
            ) {
                return this.listener.reload(
                        //? >=1.21.9
                        state,
                        //? <1.21.9 {
                        /*preparationBarrier,
                        resourceManager,
                        *///? }
                        preparationExecutor,
                        //? >=1.21.9
                        preparationBarrier,
                        reloadExecutor
                );
            }
        });
    }
}
