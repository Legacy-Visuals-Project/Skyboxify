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

import btw.lowercase.skyboxify.skybox.renderer.SkyExtractor;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.multiplayer.ClientLevel;

//? >=26.3
import com.mojang.renderpearl.api.commands.RenderPass;

public final class SkyEvents {
    public static final Event<Extraction> EXTRACTION = EventFactory.createArrayBacked(Extraction.class, callbacks -> (renderer, client, tickDelta) -> {
        for (final Extraction extraction : callbacks) {
            extraction.accept(renderer, client, tickDelta);
        }
    });

    @FunctionalInterface
    public interface Extraction {
        void accept(final SkyExtractor renderer, final ClientLevel level, final float tickDelta);
    }

    public static final Event<EndSky> END_SKY_AFTER = EventFactory.createArrayBacked(EndSky.class, callbacks -> (
            renderer
            //? >=26.3
            , pass
    ) -> {
        for (final EndSky endSky : callbacks) {
            endSky.accept(
                    renderer
                    //? >=26.3
                    , pass
            );
        }
    });

    @FunctionalInterface
    public interface EndSky {
        void accept(
                final SkyExtractor renderer
                //? >=26.3
                , final RenderPass pass
        );
    }

    //? >=1.21.4 <1.21.9 {
    /*public static final Event<SunriseSunsetAfter> SUNRISE_SUNSET_AFTER = EventFactory.createArrayBacked(SunriseSunsetAfter.class, callbacks -> (bufferSource) -> {
        for (final SunriseSunsetAfter sunriseSunsetAfter : callbacks) {
            sunriseSunsetAfter.accept(bufferSource);
        }
    });

    @FunctionalInterface
    public interface SunriseSunsetAfter {
        void accept(final net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource);
    }*///? }

    public static final Event<SunMoonStars> SUN_MOON_STARS = EventFactory.createArrayBacked(SunMoonStars.class, callbacks -> (
            renderer,
            client
            //? >=26.3
            , pass
    ) -> {
        for (final SunMoonStars sunMoonStars : callbacks) {
            if (!sunMoonStars.accept(
                    renderer,
                    client
                    //? >=26.3
                    , pass
            )) {
                return false;
            }
        }

        return true;
    });

    @FunctionalInterface
    public interface SunMoonStars {
        boolean accept(
                final SkyExtractor renderer,
                final boolean isInNether
                //? >=26.3
                , final RenderPass pass
        );
    }

    public static final Event<Celestial> CELESTIAL = EventFactory.createArrayBacked(Celestial.class, callbacks -> (type) -> {
        for (final Celestial celestial : callbacks) {
            if (!celestial.accept(type)) {
                return false;
            }
        }

        return true;
    });

    @FunctionalInterface
    public interface Celestial {
        boolean accept(final CelestialType type);
    }

    public enum CelestialType {
        SUN,
        MOON,
        STARS
    }

    public static final Event<SkyDisc> SKY_DISC = EventFactory.createArrayBacked(SkyDisc.class, callbacks -> (type) -> {
        for (final SkyDisc skyDisc : callbacks) {
            if (!skyDisc.accept(type)) {
                return false;
            }
        }

        return true;
    });

    @FunctionalInterface
    public interface SkyDisc {
        boolean accept(final DiscType type);
    }

    public enum DiscType {
        TOP,
        VOID,
        BOTTOM
    }
}
