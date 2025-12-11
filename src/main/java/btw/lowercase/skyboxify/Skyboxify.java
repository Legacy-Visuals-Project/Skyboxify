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

package btw.lowercase.skyboxify;

import btw.lowercase.skyboxify.config.SkyboxifyConfig;
import btw.lowercase.skyboxify.events.LevelTickEvent;
import btw.lowercase.skyboxify.events.SkyRenderEvent;
import btw.lowercase.skyboxify.skybox.*;
import btw.lowercase.skyboxify.utils.ParserCodecs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.mojang.serialization.JsonOps;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.visuals.legacy.lightconfig.lib.v1.events.EventManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class Skyboxify {
	public final String MOD_ID = "@MODID@";
	@Getter
	private final Logger logger = LoggerFactory.getLogger(Skyboxify.class);
	@Getter
	private final EventManager eventManager = new EventManager();
	private final String OPTIFINE_SKY_PARENT = "optifine/sky";
	private final String SKY_PATTERN_ENDING = "(?<world>[\\w-]+)/(?<name>\\w+).properties$";
	private final Pattern OPTIFINE_SKY_PATTERN = Pattern.compile(OPTIFINE_SKY_PARENT + "/" + SKY_PATTERN_ENDING);
	private final String MCPATCHER_SKY_PARENT = "mcpatcher/sky";
	private final Pattern MCPATCHER_SKY_PATTERN = Pattern.compile(MCPATCHER_SKY_PARENT + "/" + SKY_PATTERN_ENDING);
	@Getter
	private SkyboxifyConfig config;

	public ResourceLocation locationOrNull(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}

	public void initialize(final Path configPath) {
		config = new SkyboxifyConfig(configPath);
		config.load();

		eventManager.listen(LevelTickEvent.Client.class, event -> SkyboxManager.INSTANCE.tick(event.getLevel()));

		eventManager.listen(SkyRenderEvent.Celestial.class, event -> {
			if (Skyboxify.getConfig().enabled.isEnabled()) {
				final SkyRenderEvent.Celestial.Type type = event.getType();
				if (!config.renderSunMoon.isEnabled() && (type == SkyRenderEvent.Celestial.Type.SUN || type == SkyRenderEvent.Celestial.Type.MOON)) {
					event.setCancelled(true);
				} else if (!config.renderStars.isEnabled() && type == SkyRenderEvent.Celestial.Type.STARS) {
					event.setCancelled(true);
				}
			}
		});

		//? >=1.21.4 <1.21.9 {
		/*eventManager.listen(SkyRenderEvent.SunriseSunset.After.class, event -> {
			if (SkyboxManager.INSTANCE.isEnabled(event.getLevel())) {
				event.getBufferSource().endBatch();
			}
		});
		*///?}

		eventManager.listen(SkyRenderEvent.EndSky.After.class, event -> renderSkyboxes(event.getLevel(), 0.0F));

		eventManager.listen(SkyRenderEvent.SunMoonStars.class, event -> {
			final ClientLevel clientLevel = event.getLevel();
			renderSkyboxes(clientLevel, event.getTickDelta());
			// Disable Sun, Moon, & Stars in the Nether
			if (SkyboxManager.INSTANCE.isEnabled(clientLevel) && SkyboxManager.INSTANCE.containsEnabled(Level.NETHER) && clientLevel.dimension().equals(Level.NETHER)) {
				event.setCancelled(true);
			}
		});
	}

	private void renderSkyboxes(ClientLevel clientLevel, float tickDelta) {
		if (SkyboxManager.INSTANCE.isEnabled(clientLevel)) {
			final Matrix4f modelViewMatrix = new Matrix4f(RenderSystem.getModelViewStack()).rotate(Axis.YP.rotationDegrees(-90.0F));
			for (Skybox skybox : SkyboxManager.INSTANCE.getActiveSkyboxes()) {
				SkyboxSkyRenderer.INSTANCE.renderSkybox(skybox, modelViewMatrix, clientLevel, tickDelta);
			}
		}
	}

	public void convert(SkyboxResourceHelper skyboxResourceHelper) {
		if (config.processOptiFine.isEnabled()) {
			parseSkyboxes(skyboxResourceHelper, OPTIFINE_SKY_PARENT, OPTIFINE_SKY_PATTERN);
		}

		if (config.processMCPatcher.isEnabled()) {
			parseSkyboxes(skyboxResourceHelper, MCPATCHER_SKY_PARENT, MCPATCHER_SKY_PATTERN);
		}
	}

	private void parseSkyboxes(SkyboxResourceHelper skyboxResourceHelper, String skyParent, Pattern skyPattern) {
		final Map<String, JsonArray> layers = new HashMap<>();
		layers.put("world0", new JsonArray()); // Overworld
		layers.put("world-1", new JsonArray()); // Nether
		layers.put("world1", new JsonArray()); // The End
		skyboxResourceHelper.searchIn(skyParent).filter(id -> id.getPath().endsWith(".properties")).sorted(Comparator.comparing(ResourceLocation::getPath, (id1, id2) -> {
			final Matcher matcherId1 = skyPattern.matcher(id1);
			final Matcher matcherId2 = skyPattern.matcher(id2);
			if (matcherId1.find() && matcherId2.find()) {
				final int a = ParserCodecs.safeParseInteger(matcherId1.group("name").replace("sky", ""), -1);
				final int b = ParserCodecs.safeParseInteger(matcherId2.group("name").replace("sky", ""), -1);
				if (a >= 0 && b >= 0) {
					return a - b;
				}
			}

			return 0;
		})).forEach(id -> {
			final Matcher matcher = skyPattern.matcher(id.getPath());
			if (matcher.find()) {
				final String world = matcher.group("world");
				final String name = matcher.group("name");
				if (world == null || name == null) {
					return;
				}

				if (name.equals("moon_phases") || name.equals("sun")) {
					// TODO/NOTE: Support moon/sun
					logger.warn("Skipping {}, moon_phases/sun aren't currently supported!", id);
					return;
				}

				final InputStream inputStream = skyboxResourceHelper.getInputStream(id);
				if (inputStream == null) {
					logger.error("Error trying to read namespaced identifier: {}", id);
					return;
				}

				final Properties properties = new Properties();
				try {
					properties.load(inputStream);
				} catch (IOException e) {
					logger.error("Error trying to read properties from: {}", id);
					return;
				} finally {
					try {
						inputStream.close();
					} catch (IOException e) {
						logger.error("Error trying to close input stream at namespaced identifier: {}", id);
					}
				}

				final JsonObject json = SkyboxParser.parseSkyProperties(properties, id);
				// NOTE: Don't add broken skies (returns null if broken)
				if (json != null && layers.containsKey(world)) {
					layers.get(world).add(json);
				}
			}
		});

		for (Map.Entry<String, JsonArray> entry : layers.entrySet()) {
			final JsonArray skyLayers = entry.getValue();
			if (!skyLayers.isEmpty()) {
				final JsonObject skyboxJson = new JsonObject();
				skyboxJson.add("layers", skyLayers);
				skyboxJson.addProperty("world", switch (entry.getKey()) {
					case "world0" -> "overworld";
					case "world-1" -> "nether";
					case "world1" -> "end";
					default -> entry.getKey();
				});
				SkyboxManager.INSTANCE.addSkybox(Skybox.CODEC.decode(JsonOps.INSTANCE, skyboxJson).getOrThrow().getFirst());
			}
		}
	}
}
