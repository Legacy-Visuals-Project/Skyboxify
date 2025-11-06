package btw.lowercase.optiboxes.skybox.components;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record Loop(int days, List<Range> ranges) {
    public static final Loop DEFAULT = new Loop(8, ImmutableList.of());
    public static final Codec<Loop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.intRange(1, Integer.MAX_VALUE).optionalFieldOf("days", 8).forGetter(Loop::days),
            Range.CODEC.listOf().optionalFieldOf("ranges", ImmutableList.of()).forGetter(Loop::ranges)
    ).apply(instance, Loop::new));
}