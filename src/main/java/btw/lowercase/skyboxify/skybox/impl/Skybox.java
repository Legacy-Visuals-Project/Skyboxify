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
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

import java.util.List;

//? >=1.21.11 {
import net.minecraft.world.attribute.EnvironmentAttributes;
//? } else {
/*import net.minecraft.util.Mth;
*///? }

public class Skybox extends AbstractSkybox {
    public static final Codec<Skybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(Skybox::dimension),
            SkyLayer.CODEC.listOf().fieldOf("layers").forGetter(Skybox::layers)
    ).apply(instance, Skybox::new));

    private String packName = null;
    private final List<SkyLayer> layers;
    private final ResourceKey<Level> dimension;
    private boolean active = true;

    public Skybox(final ResourceKey<Level> dimension, final List<SkyLayer> layers) {
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

    public ResourceKey<Level> dimension() {
        return this.dimension;
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public void extractRenderState(final SkyFeatureRenderer skyFeatureRenderer, final ClientLevel level, final Matrix4f modelViewMatrix, final float tickDelta) {
        final long dayTime = level.getOverworldClockTime();
        final int clampedTimeOfDay = (int) (dayTime % 24000L);
        final float skyAngle = this.getSkyAngle(tickDelta);

        float thunderLevel = level.getThunderLevel(tickDelta);
        final float rainLevel = level.getRainLevel(tickDelta);
        if (rainLevel > 0.0F) {
            thunderLevel /= rainLevel;
        }

        for (final SkyLayer layer : this.layers) {
            if (layer.isActive(dayTime, clampedTimeOfDay)) {
                layer.extractRenderState(skyFeatureRenderer, level, new Matrix4f(modelViewMatrix), clampedTimeOfDay, skyAngle, rainLevel, thunderLevel);
            }
        }
    }

    @Override
    public void tick(final ClientLevel level) {
        final boolean allowOtherDimensions = SkyboxifyImpl.config().showOverworldForUnknownDimension &&
                this.dimension.equals(Level.OVERWORLD) &&
                !level.dimension().equals(Level.NETHER) &&
                !level.dimension().equals(Level.END);
        this.active = this.dimension.equals(level.dimension()) || allowOtherDimensions;
        this.layers.forEach(layer -> layer.tick(this, level));
    }

    private float getSkyAngle(final float tickDelta) {
        final Camera camera =
                //? >=26.2 {
                Minecraft.getInstance().gameRenderer.mainCamera();
                //? } else {
                /*Minecraft.getInstance().gameRenderer.getMainCamera();
                 *///? }
        //? >=1.21.11 {
        return camera.attributeProbe().getValue(EnvironmentAttributes.SUN_ANGLE, tickDelta) / 360.0F;
        //?} else {
        /*return camera.getEntity().level().getTimeOfDay(tickDelta);
         *///?}
    }
}
