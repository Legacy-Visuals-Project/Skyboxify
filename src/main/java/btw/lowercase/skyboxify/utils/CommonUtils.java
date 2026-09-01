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
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class CommonUtils {
    public static final Vector3f X_AXIS = new Vector3f(1.0F, 0.0F, 0.0F);
    public static final Vector3f Y_AXIS = new Vector3f(0.0F, 1.0F, 0.0F);
    public static final float EPSILON = 1.0E-5F;

    public static int normalizeTickTime(final int tickTime) {
        int result = tickTime % 24000;
        if (result < 0) {
            result += 24000;
        }

        return result;
    }

    public static boolean isInTimeInterval(final int currentTime, final int startTime, final int endTime) {
        if (currentTime < 0 || currentTime >= 24000) {
            return false; // Invalid time
        } else if (startTime <= endTime) {
            return currentTime >= startTime && currentTime <= endTime;
        } else {
            return currentTime >= startTime || currentTime <= endTime;
        }
    }

    public static float calculateConditionAlphaValue(final float maxAlpha, final float minAlpha, final float lastAlpha, final int duration, final boolean in) {
        if (duration == 0) {
            return lastAlpha;
        } else if (in && maxAlpha == lastAlpha) {
            return maxAlpha;
        } else if (!in && lastAlpha == minAlpha) {
            return minAlpha;
        } else {
            final float alphaChange = (maxAlpha - minAlpha) / duration;
            final float result = in ? lastAlpha + alphaChange : lastAlpha - alphaChange;
            return Math.clamp(result, minAlpha, maxAlpha);
        }
    }

    public static void rotate(final Matrix4f matrix4f, final Vector3fc axis, final float angle) {
        matrix4f.rotate(new Quaternionf().rotateAxis((float) Math.toRadians(angle), axis));
    }

    public static final int NETHER = -1;
    public static final int OVERWORLD = 0;
    public static final int END = 1;

    public static final Id NETHER_ID = Id.withDefaultNamespace("the_nether");
    public static final Id OVERWORLD_ID = Id.withDefaultNamespace("overworld");
    public static final Id END_ID = Id.withDefaultNamespace("the_end");

    public static Id dimensionIdentifier(final int dimId) {
        return switch (dimId) {
            case NETHER -> NETHER_ID;
            case OVERWORLD -> OVERWORLD_ID;
            case END -> END_ID;
            default -> Id.withDefaultNamespace("unknown");
        };
    }

    public static Id getBiomeId(final Biome biome) {
        return Id.withDefaultNamespace(biome.name.toLowerCase().replace(" ", "_")); // TODO
    }
}
