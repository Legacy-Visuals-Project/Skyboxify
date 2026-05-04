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

package btw.lowercase.skyboxify.skybox.impl;

import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.skybox.AbstractSkybox;
import btw.lowercase.skyboxify.skybox.renderer.SkyFeatureRenderer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Skybox extends AbstractSkybox {
    public static final Codec<Skybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(Skybox::getDimension),
            SkyLayer.CODEC.listOf().fieldOf("layers").forGetter(Skybox::getLayers)
    ).apply(instance, Skybox::new));

    @Getter
    @Setter
    private String packName = null;
    @Getter
    private final List<SkyLayer> layers;
    @Getter
    private final ResourceKey<Level> dimension;
    private final Map<SkyLayer, Float> alphaMap = new HashMap<>();
    @Getter
    private boolean active = true;

    public Skybox(final ResourceKey<Level> dimension, final List<SkyLayer> layers) {
        this.dimension = dimension;
        this.layers = layers;
    }

    @Override
    public void extract(final SkyFeatureRenderer skyFeatureRenderer, final ClientLevel level, final Matrix4f modelViewMatrix, final float tickDelta) {
        final long dayTime = level.getOverworldClockTime();
        final int clampedTimeOfDay = (int) (dayTime % 24000L);
        final float skyAngle = getTimeOfDay(level);
        final float rainLevel = level.getRainLevel(tickDelta);

        float thunderLevel = level.getThunderLevel(tickDelta);
        if (rainLevel > 0.0F) {
            thunderLevel /= rainLevel;
        }

        for (final SkyLayer skyLayer : this.layers.stream().filter(layer -> layer.isActive(dayTime, clampedTimeOfDay)).toList()) {
            skyLayer.extract(skyFeatureRenderer, level, new Matrix4f(modelViewMatrix), clampedTimeOfDay, skyAngle, rainLevel, thunderLevel, this.getConditionAlphaFor(skyLayer));
        }
    }

    @Override
    public void tick(final ClientLevel level) {
        this.active = true;
        final boolean allowOtherDimensions = SkyboxifyImpl.config().showOverworldForUnknownDimension && this.dimension.equals(Level.OVERWORLD) && !level.dimension().equals(Level.NETHER) && !level.dimension().equals(Level.END);
        if (this.dimension.equals(level.dimension()) || allowOtherDimensions) {
            this.layers.forEach(layer -> alphaMap.put(layer, layer.getPositionBrightness(level, this.getConditionAlphaFor(layer))));
        } else {
            this.layers.forEach(layer -> alphaMap.put(layer, -1.0F));
            this.active = false;
        }
    }

    public float getConditionAlphaFor(final SkyLayer layer) {
        return this.alphaMap.getOrDefault(layer, -1.0F);
    }

    private float getTimeOfDay(final ClientLevel level) {
        //? >=1.21.11 {
        long fixedTime = level.getOverworldClockTime();
        if (level.dimensionType().hasFixedTime()) {
            if (level.dimension().equals(Level.NETHER)) {
                fixedTime = 18000L;
            } else if (level.dimension().equals(Level.END)) {
                fixedTime = 6000L;
            }
        }

        final double frac = Mth.frac(fixedTime / 24000.0 - 0.25);
        final double mul = 0.5 - Math.cos(frac * Math.PI) / 2.0;
        return (float)(frac * 2.0 + mul) / 3.0F;
        //?} else {
        /*return level.getTimeOfDay(1.0F);
         *///?}
    }
}
