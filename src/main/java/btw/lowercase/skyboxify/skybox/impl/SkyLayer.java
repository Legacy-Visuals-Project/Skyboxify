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
import btw.lowercase.skyboxify.skybox.SkyStorage;
import btw.lowercase.skyboxify.skybox.impl.components.*;
import btw.lowercase.skyboxify.skybox.renderer.Geometry;
import btw.lowercase.skyboxify.skybox.renderer.RenderUniforms;
import btw.lowercase.skyboxify.skybox.renderer.SkyFeatureRenderer;
import btw.lowercase.skyboxify.utils.CommonUtils;
import btw.lowercase.skyboxify.utils.ParserCodecs;
import com.google.common.collect.ImmutableList;
//? >=1.21.6
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3fc;

import java.util.List;

public class SkyLayer {
    public static final Codec<SkyLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("properties").forGetter(SkyLayer::properties),
            Identifier.CODEC.fieldOf("texture").forGetter(SkyLayer::texture),
            Biomes.CODEC.optionalFieldOf("biomes", Biomes.DEFAULT).forGetter(SkyLayer::biomes),
            Range.CODEC.listOf().optionalFieldOf("heights", ImmutableList.of()).forGetter(SkyLayer::heights),
            Blend.CODEC.optionalFieldOf("blend", Blend.ADD).forGetter(SkyLayer::blend),
            Fade.CODEC.optionalFieldOf("fade", Fade.DEFAULT).forGetter(SkyLayer::fade),
            Codec.BOOL.optionalFieldOf("rotate", true).forGetter(SkyLayer::rotate),
            Codec.FLOAT.optionalFieldOf("speed", 1.0F).forGetter(SkyLayer::speed),
            ParserCodecs.AXIS.optionalFieldOf("axis", Mth.X_AXIS).forGetter(SkyLayer::axis),
            Loop.CODEC.optionalFieldOf("loop", Loop.DEFAULT).forGetter(SkyLayer::loop),
            Codec.INT.optionalFieldOf("transition", 1).forGetter(SkyLayer::transition),
            ParserCodecs.WEATHER.optionalFieldOf("weather", ImmutableList.of(Weather.CLEAR)).forGetter(SkyLayer::weatherConditions)
    ).apply(instance, SkyLayer::new));

    private final Identifier properties;
    private final Identifier texture;
    private final Biomes biomes;
    private final List<Range> heights;
    private final Blend blend;
    private final Fade fade;
    private final boolean rotate;
    private final float speed;
    private final Vector3fc axis;
    private final Loop loop;
    private final int transition;
    private final List<Weather> weatherConditions;
    //? >=1.21.6
    private final RenderPipeline pipeline;

    public SkyLayer(
            final Identifier properties,
            final Identifier texture,
            final Biomes biomes,
            final List<Range> heights,
            final Blend blend,
            final Fade fade,
            final boolean rotate,
            final float speed,
            final Vector3fc axis,
            final Loop loop,
            final int transition,
            final List<Weather> weatherConditions
    ) {
        this.properties = properties;
        this.texture = texture;
        this.biomes = biomes;
        this.heights = heights;
        this.blend = blend;
        this.fade = fade;
        this.rotate = rotate;
        this.speed = speed;
        this.axis = axis;
        this.loop = loop;
        this.transition = transition;
        this.weatherConditions = weatherConditions;
        //? >=1.21.6
        this.pipeline = SkyStorage.createSkyboxPipeline(blend.getBlendFunction());
    }

    public void extract(
            final SkyFeatureRenderer skyFeatureRenderer,
            final ClientLevel level,
            final Matrix4f modelViewMatrix,
            final int timeOfDay,
            final float skyAngle,
            final float rainLevel,
            final float thunderLevel,
            final float conditionAlpha
    ) {
        final float weatherAlpha = Weather.getAlpha(this.weatherConditions, rainLevel, thunderLevel);
        final float fadeAlpha = this.fade.getAlpha(timeOfDay);
        final float finalAlpha = Mth.clamp(conditionAlpha * weatherAlpha * fadeAlpha, 0.0F, 1.0F);
        if (!(finalAlpha < 1.0E-4F)) {
            if (this.rotate) {
                final float angle = this.getAngle(level, skyAngle);
                if (SkyboxifyImpl.config().debug && SkyboxifyImpl.config().legacyRotationLogic) {
                    CommonUtils.mulPose(modelViewMatrix, angle, this.axis);
                } else {
                    // NOTE: Using `mulPose` directly gives a different result.
                    modelViewMatrix.rotate(new Quaternionf(new AxisAngle4f(angle, this.axis)));
                }
            }

            final SkyFeatureRenderer.Pipeline pipeline = new SkyFeatureRenderer.Pipeline(
                    //? >=1.21.6 {
                    this.pipeline
                    //? } else {
                    /*this.blend.getBlendFunction()
                    *///? }
            );
            final RenderUniforms uniforms = new RenderUniforms(modelViewMatrix, this.blend.getShaderColor(finalAlpha));
            skyFeatureRenderer.submit(pipeline, Geometry.DEFAULT, uniforms, this.texture);
            skyFeatureRenderer.endFrame();
        }
    }

    private boolean getConditionCheck(final ClientLevel level) {
        final Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity == null) {
            return false;
        } else if (!this.biomes.locations().isEmpty() && !this.biomes.contains(level.getBiome(cameraEntity.blockPosition()))) {
            return false;
        } else {
            return this.heights == null || this.heights.isEmpty() || Range.contains(this.heights, cameraEntity.getOnPos().getY());
        }
    }

    public float getPositionBrightness(final ClientLevel level, final float conditionAlpha) {
        if (this.biomes.locations().isEmpty() && this.heights.isEmpty()) {
            return 1.0F;
        } else if (conditionAlpha == -1.0F) {
            return this.getConditionCheck(level) ? 1.0F : 0.0F;
        } else {
            return CommonUtils.calculateConditionAlphaValue(1.0F, 0.0F, conditionAlpha, this.transition * 20, this.getConditionCheck(level));
        }
    }

    public boolean isActive(final long dayTime, final int clampedTimeOfDay) {
        if (!this.fade.alwaysOn() && CommonUtils.isInTimeInterval(clampedTimeOfDay, this.fade.endOut(), this.fade.startIn())) {
            return false;
        } else if (this.loop.ranges() != null) {
            long adjustedTime = dayTime - (long) this.fade.startIn();
            while (adjustedTime < 0L) {
                adjustedTime += 24000L * this.loop.days();
            }

            final int daysPassed = (int) (adjustedTime / 24000L);
            final int currentDay = daysPassed % this.loop.days();
            return this.loop.ranges().isEmpty() || Range.contains(this.loop.ranges(), currentDay);
        } else {
            return true;
        }
    }

    private float getAngle(final Level level, final float skyAngle) {
        float angleDayStart = 0.0F;
        if (this.speed != (float) Math.round(this.speed)) {
            final long currentWorldDay = (level.getOverworldClockTime() + 18000L) / 24000L;
            final double anglePerDay = this.speed % 1.0F;
            final double currentAngle = (double) currentWorldDay * anglePerDay;
            angleDayStart = (float) (currentAngle % 1.0D);
        }

        return (float) Math.toRadians(360.0F * (angleDayStart + skyAngle * this.speed));
    }

    public Identifier properties() {
        return this.properties;
    }

    public Identifier texture() {
        return this.texture;
    }

    public Biomes biomes() {
        return this.biomes;
    }

    public List<Range> heights() {
        return this.heights;
    }

    public Blend blend() {
        return this.blend;
    }

    public Fade fade() {
        return this.fade;
    }

    public boolean rotate() {
        return this.rotate;
    }

    public float speed() {
        return this.speed;
    }

    public Vector3fc axis() {
        return this.axis;
    }

    public Loop loop() {
        return this.loop;
    }

    public int transition() {
        return this.transition;
    }

    public List<Weather> weatherConditions() {
        return this.weatherConditions;
    }
}
