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
import btw.lowercase.skyboxify.utils.CommonUtils;
import btw.lowercase.skyboxify.utils.Id;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.world.ClientWorld;

import java.util.List;

public class Skybox extends AbstractSkybox {
    public static final Codec<Skybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Id.CODEC.fieldOf("dimension").forGetter(Skybox::dimension),
            SkyLayer.CODEC.listOf().fieldOf("layers").forGetter(Skybox::layers)
    ).apply(instance, Skybox::new));

    private String packName = null;
    private final List<SkyLayer> layers;
    private final Id dimension;
    private boolean active = true;

    public Skybox(final Id dimension, final List<SkyLayer> layers) {
        this.dimension = dimension;
        this.layers = layers;
    }

    public String packName() {
        return this.packName;
    }

    public void setPackName(final String packName) {
        this.packName = packName;
    }

    public List<SkyLayer> layers() {
        return this.layers;
    }

    public Id dimension() {
        return this.dimension;
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public void extractRenderState(final SkyFeatureRenderer skyFeatureRenderer, final ClientWorld level, final float tickDelta) {
        final long dayTime = level.getTime();
        final int clampedTimeOfDay = (int) (dayTime % 24000L);
        final float skyAngle = level.getTimeOfDay(tickDelta);

        float thunderLevel = level.getThunder(tickDelta);
        final float rainLevel = level.getRain(tickDelta);
        if (rainLevel > 0.0F) {
            thunderLevel /= rainLevel;
        }

        for (final SkyLayer layer : this.layers) {
            if (layer.isActive(dayTime, clampedTimeOfDay)) {
                layer.extractRenderState(skyFeatureRenderer, level, clampedTimeOfDay, skyAngle, rainLevel, thunderLevel);
            }
        }
    }

    @Override
    public void tick(final ClientWorld level) {
        final boolean allowOtherDimensions = SkyboxifyImpl.config().showOverworldForUnknownDimension.isEnabled() &&
                this.dimension.equals(CommonUtils.OVERWORLD_ID) &&
                !CommonUtils.dimensionIdentifier(level.dimension.getId()).equals(CommonUtils.NETHER_ID) &&
                !CommonUtils.dimensionIdentifier(level.dimension.getId()).equals(CommonUtils.END_ID);
        this.active = this.dimension.equals(CommonUtils.dimensionIdentifier(level.dimension.getId())) || allowOtherDimensions;
        this.layers.forEach(layer -> layer.tick(this, level));
    }
}
