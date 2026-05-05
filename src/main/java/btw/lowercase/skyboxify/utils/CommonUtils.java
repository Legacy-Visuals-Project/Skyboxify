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

import lombok.experimental.UtilityClass;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

import java.text.DecimalFormat;

@UtilityClass
public final class CommonUtils {
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
            return Mth.clamp(result, minAlpha, maxAlpha);
        }
    }

    // This method replicates the old Mojang-made Quaternion mulPose method, which was
    // used for the initial sky transformations which did not have any bobbing issues.
    // See https://github.com/sp614x/optifine/issues/7235#issuecomment-1581930719
    public static void mulPose(final Matrix4f srcMatrix, final float angle, final Vector3fc axis) {
        // Create Rotation Matrix
        final Matrix4f rotationMatrix = Quaternion.rotation(axis, angle);
        // Multiply
        final float f = srcMatrix.m00() * rotationMatrix.m00() + srcMatrix.m01() * rotationMatrix.m10() + srcMatrix.m02() * rotationMatrix.m20() + srcMatrix.m03() * rotationMatrix.m30();
        final float g = srcMatrix.m00() * rotationMatrix.m01() + srcMatrix.m01() * rotationMatrix.m11() + srcMatrix.m02() * rotationMatrix.m21() + srcMatrix.m03() * rotationMatrix.m31();
        final float h = srcMatrix.m00() * rotationMatrix.m02() + srcMatrix.m01() * rotationMatrix.m12() + srcMatrix.m02() * rotationMatrix.m22() + srcMatrix.m03() * rotationMatrix.m32();
        final float i = srcMatrix.m00() * rotationMatrix.m03() + srcMatrix.m01() * rotationMatrix.m13() + srcMatrix.m02() * rotationMatrix.m23() + srcMatrix.m03() * rotationMatrix.m33();
        final float j = srcMatrix.m10() * rotationMatrix.m00() + srcMatrix.m11() * rotationMatrix.m10() + srcMatrix.m12() * rotationMatrix.m20() + srcMatrix.m13() * rotationMatrix.m30();
        final float k = srcMatrix.m10() * rotationMatrix.m01() + srcMatrix.m11() * rotationMatrix.m11() + srcMatrix.m12() * rotationMatrix.m21() + srcMatrix.m13() * rotationMatrix.m31();
        final float l = srcMatrix.m10() * rotationMatrix.m02() + srcMatrix.m11() * rotationMatrix.m12() + srcMatrix.m12() * rotationMatrix.m22() + srcMatrix.m13() * rotationMatrix.m32();
        final float m = srcMatrix.m10() * rotationMatrix.m03() + srcMatrix.m11() * rotationMatrix.m13() + srcMatrix.m12() * rotationMatrix.m23() + srcMatrix.m13() * rotationMatrix.m33();
        final float n = srcMatrix.m20() * rotationMatrix.m00() + srcMatrix.m21() * rotationMatrix.m10() + srcMatrix.m22() * rotationMatrix.m20() + srcMatrix.m23() * rotationMatrix.m30();
        final float o = srcMatrix.m20() * rotationMatrix.m01() + srcMatrix.m21() * rotationMatrix.m11() + srcMatrix.m22() * rotationMatrix.m21() + srcMatrix.m23() * rotationMatrix.m31();
        final float p = srcMatrix.m20() * rotationMatrix.m02() + srcMatrix.m21() * rotationMatrix.m12() + srcMatrix.m22() * rotationMatrix.m22() + srcMatrix.m23() * rotationMatrix.m32();
        final float q = srcMatrix.m20() * rotationMatrix.m03() + srcMatrix.m21() * rotationMatrix.m13() + srcMatrix.m22() * rotationMatrix.m23() + srcMatrix.m23() * rotationMatrix.m33();
        final float r = srcMatrix.m30() * rotationMatrix.m00() + srcMatrix.m31() * rotationMatrix.m10() + srcMatrix.m32() * rotationMatrix.m20() + srcMatrix.m33() * rotationMatrix.m30();
        final float s = srcMatrix.m30() * rotationMatrix.m01() + srcMatrix.m31() * rotationMatrix.m11() + srcMatrix.m32() * rotationMatrix.m21() + srcMatrix.m33() * rotationMatrix.m31();
        final float t = srcMatrix.m30() * rotationMatrix.m02() + srcMatrix.m31() * rotationMatrix.m12() + srcMatrix.m32() * rotationMatrix.m22() + srcMatrix.m33() * rotationMatrix.m32();
        final float u = srcMatrix.m30() * rotationMatrix.m03() + srcMatrix.m31() * rotationMatrix.m13() + srcMatrix.m32() * rotationMatrix.m23() + srcMatrix.m33() * rotationMatrix.m33();
        srcMatrix.m00(f).m01(g).m02(h).m03(i).m10(j).m11(k).m12(l).m13(m).m20(n).m21(o).m22(p).m23(q).m30(r).m31(s).m32(t).m33(u);
    }

    private static final DecimalFormat VECTOR_FORMAT = new DecimalFormat("# .##");

    public static String vectorToString(final Vector3fc vector3fc) {
        final String x = VECTOR_FORMAT.format(vector3fc.x()).trim();
        final String y = VECTOR_FORMAT.format(vector3fc.y()).trim();
        final String z = VECTOR_FORMAT.format(vector3fc.z()).trim();
        return String.format("[x=%s, y=%s, z=%s]", x, y, z);
    }

    public static Vector4f unpackARGB(final int color) {
        return new Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color));
    }

    public static int packARGB(final float red, final float green, final float blue, final float alpha) {
        return ARGB.colorFromFloat(alpha, red, green, blue);
    }
}
