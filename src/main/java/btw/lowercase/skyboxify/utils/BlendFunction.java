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

import org.lwjgl.opengl.GL11;

public record BlendFunction(SrcFactor srcFactor, DstFactor dstFactor) {
    public enum SrcFactor {
        ZERO,
        ONE,
        SRC_ALPHA,
        DST_COLOR,
        ONE_MINUS_DST_COLOR;

        public int vanilla() {
            return switch (this) {
                case ZERO -> GL11.GL_ZERO;
                case ONE -> GL11.GL_ONE;
                case SRC_ALPHA -> GL11.GL_SRC_ALPHA;
                case DST_COLOR -> GL11.GL_DST_COLOR;
                case ONE_MINUS_DST_COLOR -> GL11.GL_ONE_MINUS_DST_COLOR;
            };
        }
    }

    public enum DstFactor {
        ZERO,
        ONE,
        SRC_COLOR,
        ONE_MINUS_SRC_COLOR,
        ONE_MINUS_SRC_ALPHA;

        public int vanilla() {
            return switch (this) {
                case ZERO -> GL11.GL_ZERO;
                case ONE -> GL11.GL_ONE;
                case SRC_COLOR -> GL11.GL_SRC_COLOR;
                case ONE_MINUS_SRC_COLOR -> GL11.GL_ONE_MINUS_SRC_COLOR;
                case ONE_MINUS_SRC_ALPHA -> GL11.GL_ONE_MINUS_SRC_ALPHA;
            };
        }
    }
}
