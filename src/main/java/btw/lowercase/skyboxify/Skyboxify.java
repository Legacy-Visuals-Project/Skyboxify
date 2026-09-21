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

import btw.lowercase.skyboxify.api.SkyboxifyApi;
import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.config.SkyboxifyConfig;
import btw.lowercase.skyboxify.events.EventManager;
import btw.lowercase.skyboxify.events.SkyEvents;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public final class Skyboxify {
    private static final EventManager globalEventManager = new EventManager();

    public static EventManager eventManager() {
        return globalEventManager;
    }

    public static Identifier locationOrNull(final String path) {
        return Identifier.fromNamespaceAndPath(SkyboxifyInfo.MOD_ID, path);
    }

    public static void initialize() {
        final SkyboxifyApi impl = SkyboxifyImpl.getInstance();
        impl.getConfigHandler().load();

        final SkyboxManager skyboxManager = impl.getSkyboxManager();
        final SkyboxifyConfig config = impl.getConfig();
        ClientTickEvents.END_LEVEL_TICK.register(skyboxManager::tick);

        globalEventManager.listen(SkyEvents.Disc.class, event -> {
            if (config.enabled && !config.renderSky) {
                event.setCancelled(true);
            }
        });

        globalEventManager.listen(SkyEvents.Celestial.class, event -> {
            if (config.enabled) {
                final SkyEvents.Celestial.Type type = event.getType();
                if (!config.renderSunMoon && (type == SkyEvents.Celestial.Type.SUN || type == SkyEvents.Celestial.Type.MOON)) {
                    event.setCancelled(true);
                }

                if (skyboxManager.isEnabled() && type == SkyEvents.Celestial.Type.STARS) {
                    if (config.renderStars) {
                        return;
                    }

                    event.setCancelled(true);
                }
            }
        });

        //? >=1.21.4 <1.21.9 {
		/*globalEventManager.listen(SkyEvents.SunriseSunsetAfter.class, event -> {
			if (skyboxManager.isEnabled()) {
				event.bufferSource().endBatch(); // Fix horizon rendering over the skybox
			}
		});
		*///?}

        globalEventManager.listen(SkyEvents.Extraction.class, event -> {
            if (skyboxManager.isEnabled()) {
                final float delta = event.level().dimension().equals(Level.END) ? 0.0F : event.tickDelta();
                skyboxManager.extractSkyboxes(event.skyFeatureRenderer(), event.level(), delta);
            }
        });

        globalEventManager.listen(SkyEvents.EndSky.After.class, event -> {
            if (skyboxManager.isEnabled()) {
                event.skyFeatureRenderer().endFrame(
                        //? >=26.3
                        event.pass()
                );
            }
        });

        globalEventManager.listen(SkyEvents.SunMoonStars.class, event -> {
            if (skyboxManager.isEnabled()) {
                event.skyFeatureRenderer().endFrame(
                        //? >=26.3
                        event.pass()
                );
                if (event.isInNether()) {
                    event.setCancelled(true);
                }
            }
        });
    }
}
