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

package btw.lowercase.skyboxify.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import org.visuals.legacy.lightconfig.lib.v1.events.CancellableEvent;
import org.visuals.legacy.lightconfig.lib.v1.events.Event;

public class SkyRenderEvent {
	public static final class EndSky {
		@RequiredArgsConstructor
		public static class After extends Event {
			@Getter
			private final ClientLevel level;
		}
	}

	@RequiredArgsConstructor
	public static final class SunriseSunset extends CancellableEvent {
		@RequiredArgsConstructor
		public static class After extends Event {
			//? >=1.21.4 <1.21.9 {
			/*@Getter
			private final net.minecraft.client.renderer.MultiBufferSource.BufferSource bufferSource;
			*///?}
		}
	}

	@RequiredArgsConstructor
	public static final class SunMoonStars extends CancellableEvent {
		@Getter
		private final ClientLevel level;
		@Getter
		private final float tickDelta;
	}

	@RequiredArgsConstructor
	public static final class Celestial extends CancellableEvent {
		@Getter
		private final Type type;

		public enum Type {
			SUN,
			MOON,
			STARS
		}
	}
}
