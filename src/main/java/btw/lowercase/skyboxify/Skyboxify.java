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
import btw.lowercase.skyboxify.skybox.Skybox;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import btw.lowercase.skyboxify.skybox.SkyboxSkyRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.visuals.legacy.lightconfig.lib.v1.events.EventManager;

import java.nio.file.Path;

@UtilityClass
public class Skyboxify {
	public final String MOD_ID = "@MODID@";
	@Getter
	private final Logger logger = LoggerFactory.getLogger(Skyboxify.class);
	@Getter
	private final EventManager eventManager = new EventManager();
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
}
