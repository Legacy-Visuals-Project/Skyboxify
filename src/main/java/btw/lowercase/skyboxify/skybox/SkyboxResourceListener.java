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

package btw.lowercase.skyboxify.skybox;

import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.skybox.impl.Skybox;
import btw.lowercase.skyboxify.utils.Id;
import btw.lowercase.skyboxify.utils.ParserCodecs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.ornithemc.osl.core.api.util.function.IOSupplier;
import net.ornithemc.osl.resource.loader.api.resource.ResourceType;
import net.ornithemc.osl.resource.loader.api.resource.manager.ResourceManager;
import net.ornithemc.osl.resource.loader.api.resource.pack.ResourceConsumer;
import net.ornithemc.osl.resource.loader.api.resource.pack.ResourcePack;
import net.ornithemc.osl.resource.loader.api.resource.reload.ReloadStep;
import net.ornithemc.osl.resource.loader.api.resource.reload.ResourceReloadListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkyboxResourceListener implements ResourceReloadListener {
    private static final String OPTIFINE_SKY_PARENT = "optifine/sky";
    private static final String SKY_PATTERN_ENDING = "(?<dimension>[\\w-]+)/(?<name>\\w+).properties$";
    private static final Pattern OPTIFINE_SKY_PATTERN = Pattern.compile(OPTIFINE_SKY_PARENT + "/" + SKY_PATTERN_ENDING);
    private static final String MCPATCHER_SKY_PARENT = "mcpatcher/sky";
    private static final Pattern MCPATCHER_SKY_PATTERN = Pattern.compile(MCPATCHER_SKY_PARENT + "/" + SKY_PATTERN_ENDING);
    private static final Logger LOGGER = LoggerFactory.getLogger(SkyboxResourceListener.class);

    private final SkyboxManager skyboxManager;

    public SkyboxResourceListener(final SkyboxManager skyboxManager) {
        this.skyboxManager = skyboxManager;
    }

    private static ResourceConsumer filterResource(final List<Id> list) {
        return (identifier, ioSupplier) -> {
            if (identifier.identifier().endsWith(".properties")) {
                list.add(Id.fromVanilla(identifier));
            }
        };
    }

    private static Comparator<Id> compareLocations(final Pattern pattern) {
        return Comparator.comparing(Id::path, (first, second) -> {
            final Matcher matcherId1 = pattern.matcher(first);
            final Matcher matcherId2 = pattern.matcher(second);
            if (matcherId1.find() && matcherId2.find()) {
                final int a = ParserCodecs.safeParseInteger(matcherId1.group("name").replace("sky", ""), -1);
                final int b = ParserCodecs.safeParseInteger(matcherId2.group("name").replace("sky", ""), -1);
                if (a >= 0 && b >= 0) {
                    return a - b;
                }
            }

            return 0;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> reloadResources(final SharedState state, final ReloadStep previousStep, final Executor preparationExecutor, final Executor reloadExecutor) {
        final ResourceManager manager = state.resourceManager();
        return CompletableFuture.supplyAsync(() -> {
            final List<Skybox> skyboxes = new ArrayList<>();
            manager.getResourcePacks().forEach(pack -> this.parseSkyboxesInPack(pack, skyboxes));
            return skyboxes;
        }, preparationExecutor)
            .thenCompose(previousStep::await)
            .thenAcceptAsync(this::applySkyboxes, reloadExecutor);
    }

    @Override
    public void resourcesReloaded(final ResourceManager manager) {
        final List<Skybox> skyboxes = new ArrayList<>();
        manager.getResourcePacks().forEach(pack -> this.parseSkyboxesInPack(pack, skyboxes));
        this.applySkyboxes(skyboxes);
    }

    private void parseSkyboxesInPack(final ResourcePack pack, final List<Skybox> outputSkyboxes) {
        final List<Id> skies = new ArrayList<>();
        pack.findResources(ResourceType.CLIENT_ASSETS, "minecraft", OPTIFINE_SKY_PARENT, filterResource(skies));
        skies.sort(compareLocations(OPTIFINE_SKY_PATTERN));
        if (!skies.isEmpty()) {
            this.parseSkyboxes(pack, OPTIFINE_SKY_PATTERN, skies, outputSkyboxes);
        } else {
            if (SkyboxifyImpl.config().debug.isEnabled()) {
                LOGGER.info("Couldn't find any skies inside \"{}\" under \"optifine\", searching for skies under \"mcpatcher\" instead...", pack.getName());
            }

            pack.findResources(ResourceType.CLIENT_ASSETS, "minecraft", MCPATCHER_SKY_PARENT, filterResource(skies));
            skies.sort(compareLocations(MCPATCHER_SKY_PATTERN));
            this.parseSkyboxes(pack, MCPATCHER_SKY_PATTERN, skies, outputSkyboxes);
        }
    }

    private void parseSkyboxes(final ResourcePack pack, final Pattern skyPattern, final List<Id> skies, final List<Skybox> outputSkyboxes) {
        final Map<String, JsonArray> layers = new HashMap<>();
        int loadedCount = 0;
        skies.forEach(id -> {
            final Matcher matcher = skyPattern.matcher(id.path());
            if (!matcher.find()) {
                return;
            }

            final String dimension = matcher.group("dimension");
            final String name = matcher.group("name");
            if (dimension == null || name == null) {
                return;
            }

            // TODO/NOTE: Support moon/sun? (apparently doesn't even work in OptiFine)
            if (name.equals("moon_phases") || name.equals("sun")) {
                if (SkyboxifyImpl.config().debug.isEnabled()) {
                    LOGGER.warn("Skipping {}, moon_phases/sun aren't currently supported!", id);
                }

                return;
            }

            final IOSupplier<InputStream> resource = pack.getResource(ResourceType.CLIENT_ASSETS, id);
            if (resource == null) {
                LOGGER.error("Error trying to read namespaced identifier: {}", id);
                return;
            }

            final Properties properties = new Properties();
            try {
                final InputStream inputStream = resource.get();
                properties.load(inputStream);
                inputStream.close();
            } catch (final IOException ignored) {
                LOGGER.error("Error trying to read properties from: {}", id);
                return;
            }

            final JsonObject json = SkyboxParser.parseSkyProperties(properties, id, pack);
            // NOTE: Don't add broken skies (returns null if broken)
            if (json != null) {
                layers.computeIfAbsent(dimension, key -> new JsonArray()).add(json);
            }
        });

        for (final Map.Entry<String, JsonArray> entry : layers.entrySet()) {
            final JsonArray skyLayers = entry.getValue();
            if (!skyLayers.isEmpty()) {
                final int dimensionId = Integer.parseInt(entry.getKey().replace("world", ""));
                final Id dimension = SkyboxifyImpl.getInstance().getModernDimension(dimensionId);
                if (dimension == null) {
                    if (SkyboxifyImpl.config().debug.isEnabled()) {
                        LOGGER.warn("Tried to load Skybox with legacy dimension properties {} but no modern dimension identifier mapping was found, skipping!", dimensionId);
                    }

                    // Skip as unknown dimension
                    continue;
                }

                final JsonObject skyboxJson = new JsonObject();
                skyboxJson.addProperty("dimension", dimension.toString());
                skyboxJson.add("layers", skyLayers);

                final Skybox skybox = Skybox.CODEC.decode(JsonOps.INSTANCE, skyboxJson).getOrThrow().getFirst();
                skybox.setPackName(pack.getName());
                outputSkyboxes.add(skybox);
                loadedCount++;
            }
        }

        if (loadedCount > 0 && SkyboxifyImpl.config().debug.isEnabled()) {
            LOGGER.info("Loaded {} {} from \"{}\"!", loadedCount, (loadedCount == 1 ? "skies" : "sky"), pack.getName());
        }
    }

    private void applySkyboxes(final List<Skybox> skyboxes) {
        this.skyboxManager.clearSkyboxes();
        skyboxes.forEach(this.skyboxManager::addSkybox);
        // Tick at-least once as a trick for the sky to show up immediately while in the menu
        this.skyboxManager.tick();
    }
}