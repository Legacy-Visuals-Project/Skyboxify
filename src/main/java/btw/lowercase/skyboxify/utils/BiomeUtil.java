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

package btw.lowercase.skyboxify.utils;

import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class BiomeUtil {
    private BiomeUtil() {
    }

    private static final Map<Integer, Id> LEGACY_ID_MAP = new HashMap<>();
    private static final Map<String, Integer> LEGACY_OPTIFINE_MAP = new HashMap<>();

    static {
        // Biome Raw ID -> NamespacedIdentifier
        LEGACY_ID_MAP.put(0, Id.withDefaultNamespace("ocean"));
        LEGACY_ID_MAP.put(1, Id.withDefaultNamespace("plains"));
        LEGACY_ID_MAP.put(2, Id.withDefaultNamespace("desert"));
        LEGACY_ID_MAP.put(3, Id.withDefaultNamespace("extreme_hills"));
        LEGACY_ID_MAP.put(4, Id.withDefaultNamespace("forest"));
        LEGACY_ID_MAP.put(5, Id.withDefaultNamespace("taiga"));
        LEGACY_ID_MAP.put(6, Id.withDefaultNamespace("swampland"));
        LEGACY_ID_MAP.put(7, Id.withDefaultNamespace("river"));
        LEGACY_ID_MAP.put(8, Id.withDefaultNamespace("hell"));
        LEGACY_ID_MAP.put(9, Id.withDefaultNamespace("the_end"));
        LEGACY_ID_MAP.put(10, Id.withDefaultNamespace("frozen_ocean"));
        LEGACY_ID_MAP.put(11, Id.withDefaultNamespace("frozen_river"));
        LEGACY_ID_MAP.put(12, Id.withDefaultNamespace("ice_plains"));
        LEGACY_ID_MAP.put(13, Id.withDefaultNamespace("ice_mountains"));
        LEGACY_ID_MAP.put(14, Id.withDefaultNamespace("mushroom_island"));
        LEGACY_ID_MAP.put(15, Id.withDefaultNamespace("mushroom_island_shore"));
        LEGACY_ID_MAP.put(16, Id.withDefaultNamespace("beach"));
        LEGACY_ID_MAP.put(17, Id.withDefaultNamespace("desert_hills"));
        LEGACY_ID_MAP.put(18, Id.withDefaultNamespace("forest_hills"));
        LEGACY_ID_MAP.put(19, Id.withDefaultNamespace("taiga_hills"));
        LEGACY_ID_MAP.put(20, Id.withDefaultNamespace("extreme_hills_edge"));
        LEGACY_ID_MAP.put(21, Id.withDefaultNamespace("jungle"));
        LEGACY_ID_MAP.put(22, Id.withDefaultNamespace("jungle_hills"));
        LEGACY_ID_MAP.put(23, Id.withDefaultNamespace("jungle_edge"));
        LEGACY_ID_MAP.put(24, Id.withDefaultNamespace("deep_ocean"));
        LEGACY_ID_MAP.put(25, Id.withDefaultNamespace("stone_beach"));
        LEGACY_ID_MAP.put(26, Id.withDefaultNamespace("cold_beach"));
        LEGACY_ID_MAP.put(27, Id.withDefaultNamespace("birch_forest"));
        LEGACY_ID_MAP.put(28, Id.withDefaultNamespace("birch_forest_hills"));
        LEGACY_ID_MAP.put(29, Id.withDefaultNamespace("roofed_forest"));
        LEGACY_ID_MAP.put(30, Id.withDefaultNamespace("cold_taiga"));
        LEGACY_ID_MAP.put(31, Id.withDefaultNamespace("cold_taiga_hills"));
        LEGACY_ID_MAP.put(32, Id.withDefaultNamespace("mega_taiga"));
        LEGACY_ID_MAP.put(33, Id.withDefaultNamespace("mega_taiga_hills"));
        LEGACY_ID_MAP.put(34, Id.withDefaultNamespace("extreme_hills_plus"));
        LEGACY_ID_MAP.put(35, Id.withDefaultNamespace("savanna"));
        LEGACY_ID_MAP.put(36, Id.withDefaultNamespace("savanna_plateau"));
        LEGACY_ID_MAP.put(37, Id.withDefaultNamespace("mesa"));
        LEGACY_ID_MAP.put(38, Id.withDefaultNamespace("mesa_plateau_f"));
        LEGACY_ID_MAP.put(39, Id.withDefaultNamespace("mesa_plateau"));

        // Legacy OptiFine Name -> Biome Raw ID
        // TODO
    }

    public static @Nullable Id legacyId(final Biome biome) {
        return LEGACY_ID_MAP.getOrDefault(biome.id, null);
    }

    public static int fromLegacyOptiFine(final String name) {
        return LEGACY_OPTIFINE_MAP.getOrDefault(name, -1);
    }
}
