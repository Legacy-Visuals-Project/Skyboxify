package btw.lowercase.optiboxes.utils;

// NOTE: Enum values fetched from Iris mod
public enum IrisPipeline {
    BASIC,
    TEXTURED,
    TERRAIN,
    TERRAIN_SOLID,
    TERRAIN_CUTOUT,
    TRANSLUCENT,
    SKY_BASIC,
    SKY_TEXTURED,
    ARMOR_GLINT,
    ENTITIES,
    ENTITIES_TRANSLUCENT,
    CLOUDS,
    BLOCK,
    BLOCK_TRANSLUCENT,
    HAND,
    HAND_TRANSLUCENT,
    PARTICLES,
    PARTICLES_TRANSLUCENT,
    EMISSIVE_ENTITIES,
    BEACON_BEAM,
    LINES;

    public static final IrisPipeline[] VALUES = values();

    private Enum<?> value = null;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void initialize(Class<? extends Enum> clazz) {
        this.value = Enum.valueOf(clazz, this.name());
    }

    public Enum<?> internal() {
        return this.value;
    }
}
