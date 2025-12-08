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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class SkyboxManager {
    public static final SkyboxManager INSTANCE = new SkyboxManager();
    private final List<OptiFineSkybox> loadedSkyboxes = new ArrayList<>();
    @Getter
    private final List<OptiFineSkybox> activeSkyboxes = new LinkedList<>();

    private SkyboxManager() {
    }

    public void addSkybox(OptiFineSkybox optiFineSkybox) {
        Preconditions.checkNotNull(optiFineSkybox, "Skybox was null");
        this.loadedSkyboxes.add(optiFineSkybox);
    }

    public void clearSkyboxes() {
        Minecraft.getInstance().execute(OptiFineSkyRenderer.INSTANCE::clearCache);
        this.loadedSkyboxes.clear();
        this.activeSkyboxes.clear();
    }

    public void tick(ClientLevel level) {
        if (!Skyboxify.getConfig().enabled.isEnabled()) {
            return;
        }

        for (OptiFineSkybox optiFineSkybox : this.loadedSkyboxes) {
            optiFineSkybox.tick(level);
        }

        this.activeSkyboxes.removeIf(optiFineSkybox -> !optiFineSkybox.isActive());
        for (OptiFineSkybox optiFineSkybox : this.loadedSkyboxes) {
            if (!this.activeSkyboxes.contains(optiFineSkybox) && optiFineSkybox.isActive()) {
                this.activeSkyboxes.add(optiFineSkybox);
            }
        }
    }

    public boolean isEnabled(Level level) {
        return Skyboxify.getConfig().enabled.isEnabled() && !activeSkyboxes.isEmpty() && level != null;
    }

    public List<OptiFineSkybox> getSkiesFor(ResourceKey<@NotNull Level> resourceKey) {
        return getActiveSkyboxes().stream().filter(skybox -> resourceKey.equals(skybox.getWorldResourceKey())).toList();
    }

    public boolean containsEnabled(ResourceKey<@NotNull Level> resourceKey) {
        return !getSkiesFor(resourceKey).isEmpty();
    }
}