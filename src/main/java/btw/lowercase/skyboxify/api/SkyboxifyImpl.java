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
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class SkyboxifyImpl implements SkyboxifyApi {
	private static final SkyboxifyImpl INSTANCE = new SkyboxifyImpl();

	private final SkyboxifyConfig config = new SkyboxifyConfig();
	private final SkyboxManager skyboxManager = new SkyboxManager(this);
	private final Map<Integer, Identifier> dimensionMapping = new HashMap<>();

	private SkyboxifyImpl() {
		this.registerDimensionMapping(-1, Identifier.withDefaultNamespace("the_nether"));
		this.registerDimensionMapping(0, Identifier.withDefaultNamespace("overworld"));
		this.registerDimensionMapping(1, Identifier.withDefaultNamespace("the_end"));
		this.registerDimensionMapping(4, Identifier.fromNamespaceAndPath("aether", "the_aether"));
		this.registerDimensionMapping(7, Identifier.fromNamespaceAndPath("twilightforest", "twilight_forest"));
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

	public static SkyboxManager skyboxManager() {
		return getInstance().getSkyboxManager();
	}

	@Override
	public SkyboxManager getSkyboxManager() {
		return this.skyboxManager;
	}

	@Override
	public Identifier getModernDimension(final int legacyId) {
		return this.dimensionMapping.getOrDefault(legacyId, null);
	}

	@Override
	public void registerDimensionMapping(final int legacyId, final Identifier modernId) {
		if (this.dimensionMapping.containsKey(legacyId)) {
			throw new IllegalArgumentException("Cannot register dimension mapping, world with legacy id " + legacyId + " is already taken by \"" + dimensionMapping.get(legacyId) + "\"!");
		}

		if (this.dimensionMapping.containsValue(modernId)) {
			int currentId = 0;
			for (final int key : this.dimensionMapping.keySet()) {
				if (Objects.equals(this.dimensionMapping.get(key), modernId)) {
					currentId = key;
				}
			}

			throw new IllegalArgumentException("Cannot register dimension mapping, world \"" + modernId + "\" is already mapped to legacy id " + currentId);
		}

		this.dimensionMapping.put(legacyId, modernId);
	}
}
