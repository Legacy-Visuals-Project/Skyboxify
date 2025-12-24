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

package btw.lowercase.skyboxify.skybox;

import btw.lowercase.skyboxify.Skyboxify;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@UtilityClass
public final class SkyboxManager {
	@Getter
	private final List<Skybox> loadedSkyboxes = new ArrayList<>();
	@Getter
	private final List<Skybox> activeSkyboxes = new LinkedList<>();

	public void addSkybox(final Skybox skybox) {
		Preconditions.checkNotNull(skybox, "Skybox was null");
		loadedSkyboxes.add(skybox);
	}

	public void clearSkyboxes() {
		Minecraft.getInstance().execute(SkyboxSkyRenderer.INSTANCE::clearCache);
		loadedSkyboxes.clear();
		activeSkyboxes.clear();
	}

	public void tick(final ClientLevel level) {
		if (Skyboxify.getConfig().enabled.isEnabled()) {
			for (Skybox skybox : loadedSkyboxes) {
				skybox.tick(level);
			}

			activeSkyboxes.removeIf(optiFineSkybox -> !optiFineSkybox.isActive());
			for (Skybox skybox : loadedSkyboxes) {
				if (!activeSkyboxes.contains(skybox) && skybox.isActive()) {
					activeSkyboxes.add(skybox);
				}
			}
		}
	}

	public boolean isEnabled(final Level level) {
		return Skyboxify.getConfig().enabled.isEnabled() && !activeSkyboxes.isEmpty() && level != null;
	}

	public List<Skybox> getSkiesFor(final ResourceKey<@NotNull Level> resourceKey) {
		return getActiveSkyboxes().stream().filter(skybox -> resourceKey.equals(skybox.getWorldKey())).toList();
	}

	public boolean containsEnabled(final ResourceKey<@NotNull Level> resourceKey) {
		return !getSkiesFor(resourceKey).isEmpty();
	}
}