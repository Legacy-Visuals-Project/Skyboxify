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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.visuals.legacy.lightconfig.lib.v1.events.EventManager;

public final class Skyboxify {
    private static final EventManager globalEventManager = new EventManager();

    public static EventManager eventManager() {
        return globalEventManager;
    }

    public static ResourceLocation locationOrNull(final String path) {
        return ResourceLocation.fromNamespaceAndPath(SkyboxifyInfo.MOD_ID, path);
    }

    public static void initialize() {
        final SkyboxifyApi impl = SkyboxifyImpl.getInstance();
        impl.getConfig().load();

        final SkyboxifyConfig config = impl.getConfig();
        ClientTickEvents.END_WORLD_TICK.register(SkyboxifyImpl.skyboxManager()::tick);

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

        globalEventManager.listen(SkyRenderEvent.SunriseSunset.After.class, event -> {
            if (SkyboxifyImpl.skyboxManager().isEnabled()) {
                event.bufferSource().endBatch(); // Fix horizon rendering over the skybox
            }
        });

        globalEventManager.listen(SkyRenderEvent.EndSky.After.class, event -> {
            if (SkyboxifyImpl.skyboxManager().isEnabled()) {
                renderSkyboxes(event.skyFeatureRenderer(), event.level(), 0.0F);
            }
        });

        globalEventManager.listen(SkyRenderEvent.SunMoonStars.class, event -> {
            final ClientLevel level = event.level();
            if (SkyboxifyImpl.skyboxManager().isEnabled()) {
                renderSkyboxes(event.skyFeatureRenderer(), level, event.tickDelta());
                if (level.dimension().equals(Level.NETHER)) {
                    event.setCancelled(true);
                }
            }
        });
    }

    private static void renderSkyboxes(final SkyFeatureRenderer skyFeatureRenderer, final ClientLevel level, final float tickDelta) {
        final Matrix4f modelViewMatrix = new Matrix4f(RenderSystem.getModelViewStack());
        modelViewMatrix.rotate(Axis.YP.rotationDegrees(-90.0F));
        for (final Skybox skybox : SkyboxifyImpl.skyboxManager().getActiveSkies()) {
            skybox.extractRenderState(skyFeatureRenderer, level, modelViewMatrix, tickDelta);
        }

        skyFeatureRenderer.endFrame();
    }
}
