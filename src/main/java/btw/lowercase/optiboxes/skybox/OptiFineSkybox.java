package btw.lowercase.optiboxes.skybox;

import btw.lowercase.optiboxes.OptiBoxesClient;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptiFineSkybox {
    public static final Codec<OptiFineSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OptiFineSkyLayer.CODEC.listOf().optionalFieldOf("layers", ImmutableList.of()).forGetter(OptiFineSkybox::getLayers),
            Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(OptiFineSkybox::getWorldResourceKey)
    ).apply(instance, OptiFineSkybox::new));

    @Getter
    private final List<OptiFineSkyLayer> layers;
    @Getter
    private final ResourceKey<@NotNull Level> worldResourceKey;
    private final Map<OptiFineSkyLayer, Float> optiFineSkyLayerAlphaMap = new HashMap<>();
    @Getter
    private boolean active = true;

    public OptiFineSkybox(List<OptiFineSkyLayer> layers, ResourceKey<Level> worldResourceKey) {
        this.layers = layers;
        this.worldResourceKey = worldResourceKey;
    }

    public void tick(ClientLevel level) {
        this.active = true;
        if (level.dimension().equals(this.worldResourceKey) || (OptiBoxesClient.getConfig().showOverworldForUnknownDimension.isEnabled() && this.worldResourceKey.equals(Level.OVERWORLD) && !level.dimension().equals(Level.NETHER) && !level.dimension().equals(Level.END))) {
            this.layers.forEach(layer -> optiFineSkyLayerAlphaMap.put(layer, layer.getPositionBrightness(level, this.getConditionAlphaFor(layer))));
        } else {
            this.layers.forEach(layer -> optiFineSkyLayerAlphaMap.put(layer, -1.0F));
            this.active = false;
        }
    }

    public float getConditionAlphaFor(OptiFineSkyLayer optiFineSkyLayer) {
        return this.optiFineSkyLayerAlphaMap.getOrDefault(optiFineSkyLayer, -1.0F);
    }
}
