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
import btw.lowercase.skyboxify.utils.ParserCodecs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
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
		net.minecraft.server.packs.resources.PreparableReloadListener
		//?} else {
		/*net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
		 *///?}
{
	private static final String OPTIFINE_SKY_PARENT = "optifine/sky";
	private static final String SKY_PATTERN_ENDING = "(?<dimension>[\\w-]+)/(?<name>\\w+).properties$";
	private static final Pattern OPTIFINE_SKY_PATTERN = Pattern.compile(OPTIFINE_SKY_PARENT + "/" + SKY_PATTERN_ENDING);
	private static final String MCPATCHER_SKY_PARENT = "mcpatcher/sky";
	private static final Pattern MCPATCHER_SKY_PATTERN = Pattern.compile(MCPATCHER_SKY_PARENT + "/" + SKY_PATTERN_ENDING);
	private static final Logger LOGGER = LoggerFactory.getLogger(SkyboxResourceHelper.class);

	private static PackResources.ResourceOutput filterResource(final List<Identifier> list) {
		return (resourceLocation, ioSupplier) -> {
			if (resourceLocation.getPath().endsWith(".properties")) {
				list.add(resourceLocation);
			}
		};
	}

	private static Comparator<Identifier> compareLocations(final Pattern pattern) {
		return Comparator.comparing(Identifier::getPath, (first, second) -> {
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

	//? <=1.21.8 {
    /*@Override
    public Identifier getFabricId() {
        return btw.lowercase.skyboxify.Skyboxify.locationOrNull("skybox_reader");
    }
    *///?}

	@Override
	public @NotNull CompletableFuture<Void> reload(
			//? >=1.21.9
			SharedState sharedState,
			//? <1.21.9
			//PreparationBarrier preparationBarrier,
			//? <1.21.9
			//ResourceManager resourceManager,
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
			if (SkyboxifyImpl.config().enabled.isEnabled()) {
				SkyboxifyImpl.skyboxManager().clearSkyboxes();
				theResourceManager.listPacks().forEach(pack -> {
					final List<Identifier> optiFineSkies = new ArrayList<>();
					pack.listResources(PackType.CLIENT_RESOURCES, Identifier.DEFAULT_NAMESPACE, OPTIFINE_SKY_PARENT, filterResource(optiFineSkies));
					optiFineSkies.sort(compareLocations(OPTIFINE_SKY_PATTERN));

					final List<Identifier> mcPatcherSkies = new ArrayList<>();
					pack.listResources(PackType.CLIENT_RESOURCES, Identifier.DEFAULT_NAMESPACE, MCPATCHER_SKY_PARENT, filterResource(mcPatcherSkies));
					mcPatcherSkies.sort(compareLocations(MCPATCHER_SKY_PATTERN));

					Pattern skyPattern = OPTIFINE_SKY_PATTERN;
					if (optiFineSkies.isEmpty()) {
						if (SkyboxifyImpl.config().debug.isEnabled()) {
							LOGGER.info("Couldn't find any skies inside \"{}\" under \"optifine\", searching for skies under \"mcpatcher\" instead...", pack.packId());
						}

						skyPattern = MCPATCHER_SKY_PATTERN;
					}

					final List<Identifier> skies = (skyPattern == OPTIFINE_SKY_PATTERN ? optiFineSkies : mcPatcherSkies);
					if (!skies.isEmpty()) {
						final int count = this.parseSkyboxesInPack(pack, skies, skyPattern);
						if (count > 0 && SkyboxifyImpl.config().debug.isEnabled()) {
							LOGGER.info("Loaded {} {} from \"{}\"!", count, (count == 1 ? "skies" : "sky"), pack.packId());
						}
					}
				});
			}
		}).thenCompose(preparationBarrier::wait);
	}

	private int parseSkyboxesInPack(final PackResources packResources, final List<Identifier> skies, final Pattern skyPattern) {
		final Map<String, JsonArray> layers = new HashMap<>();
		int count = 0;
		skies.forEach(id -> {
			final Matcher matcher = skyPattern.matcher(id.getPath());
			if (!matcher.find()) {
				return;
			}

			final String dimension = matcher.group("dimension");
			final String name = matcher.group("name");
			if (dimension == null || name == null) {
				return;
			}

			if (name.equals("moon_phases") || name.equals("sun")) {
				// TODO/NOTE: Support moon/sun? (apparently doesn't even work in OptiFine)
				if (SkyboxifyImpl.config().debug.isEnabled()) {
					LOGGER.warn("Skipping {}, moon_phases/sun aren't currently supported!", id);
				}

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
			} catch (final IOException ignored) {
				LOGGER.error("Error trying to read properties from: {}", id);
				return;
			}

			final JsonObject json = SkyboxParser.parseSkyProperties(properties, id, packResources);
			// NOTE: Don't add broken skies (returns null if broken)
			if (json != null) {
				layers.computeIfAbsent(dimension, key -> new JsonArray()).add(json);
			}
		});

		for (final Map.Entry<String, JsonArray> entry : layers.entrySet()) {
			final JsonArray skyLayers = entry.getValue();
			if (!skyLayers.isEmpty()) {
				final int dimensionId = Integer.parseInt(entry.getKey().replace("world", ""));
				final Identifier dimension = SkyboxifyImpl.getInstance().getModernDimension(dimensionId);
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
				skybox.setPackName(packResources.packId());
				SkyboxifyImpl.skyboxManager().addSkybox(skybox);

				count++;
			}
		}

		return count;
	}
}