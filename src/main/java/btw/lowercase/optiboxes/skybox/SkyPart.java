package btw.lowercase.optiboxes.skybox;

import btw.lowercase.optiboxes.skybox.components.UVRange;
import org.joml.Matrix4f;

public enum SkyPart {
    BOTTOM(
            new Matrix4f().rotateY((float) Math.toRadians(90.0F)),
            new UVRange(0.0F, 0.0F, 0.33333334F, 0.5F)
    ),
    TOP(
            new Matrix4f().rotateX((float) Math.toRadians(180.0F)).rotateY((float) Math.toRadians(-90.0F)),
            new UVRange(0.33333334F, 0.0F, 0.6666667F, 0.5F)
    ),
    EAST(
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(90.0F)),
            new UVRange(0.6666667F, 0.0F, 1.0F, 0.5F)
    ),
    SOUTH(
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(180.0F)),
            new UVRange(0.0F, 0.5F, 0.33333334F, 1.0F)
    ),
    WEST(
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(-90.0F)),
            new UVRange(0.33333334F, 0.5F, 0.6666667F, 1.0F)
    ),
    NORTH(
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)),
            new UVRange(0.6666667F, 0.5F, 1.0F, 1.0F)
    );

    public static final SkyPart[] VALUES = values();
    public static final int COUNT = VALUES.length;

    private final Matrix4f rotationMatrix;
    private final UVRange uvRange;

    SkyPart(Matrix4f rotationMatrix, UVRange uvRange) {
        this.rotationMatrix = rotationMatrix;
        this.uvRange = uvRange;
    }

    public Matrix4f getRotationMatrix() {
        return this.rotationMatrix;
    }

    public UVRange getUVRange() {
        return uvRange;
    }
}
