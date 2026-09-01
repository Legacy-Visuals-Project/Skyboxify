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

import btw.lowercase.skyboxify.skybox.impl.components.*;
import btw.lowercase.skyboxify.skybox.renderer.Geometry;
import btw.lowercase.skyboxify.skybox.renderer.RenderUniforms;
import btw.lowercase.skyboxify.skybox.renderer.SkyFeatureRenderer;
import btw.lowercase.skyboxify.utils.CommonUtils;
import btw.lowercase.skyboxify.utils.Id;
import btw.lowercase.skyboxify.utils.ParserCodecs;
import btw.lowercase.skyboxify.utils.ShaderUtil;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3fc;

import java.util.List;

public class SkyLayer {
    private static final float MIN_ALPHA_ALLOWED = 1.0E-4F;

    public static final Codec<SkyLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Id.CODEC.fieldOf("properties").forGetter(SkyLayer::properties),
            Id.CODEC.fieldOf("texture").forGetter(SkyLayer::texture),
            Biomes.CODEC.optionalFieldOf("biomes", Biomes.DEFAULT).forGetter(SkyLayer::biomes),
            Weather.CODEC.optionalFieldOf("weather", Weather.CLEAR).forGetter(SkyLayer::weather),
            Range.CODEC.listOf().optionalFieldOf("heights", ImmutableList.of()).forGetter(SkyLayer::heights),
            Blend.CODEC.optionalFieldOf("blend", Blend.ADD).forGetter(SkyLayer::blend),
            Fade.CODEC.optionalFieldOf("fade", Fade.DEFAULT).forGetter(SkyLayer::fade),
            ParserCodecs.AXIS.optionalFieldOf("axis", CommonUtils.X_AXIS).forGetter(SkyLayer::axis),
            Loop.CODEC.optionalFieldOf("loop", Loop.DEFAULT).forGetter(SkyLayer::loop),
            Codec.BOOL.optionalFieldOf("rotate", true).forGetter(SkyLayer::rotate),
            Codec.FLOAT.optionalFieldOf("speed", 1.0F).forGetter(SkyLayer::speed),
            Codec.INT.optionalFieldOf("transition", 20).forGetter(SkyLayer::transition)
    ).apply(instance, SkyLayer::new));

    private final Id properties;
    private final Id texture;
    private final Biomes biomes;
    private final Weather weather;
    private final List<Range> heights;
    private final Blend blend;
    private final Fade fade;
    private final Vector3fc axis;
    private final Loop loop;
    private final boolean rotate;
    private final float speed;
    private final int transition;

    private float alpha = -1.0F;

    public SkyLayer(
            final Id properties,
            final Id texture,
            final Biomes biomes,
            final Weather weather,
            final List<Range> heights,
            final Blend blend,
            final Fade fade,
            final Vector3fc axis,
            final Loop loop,
            final boolean rotate,
            final float speed,
            final int transition
    ) {
        this.properties = properties;
        this.texture = texture;
        this.biomes = biomes;
        this.weather = weather;
        this.heights = heights;
        this.blend = blend;
        this.fade = fade;
        this.axis = axis;
        this.loop = loop;
        this.rotate = rotate;
        this.speed = speed;
        this.transition = transition;
    }

    public void extractRenderState(
            final SkyFeatureRenderer skyFeatureRenderer,
            final ClientWorld level,
            final int clampedTimeOfDay,
            final float skyAngle,
            final float rainLevel,
            final float thunderLevel
    ) {
        final float weatherAlpha = this.weather.getAlpha(rainLevel, thunderLevel);
        final float fadeAlpha = this.fade.getAlpha(clampedTimeOfDay);
        final float finalAlpha = Math.clamp(this.alpha * weatherAlpha * fadeAlpha, 0.0F, 1.0F);
        if (finalAlpha >= MIN_ALPHA_ALLOWED) {
            GlStateManager.pushMatrix();
            if (this.rotate) {
                GlStateManager.rotatef(this.getAngle(level, skyAngle), this.axis.x(), this.axis.y(), this.axis.z());
            }

            final SkyFeatureRenderer.Pipeline pipeline = new SkyFeatureRenderer.Pipeline(this.blend.getBlendFunction());
            final RenderUniforms uniforms = new RenderUniforms(ShaderUtil.extractModelView(), this.blend.getShaderColor(finalAlpha));
            skyFeatureRenderer.submit(pipeline, Geometry.DEFAULT, uniforms, this.texture);
            GlStateManager.popMatrix();
        }
    }

    public void tick(final Skybox skybox, final ClientWorld level) {
        this.alpha = skybox.isActive() ? this.getPositionBrightness(level) : -1.0F;
    }

    private boolean getConditionCheck(final ClientWorld level) {
        final Entity cameraEntity = Minecraft.getInstance().getCamera();
        final BlockPos blockPos = cameraEntity != null ? new BlockPos(cameraEntity) : null;
        if (blockPos == null) {
            return false;
        } else if (!this.biomes.locations().isEmpty() && !this.biomes.contains(level.getBiome(blockPos))) {
            return false;
        } else {
            return this.heights == null || this.heights.isEmpty() || Range.contains(this.heights, blockPos.getY());
        }
    }

    public float getPositionBrightness(final ClientWorld level) {
        if (this.biomes.locations().isEmpty() && this.heights.isEmpty()) {
            return 1.0F;
        } else if (this.alpha == -1.0F) {
            return this.getConditionCheck(level) ? 1.0F : 0.0F;
        } else {
            return CommonUtils.calculateConditionAlphaValue(1.0F, 0.0F, this.alpha, this.transition, this.getConditionCheck(level));
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

    private float getAngle(final ClientWorld level, final float skyAngle) {
        float angleDayStart = 0.0F;
        if (this.speed != (float) Math.round(this.speed)) {
            final long currentWorldDay = (level.getTime() + 18000L) / 24000L;
            final float currentAngle = (float) currentWorldDay * (this.speed % 1.0F);
            angleDayStart = currentAngle % 1.0F;
        }

        return 360.0F * (angleDayStart + skyAngle * this.speed);
    }

    public Id properties() {
        return this.properties;
    }

    public Id texture() {
        return this.texture;
    }

    public Biomes biomes() {
        return this.biomes;
    }

    public Weather weather() {
        return this.weather;
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

    public Vector3fc axis() {
        return this.axis;
    }

    public Loop loop() {
        return this.loop;
    }

    public boolean rotate() {
        return this.rotate;
    }

    public float speed() {
        return this.speed;
    }

    public int transition() {
        return this.transition;
    }

    public float currentAlpha() {
        return this.alpha;
    }
}
