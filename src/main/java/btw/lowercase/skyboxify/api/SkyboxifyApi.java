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

public interface SkyboxifyApi {
	SkyboxifyConfig getConfig();

	SkyboxManager getSkyboxManager();

	Identifier getModernDimension(final int legacyId);

	void registerDimensionMapping(final int legacyId, final Identifier modernId);

	default int getApiVersion() {
		return 0;
	}
}
