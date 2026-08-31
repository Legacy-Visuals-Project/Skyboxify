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

import net.minecraft.util.Mth;

public final class ARGB {
    private ARGB() {
    }

    public static int color(final int alpha, final int rgb) {
        return alpha << 24 | rgb & 16777215;
    }

    public static int white(final float alpha) {
        return as8BitChannel(alpha) << 24 | 16777215;
    }

    public static int red(final int color) {
        return (color >> 16) & 0xFF;
    }

    public static float redFloat(final int color) {
        return as8BitChannel(red(color));
    }

    public static int green(final int color) {
        return (color >> 8) & 0xFF;
    }

    public static float greenFloat(final int color) {
        return as8BitChannel(green(color));
    }

    public static int blue(final int color) {
        return color & 0xFF;
    }

    public static float blueFloat(final int color) {
        return as8BitChannel(blue(color));
    }

    public static int alpha(final int color) {
        return (color >> 24) & 0xFF;
    }

    public static float alphaFloat(final int color) {
        return as8BitChannel(alpha(color));
    }

    public static int as8BitChannel(final float value) {
        return Mth.floor(value * 255.0F);
    }
}
