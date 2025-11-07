package btw.lowercase.optiboxes.skybox.components;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum Weather implements StringRepresentable {
    CLEAR,
    RAIN,
    THUNDER;

    public static final Codec<Weather> CODEC = StringRepresentable.fromEnum(Weather::values);

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase();
    }
}
