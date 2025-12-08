/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 Contributors to the project retain their copyright
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

package btw.lowercase.skyboxify.skybox;

import btw.lowercase.skyboxify.skybox.components.*;
import btw.lowercase.skyboxify.utils.CommonUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.joml.Vector3f;

import java.util.List;

public record OptiFineSkyLayer(
        ResourceLocation source,
        boolean biomeInclusion,
        List<ResourceLocation> biomes,
        List<Range> heights,
        Blend blend,
        Fade fade,
        boolean rotate,
        float speed,
        //? >=1.21.11 {
        /*org.joml.Vector3fc axis,
        *///?} else {
        Vector3f axis,
         //?}
        Loop loop,
        int transition,
        List<Weather> weatherConditions
) {
    public static final Codec<OptiFineSkyLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("source").forGetter(OptiFineSkyLayer::source),
            Codec.BOOL.optionalFieldOf("biomeInclusion", true).forGetter(OptiFineSkyLayer::biomeInclusion),
            ResourceLocation.CODEC.listOf().optionalFieldOf("biomes", ImmutableList.of()).forGetter(OptiFineSkyLayer::biomes),
            Range.CODEC.listOf().optionalFieldOf("heights", ImmutableList.of()).forGetter(OptiFineSkyLayer::heights),
            Blend.CODEC.optionalFieldOf("blend", Blend.ADD).forGetter(OptiFineSkyLayer::blend),
            Fade.CODEC.optionalFieldOf("fade", Fade.DEFAULT).forGetter(OptiFineSkyLayer::fade),
            Codec.BOOL.optionalFieldOf("rotate", true).forGetter(OptiFineSkyLayer::rotate),
            Codec.FLOAT.optionalFieldOf("speed", 1.0F).forGetter(OptiFineSkyLayer::speed),
            ExtraCodecs.VECTOR3F.optionalFieldOf("axis", new Vector3f(1.0F, 0.0F, 0.0F)).forGetter(OptiFineSkyLayer::axis),
            Loop.CODEC.optionalFieldOf("loop", Loop.DEFAULT).forGetter(OptiFineSkyLayer::loop),
            Codec.INT.optionalFieldOf("transition", 1).forGetter(OptiFineSkyLayer::transition),
            Weather.CODEC.listOf().optionalFieldOf("weather", ImmutableList.of(Weather.CLEAR)).forGetter(OptiFineSkyLayer::weatherConditions)
    ).apply(instance, OptiFineSkyLayer::new));

    public boolean getConditionCheck(Level level) {
        final Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity == null) {
            return false;
        }

        final BlockPos entityPos = cameraEntity.getOnPos();
        if (!this.biomes.isEmpty()) {
            final Holder<Biome> currentBiome = level.getBiome(entityPos);
            if (!currentBiome.isBound()) {
                return false;
            }

            if (!(this.biomeInclusion && this.biomes.contains(level.getBiome(cameraEntity.blockPosition()).unwrapKey().orElseThrow()
                            //? >=1.21.11 {
                            /*.identifier()
                    *///?} else {
                    .location()
                     //?}
            ))) {
                return false;
            }
        }

        return this.heights == null || CommonUtils.checkRanges(entityPos.getY(), this.heights);
    }

    public float getPositionBrightness(Level level, float conditionAlpha) {
        if (this.biomes.isEmpty() && this.heights.isEmpty()) {
            return 1.0F;
        } else if (conditionAlpha == -1.0F) {
            return this.getConditionCheck(level) ? 1.0F : 0.0F;
        } else {
            return CommonUtils.calculateConditionAlphaValue(1.0F, 0.0F, conditionAlpha, this.transition * 20, this.getConditionCheck(level));
        }
    }

    public boolean isActive(long dayTime, int clampedTimeOfDay) {
        if (!this.fade.alwaysOn() && CommonUtils.isInTimeInterval(clampedTimeOfDay, this.fade.endFadeOut(), this.fade.startFadeIn())) {
            return false;
        } else if (this.loop.ranges() != null) {
            long adjustedTime = dayTime - (long) this.fade.startFadeIn();
            while (adjustedTime < 0L) {
                adjustedTime += 24000L * this.loop.days();
            }

            final int daysPassed = (int) (adjustedTime / 24000L);
            final int currentDay = daysPassed % this.loop.days();
            // TODO/NOTE: "Days are numbered from 0 to daysLoop-1"
            return CommonUtils.checkRanges(currentDay, this.loop.ranges());
        } else {
            return true;
        }
    }
}
