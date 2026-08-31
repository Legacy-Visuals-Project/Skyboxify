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

package btw.lowercase.skyboxify.events;

import btw.lowercase.skyboxify.skybox.renderer.SkyFeatureRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import org.visuals.legacy.lightconfig.lib.v1.events.CancellableEvent;
import org.visuals.legacy.lightconfig.lib.v1.events.Event;

public class SkyRenderEvent {
    public static final class EndSky {
        public record After(SkyFeatureRenderer skyFeatureRenderer, ClientLevel level) implements Event {
        }
    }

    public static final class SunriseSunset extends CancellableEvent {
        public record After(MultiBufferSource.BufferSource bufferSource) implements Event {
        }
    }

    public static final class SunMoonStars extends CancellableEvent {
        private final SkyFeatureRenderer skyFeatureRenderer;
        private final ClientLevel level;
        private final float tickDelta;

        public SunMoonStars(final SkyFeatureRenderer skyFeatureRenderer, final ClientLevel level, final float tickDelta) {
            this.skyFeatureRenderer = skyFeatureRenderer;
            this.level = level;
            this.tickDelta = tickDelta;
        }

        public SkyFeatureRenderer skyFeatureRenderer() {
            return this.skyFeatureRenderer;
        }

        public ClientLevel level() {
            return this.level;
        }

        public float tickDelta() {
            return this.tickDelta;
        }
    }

    public static final class Celestial extends CancellableEvent {
        private final Type type;

        public Celestial(final Type type) {
            this.type = type;
        }

        public Type getType() {
            return this.type;
        }

        public enum Type {
            SUN,
            MOON,
            STARS
        }
    }

    public static Celestial sun() {
        return new Celestial(Celestial.Type.SUN);
    }

    public static Celestial moon() {
        return new Celestial(Celestial.Type.MOON);
    }

    public static Celestial stars() {
        return new Celestial(Celestial.Type.STARS);
    }

    public static final class Disc extends CancellableEvent {
        private final Type type;

        public Disc(final Type type) {
            this.type = type;
        }

        public Type getType() {
            return this.type;
        }

        public enum Type {
            TOP,
            BOTTOM
        }
    }

    public static Disc topDisc() {
        return new Disc(Disc.Type.TOP);
    }

    public static Disc bottomDisc() {
        return new Disc(Disc.Type.BOTTOM);
    }
}
