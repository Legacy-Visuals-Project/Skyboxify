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
import btw.lowercase.skyboxify.skybox.SkyEvents;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public final class Skyboxify {
    public static Identifier locationOrNull(final String path) {
        return Identifier.fromNamespaceAndPath(SkyboxifyInfo.MOD_ID, path);
    }

    public static void initialize() {
        final SkyboxifyApi impl = SkyboxifyImpl.getInstance();
        impl.getConfigHandler().load();

        final SkyboxManager skyboxManager = impl.getSkyboxManager();
        final SkyboxifyConfig config = impl.getConfig();
        ClientTickEvents.END_LEVEL_TICK.register(skyboxManager::tick);

        SkyEvents.SKY_DISC.register(it -> !config.enabled || config.renderSky);

        SkyEvents.CELESTIAL.register(type -> {
            if (config.enabled) {
                if (!config.renderSunMoon && (type == SkyEvents.CelestialType.SUN || type == SkyEvents.CelestialType.MOON)) {
                    return false;
                }

                if (skyboxManager.isEnabled() && type == SkyEvents.CelestialType.STARS) {
                    return config.renderStars;
                }
            }

            return true;
        });


        //? >=1.21.4 <1.21.9 {
		/*SkyEvents.SUNRISE_SUNSET_AFTER.register((bufferSource) -> {
			if (skyboxManager.isEnabled()) {
				bufferSource.endBatch(); // Fix horizon rendering over the skybox
			}
		});
		*///?}

        SkyEvents.EXTRACTION.register((renderer, level, tickDelta) -> {
            if (skyboxManager.isEnabled()) {
                skyboxManager.extract(renderer, level, level.dimension().equals(Level.END) ? 0.0F : tickDelta);
            }
        });

        SkyEvents.END_SKY_AFTER.register((
                renderer
                //? >=26.3
                , pass
        ) -> {
            if (skyboxManager.isEnabled()) {
                renderer.endFrame(
                        //? >=26.3
                        pass
                );
            }
        });

        SkyEvents.SUN_MOON_STARS.register((
                renderer,
                isInNether
                //? >=26.3
                , pass
        ) ->  {
            if (skyboxManager.isEnabled()) {
                renderer.endFrame(
                        //? >=26.3
                        pass
                );
                return !isInNether;
            } else {
                return true;
            }
        });
    }
}
