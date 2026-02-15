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
import btw.lowercase.skyboxify.config.SkyboxifyConfig;
import btw.lowercase.skyboxify.events.LevelTickEvent;
import btw.lowercase.skyboxify.events.SkyRenderEvent;
import btw.lowercase.skyboxify.skybox.Skybox;
import btw.lowercase.skyboxify.skybox.SkyboxRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.visuals.legacy.lightconfig.lib.v1.events.EventManager;

import java.nio.file.Path;

@UtilityClass
public class Skyboxify {
	public final Path DEBUG_FOLDER = Minecraft.getInstance().gameDirectory.toPath().resolve("debug_" + SkyboxifyInfo.MOD_ID);
	@Getter
	private final Logger logger = LoggerFactory.getLogger(Skyboxify.class);
	@Getter
	private final EventManager globalEventManager = new EventManager();

	public Identifier locationOrNull(final String path) {
		return Identifier.fromNamespaceAndPath(SkyboxifyInfo.MOD_ID, path);
	}

	public void initialize() {
		final SkyboxifyConfig config = SkyboxifyImpl.config();
		config.load();

		globalEventManager.listen(LevelTickEvent.Client.class, event -> SkyboxifyImpl.skyboxManager().tick(event.getLevel()));

		globalEventManager.listen(SkyRenderEvent.Celestial.class, event -> {
			if (config.enabled.isEnabled()) {
				final SkyRenderEvent.Celestial.Type type = event.getType();
				if ((!config.renderSunMoon.isEnabled() && (type == SkyRenderEvent.Celestial.Type.SUN || type == SkyRenderEvent.Celestial.Type.MOON))
						|| (!config.renderStars.isEnabled() && type == SkyRenderEvent.Celestial.Type.STARS)) {
					event.setCancelled(true);
				}
			}
		});

		//? >=1.21.4 <1.21.9 {
		/*globalEventManager.listen(SkyRenderEvent.SunriseSunset.After.class, event -> {
			if (SkyboxifyImpl.skyboxManager().isEnabled()) {
				event.getBufferSource().endBatch();
			}
		});
		*///?}

		globalEventManager.listen(SkyRenderEvent.EndSky.After.class, event -> {
			if (SkyboxifyImpl.skyboxManager().isEnabled()) {
				renderSkyboxes(event.getLevel(), 0.0F);
			}
		});

		globalEventManager.listen(SkyRenderEvent.SunMoonStars.class, event -> {
			final ClientLevel level = event.getLevel();
			if (SkyboxifyImpl.skyboxManager().isEnabled()) {
				renderSkyboxes(level, event.getTickDelta());
				// Disable Sun, Moon, & Stars in the Nether
				if (SkyboxifyImpl.skyboxManager().containsEnabled(Level.NETHER) && level.dimension().equals(Level.NETHER)){
					event.setCancelled(true);
				}
			}
		});
	}

	private void renderSkyboxes(final ClientLevel level, final float tickDelta) {
		final Matrix4f modelViewMatrix = new Matrix4f(RenderSystem.getModelViewStack()).rotate(Axis.YP.rotationDegrees(-90.0F));
		for (final Skybox skybox : SkyboxifyImpl.skyboxManager().getActive()) {
			SkyboxRenderer.INSTANCE.render(skybox, modelViewMatrix, level, tickDelta);
		}
	}
}
