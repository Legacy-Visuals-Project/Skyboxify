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

import btw.lowercase.skyboxify.api.SkyboxifyApi;
import com.google.common.base.Preconditions;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class SkyboxManager {
	@Getter
	private final List<Skybox> loaded = new ArrayList<>();
	@Getter
	private final List<Skybox> active = new LinkedList<>();
	private final SkyboxifyApi api;

	public SkyboxManager(final SkyboxifyApi api) {
		this.api = api;
	}

	public void addSkybox(final Skybox skybox) {
		if (this.api.getConfig().enabled.isEnabled()) {
			this.loaded.add(Preconditions.checkNotNull(skybox, "Skybox was null"));
		}
	}

	public void clearSkyboxes() {
		SkyboxRenderer.INSTANCE.clearCache();
		this.loaded.clear();
		this.active.clear();
	}

	public void tick(final ClientLevel level) {
		if (this.api.getConfig().enabled.isEnabled()) {
			for (final Skybox skybox : this.loaded) {
				skybox.tick(level);
			}

			this.active.removeIf(optiFineSkybox -> !optiFineSkybox.isActive());
			this.loaded.stream().filter(it -> !this.active.contains(it) && it.isActive()).forEach(this.active::add);
		}
	}

	public boolean isEnabled() {
		return this.api.getConfig().enabled.isEnabled() && !this.active.isEmpty();
	}

	public List<Skybox> getSkiesFor(final ResourceKey<@NotNull Level> resourceKey) {
		return getActive().stream().filter(skybox -> resourceKey.equals(skybox.getDimension())).toList();
	}

	public boolean containsEnabled(final ResourceKey<@NotNull Level> resourceKey) {
		return !getSkiesFor(resourceKey).isEmpty();
	}
}