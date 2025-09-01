package btw.lowercase.optiboxes.utils;

public enum SkyPart {
    BOTTOM,
    TOP,
    EAST,
    SOUTH,
    WEST,
    NORTH;

    public static final SkyPart[] VALUES = values();
    public static final int COUNT = VALUES.length;

    public static SkyPart of(int ordinal) {
        if (ordinal < 0) {
            throw new RuntimeException("Face not found! Ordinal is below 0 which is not allowed!");
        } else if (ordinal > COUNT) {
            throw new RuntimeException("Face not found! Ordinal is above the count of faces!");
        } else {
            return VALUES[ordinal];
        }
    }
}
