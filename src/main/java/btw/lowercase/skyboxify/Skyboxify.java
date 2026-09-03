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
import btw.lowercase.skyboxify.events.SkyRenderEvent;
import btw.lowercase.skyboxify.skybox.impl.Skybox;
import btw.lowercase.skyboxify.skybox.renderer.SkyFeatureRenderer;
import btw.lowercase.skyboxify.utils.CommonUtils;
import btw.lowercase.skyboxify.utils.ShaderUtil;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.world.ClientWorld;
import net.ornithemc.osl.lifecycle.api.client.ClientWorldEvents;
import org.joml.Matrix4f;
import org.visuals.legacy.lightconfig.lib.v1.events.EventManager;

public final class Skyboxify {
    private static final EventManager globalEventManager = new EventManager();

    public static EventManager eventManager() {
        return globalEventManager;
    }

    public static void initialize() {
        final SkyboxifyApi impl = SkyboxifyImpl.getInstance();
        final SkyboxifyConfig config = impl.getConfig();
        config.load();

        ClientWorldEvents.TICK_END.register(impl.getSkyboxManager()::tick);

        globalEventManager.listen(SkyRenderEvent.Disc.class, event -> {
            if (config.enabled.isEnabled() && !config.renderSky.isEnabled()) {
                event.setCancelled(true);
            }
        });

        globalEventManager.listen(SkyRenderEvent.Celestial.class, event -> {
            if (config.enabled.isEnabled()) {
                final SkyRenderEvent.Celestial.Type type = event.getType();
                if (!config.renderSunMoon.isEnabled() && (type == SkyRenderEvent.Celestial.Type.SUN || type == SkyRenderEvent.Celestial.Type.MOON)) {
                    event.setCancelled(true);
                }

                if (SkyboxifyImpl.skyboxManager().isEnabled() && type == SkyRenderEvent.Celestial.Type.STARS) {
                    if (config.renderStars.isEnabled()) {
                        return;
                    }

                    event.setCancelled(true);
                }
            }
        });

        globalEventManager.listen(SkyRenderEvent.EndSky.After.class, event -> {
            if (SkyboxifyImpl.skyboxManager().isEnabled()) {
                renderSkyboxes(event.skyFeatureRenderer(), event.level(), 0.0F);

                // Restore
                GlStateManager.depthMask(true);
            }
        });

        globalEventManager.listen(SkyRenderEvent.SunMoonStars.class, event -> {
            final ClientWorld level = event.level();
            if (SkyboxifyImpl.skyboxManager().isEnabled()) {
                renderSkyboxes(event.skyFeatureRenderer(), level, event.tickDelta());
                if (level.dimension.getId() == CommonUtils.NETHER) {
                    event.setCancelled(true);
                }

                // Restore
                GlStateManager.enableBlend();
                GlStateManager.depthMask(false);
            }
        });
    }

    private static void renderSkyboxes(final SkyFeatureRenderer skyFeatureRenderer, final ClientWorld level, final float tickDelta) {
        final Matrix4f modelViewMatrix = new Matrix4f(ShaderUtil.captureModelView());
        CommonUtils.rotate(modelViewMatrix, CommonUtils.Y_AXIS, -90.0F);
        for (final Skybox skybox : SkyboxifyImpl.skyboxManager().getActiveSkies()) {
            skybox.extractRenderState(skyFeatureRenderer, level, modelViewMatrix, tickDelta);
        }

        skyFeatureRenderer.endFrame();
    }
}
