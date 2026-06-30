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
import btw.lowercase.skyboxify.skybox.impl.Skybox;
import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class SkyboxManager {
    private final List<Skybox> loadedSkies = new ArrayList<>();
    private final List<Skybox> activeSkies = new LinkedList<>();
    private final SkyboxifyApi api;

    public SkyboxManager(final SkyboxifyApi api) {
        this.api = api;
    }

    public void addSkybox(final Skybox skybox) {
        this.loadedSkies.add(Preconditions.checkNotNull(skybox, "Skybox was null"));
    }

    public void clearSkyboxes() {
        this.loadedSkies.clear();
        this.activeSkies.clear();
    }

    public void tick() {
        final ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            this.tick(level);
        }
    }

    public void tick(final ClientLevel level) {
        for (final Skybox skybox : this.loadedSkies) {
            skybox.tick(level);
        }

        this.activeSkies.removeIf(optiFineSkybox -> !optiFineSkybox.isActive());
        this.loadedSkies.stream().filter(it -> !this.activeSkies.contains(it) && it.isActive()).forEach(this.activeSkies::add);
    }

    public boolean isEnabled() {
        return this.api.getConfig().enabled && !this.activeSkies.isEmpty();
    }

    public List<Skybox> getSkiesFor(final ResourceKey<Level> resourceKey) {
        return getActiveSkies().stream().filter(skybox -> resourceKey.equals(skybox.dimension())).toList();
    }

    public boolean containsEnabled(final ResourceKey<Level> resourceKey) {
        return !getSkiesFor(resourceKey).isEmpty();
    }

    public List<Skybox> getActiveSkies() {
        return this.activeSkies;
    }

    public List<Skybox> getLoadedSkies() {
        return this.loadedSkies;
    }
}