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
import btw.lowercase.skyboxify.utils.ParserCodecs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
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

public class SkyboxResourceHelper implements
        //? >=1.21.10 {
        PreparableReloadListener
        //?} else {
        /*net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
         *///?}
{
	private static final String OPTIFINE_SKY_PARENT = "optifine/sky";
	private static final String SKY_PATTERN_ENDING = "(?<world>[\\w-]+)/(?<name>\\w+).properties$";
	private static final Pattern OPTIFINE_SKY_PATTERN = Pattern.compile(OPTIFINE_SKY_PARENT + "/" + SKY_PATTERN_ENDING);
	private static final String MCPATCHER_SKY_PARENT = "mcpatcher/sky";
	private static final Pattern MCPATCHER_SKY_PATTERN = Pattern.compile(MCPATCHER_SKY_PARENT + "/" + SKY_PATTERN_ENDING);
	private static final Logger LOGGER = LoggerFactory.getLogger(SkyboxResourceHelper.class);

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
        final ResourceManager theResourceManager =
			//? >=1.21.9 {
			sharedState.resourceManager();
			//?} else {
			/*resourceManager;
			 *///?}
        return CompletableFuture.runAsync(() -> {
            SkyboxManager.INSTANCE.clearSkyboxes();
            if (Skyboxify.getConfig().enabled.isEnabled()) {
				LOGGER.info("Looking for OptiFine/MCPatcher Skies...");
				theResourceManager.listPacks().forEach(pack -> {
					final List<ResourceLocation> optifineSkies = new ArrayList<>();
					pack.listResources(PackType.CLIENT_RESOURCES, ResourceLocation.DEFAULT_NAMESPACE, OPTIFINE_SKY_PARENT, (resourceLocation, inputStreamIoSupplier) -> optifineSkies.add(resourceLocation));

					final List<ResourceLocation> mcpatcherSkies = new ArrayList<>();
					pack.listResources(PackType.CLIENT_RESOURCES, ResourceLocation.DEFAULT_NAMESPACE, MCPATCHER_SKY_PARENT, (resourceLocation, inputStreamIoSupplier) -> mcpatcherSkies.add(resourceLocation));

					this.parseSkyboxes(pack, optifineSkies, mcpatcherSkies);
				});
            }
        }).thenCompose(preparationBarrier::wait);
    }

    //? <=1.21.8 {
    /*@Override
    public ResourceLocation getFabricId() {
        return Skyboxify.locationOrNull("skybox_reader");
    }
    *///?}

	private void parseSkyboxes(final PackResources packResources, final List<ResourceLocation> optifineSkies, final List<ResourceLocation> mcpatcherSkies) {
		if (optifineSkies.isEmpty() && mcpatcherSkies.isEmpty()) {
			return;
		}

		Pattern skyPattern = OPTIFINE_SKY_PATTERN;
		List<ResourceLocation> skies = optifineSkies.stream().filter(SkyboxResourceHelper::isProperties).sorted(compareLocations(skyPattern)).toList();
		if (optifineSkies.isEmpty()) {
			LOGGER.info("Couldn't find any skies under \"optifine\", searching for skies under \"mcpatcher\" instead...");
			skyPattern = MCPATCHER_SKY_PATTERN;
			skies = mcpatcherSkies.stream().filter(SkyboxResourceHelper::isProperties).sorted(compareLocations(skyPattern)).toList();
		}

		if (!skies.isEmpty()) {
			this.parseSkyboxesInPack(packResources, skies, skyPattern);
		}
	}

	private void parseSkyboxesInPack(final PackResources packResources, final List<ResourceLocation> skies, final Pattern skyPattern) {
		final Map<String, JsonArray> layers = new HashMap<>();
		skies.forEach(id -> {
			final Matcher matcher = skyPattern.matcher(id.getPath());
			if (!matcher.find()) {
				return;
			}

			final String world = matcher.group("world");
			final String name = matcher.group("name");
			if (world == null || name == null) {
				return;
			}

			if (name.equals("moon_phases") || name.equals("sun")) {
				// TODO/NOTE: Support moon/sun? (apparently doesn't even work in OptiFine)
				LOGGER.warn("Skipping {}, moon_phases/sun aren't currently supported!", id);
				return;
			}

			final IoSupplier<InputStream> resource = packResources.getResource(PackType.CLIENT_RESOURCES, id);
			if (resource == null) {
				LOGGER.error("Error trying to read namespaced identifier: {}", id);
				return;
			}

			final Properties properties = new Properties();
			try {
				final InputStream inputStream = resource.get();
				properties.load(inputStream);
				inputStream.close();
			} catch (IOException e) {
				LOGGER.error("Error trying to read properties from: {}", id);
				return;
			}

			final JsonObject json = SkyboxParser.parseSkyProperties(properties, id);
			// NOTE: Don't add broken skies (returns null if broken)
			if (json != null) {
				layers.computeIfAbsent(world, key -> new JsonArray()).add(json);
			}
		});

		for (Map.Entry<String, JsonArray> entry : layers.entrySet()) {
			final JsonArray skyLayers = entry.getValue();
			if (!skyLayers.isEmpty()) {
				final JsonObject skyboxJson = new JsonObject();
				skyboxJson.addProperty("world", switch (entry.getKey()) {
					case "world0" -> "overworld";
					case "world-1" -> "nether";
					case "world1" -> "end";
					default -> entry.getKey().replaceAll("-", "_");
				});
				skyboxJson.add("layers", skyLayers);
				SkyboxManager.INSTANCE.addSkybox(Skybox.CODEC.decode(JsonOps.INSTANCE, skyboxJson).getOrThrow().getFirst());
			}
		}
	}

	private static Comparator<ResourceLocation> compareLocations(final Pattern pattern) {
		return Comparator.comparing(ResourceLocation::getPath, (first, second) -> {
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

	private static boolean isProperties(final ResourceLocation location) {
		return location.getPath().endsWith(".properties");
	}
}