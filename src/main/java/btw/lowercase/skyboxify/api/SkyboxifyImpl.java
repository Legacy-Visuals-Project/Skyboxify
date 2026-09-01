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

package btw.lowercase.skyboxify.api;

import btw.lowercase.skyboxify.config.SkyboxifyConfig;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import btw.lowercase.skyboxify.utils.Id;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.gui.screen.Screen;

import java.util.Map;
import java.util.Objects;

public final class SkyboxifyImpl implements SkyboxifyApi {
    private static final SkyboxifyImpl INSTANCE = new SkyboxifyImpl();

    private final SkyboxManager skyboxManager = new SkyboxManager(this);
    private final Map<Integer, Id> dimensionMapping = new Int2ObjectArrayMap<>();
    private final SkyboxifyConfig config = new SkyboxifyConfig();

    private SkyboxifyImpl() {
        this.registerDimensionMapping(-1, Id.withDefaultNamespace("the_nether"));
        this.registerDimensionMapping(0, Id.withDefaultNamespace("overworld"));
        this.registerDimensionMapping(1, Id.withDefaultNamespace("the_end"));
        this.registerDimensionMapping(4, Id.fromNamespaceAndPath("aether", "the_aether"));
        this.registerDimensionMapping(7, Id.fromNamespaceAndPath("twilightforest", "twilight_forest"));
    }

    public static SkyboxifyApi getInstance() {
        return INSTANCE;
    }

    public static SkyboxifyConfig config() {
        return getInstance().getConfig();
    }

    @Override
    public SkyboxifyConfig getConfig() {
        return this.config;
    }

    @Override
    public Screen getConfigScreen(final Screen parent) {
        return this.config.getConfigScreen(parent);
    }

    public static SkyboxManager skyboxManager() {
        return getInstance().getSkyboxManager();
    }

    @Override
    public SkyboxManager getSkyboxManager() {
        return this.skyboxManager;
    }

    @Override
    public Id getModernDimension(final int legacyId) {
        return this.dimensionMapping.getOrDefault(legacyId, null);
    }

    @Override
    public void registerDimensionMapping(final int legacyId, final Id modernId) {
        if (this.dimensionMapping.containsKey(legacyId)) {
            throw new IllegalArgumentException("Cannot register dimension mapping, world with legacy properties " + legacyId + " is already taken by \"" + dimensionMapping.get(legacyId) + "\"!");
        }

        if (this.dimensionMapping.containsValue(modernId)) {
            int currentId = 0;
            for (final int key : this.dimensionMapping.keySet()) {
                if (Objects.equals(this.dimensionMapping.get(key), modernId)) {
                    currentId = key;
                    break;
                }
            }

            throw new IllegalArgumentException("Cannot register dimension mapping, world \"" + modernId + "\" is already mapped to legacy properties " + currentId);
        }

        this.dimensionMapping.put(legacyId, modernId);
    }
}
