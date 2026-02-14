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

import btw.lowercase.skyboxify.skybox.components.UV;
import lombok.Getter;
import org.joml.Matrix4f;

public enum SkyPart {
    BOTTOM(
            new Matrix4f().rotateY((float) Math.toRadians(90.0F)),
            new UV(0.0F, 0.0F, 0.33333334F, 0.5F)
    ),
    TOP(
            new Matrix4f().rotateX((float) Math.toRadians(180.0F)).rotateY((float) Math.toRadians(-90.0F)),
            new UV(0.33333334F, 0.0F, 0.6666667F, 0.5F)
    ),
    EAST(
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(90.0F)),
            new UV(0.6666667F, 0.0F, 1.0F, 0.5F)
    ),
    SOUTH(
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(180.0F)),
            new UV(0.0F, 0.5F, 0.33333334F, 1.0F)
    ),
    WEST(
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(-90.0F)),
            new UV(0.33333334F, 0.5F, 0.6666667F, 1.0F)
    ),
    NORTH(
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)),
            new UV(0.6666667F, 0.5F, 1.0F, 1.0F)
    );

    public static final SkyPart[] VALUES = values();
    public static final int COUNT = VALUES.length;

    @Getter
	private final Matrix4f rotationMatrix;
	@Getter
    private final UV uv;

    SkyPart(final Matrix4f rotationMatrix, final UV uv) {
        this.rotationMatrix = rotationMatrix;
        this.uv = uv;
    }
}
