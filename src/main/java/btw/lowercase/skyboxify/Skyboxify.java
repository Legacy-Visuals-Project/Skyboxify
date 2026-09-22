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
import btw.lowercase.skyboxify.events.SkyEvents;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import btw.lowercase.skyboxify.utils.CommonUtils;
import btw.lowercase.skyboxify.utils.Id;
import net.minecraft.client.render.platform.GlStateManager;
import net.ornithemc.osl.lifecycle.api.client.ClientWorldEvents;
import org.visuals.legacy.lightconfig.lib.v1.events.EventManager;

public final class Skyboxify {
    private static final EventManager globalEventManager = new EventManager();

    public static EventManager eventManager() {
        return globalEventManager;
    }

    public static Id locationOrNull(final String path) {
        return Id.fromNamespaceAndPath(SkyboxifyInfo.MOD_ID, path);
    }

    public static void initialize() {
        final SkyboxifyApi impl = SkyboxifyImpl.getInstance();
        impl.getConfig().load();

        final SkyboxManager skyboxManager = impl.getSkyboxManager();
        final SkyboxifyConfig config = impl.getConfig();
        ClientWorldEvents.TICK_END.register(skyboxManager::tick);

        globalEventManager.listen(SkyEvents.Disc.class, event -> {
            if (config.enabled.isEnabled() && !config.renderSky.isEnabled()) {
                event.setCancelled(true);
            }
        });

        globalEventManager.listen(SkyEvents.Celestial.class, event -> {
            if (config.enabled.isEnabled()) {
                final SkyEvents.Celestial.Type type = event.getType();
                if (!config.renderSunMoon.isEnabled() && (type == SkyEvents.Celestial.Type.SUN || type == SkyEvents.Celestial.Type.MOON)) {
                    event.setCancelled(true);
                }

                if (skyboxManager.isEnabled() && type == SkyEvents.Celestial.Type.STARS) {
                    if (config.renderStars.isEnabled()) {
                        return;
                    }

                    event.setCancelled(true);
                }
            }
        });

        globalEventManager.listen(SkyEvents.Extraction.class, event -> {
            if (skyboxManager.isEnabled()) {
                final float delta = event.world().dimension.getId() == CommonUtils.END ? 0.0F : event.tickDelta();
                skyboxManager.extractSkyboxes(event.skyFeatureRenderer(), event.world(), delta);
            }
        });

        globalEventManager.listen(SkyEvents.EndSky.After.class, event -> {
            if (skyboxManager.isEnabled()) {
                event.skyFeatureRenderer().endFrame();

                // Restore
                GlStateManager.depthMask(true);
            }
        });

        globalEventManager.listen(SkyEvents.SunMoonStars.class, event -> {
            if (impl.getSkyboxManager().isEnabled()) {
                event.skyFeatureRenderer().endFrame();
                if (event.isInNether()) {
                    event.setCancelled(true);
                }

                // Restore
                GlStateManager.enableBlend();
                GlStateManager.depthMask(false);
            }
        });
    }
}
