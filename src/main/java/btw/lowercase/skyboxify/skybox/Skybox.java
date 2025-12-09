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

import btw.lowercase.skyboxify.Skyboxify;
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

public class Skybox {
	public static final Codec<Skybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SkyLayer.CODEC.listOf().optionalFieldOf("layers", ImmutableList.of()).forGetter(Skybox::getLayers),
			Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(Skybox::getWorldResourceKey)
	).apply(instance, Skybox::new));

	@Getter
	private final List<SkyLayer> layers;
	@Getter
	private final ResourceKey<@NotNull Level> worldResourceKey;
	private final Map<SkyLayer, Float> optiFineSkyLayerAlphaMap = new HashMap<>();
	@Getter
	private boolean active = true;

	public Skybox(List<SkyLayer> layers, ResourceKey<@NotNull Level> worldResourceKey) {
		this.layers = layers;
		this.worldResourceKey = worldResourceKey;
	}

	public void tick(ClientLevel level) {
		this.active = true;
		if (level.dimension().equals(this.worldResourceKey) || (Skyboxify.getConfig().showOverworldForUnknownDimension.isEnabled() && this.worldResourceKey.equals(Level.OVERWORLD) && !level.dimension().equals(Level.NETHER) && !level.dimension().equals(Level.END))) {
			this.layers.forEach(layer -> optiFineSkyLayerAlphaMap.put(layer, layer.getPositionBrightness(level, this.getConditionAlphaFor(layer))));
		} else {
			this.layers.forEach(layer -> optiFineSkyLayerAlphaMap.put(layer, -1.0F));
			this.active = false;
		}
	}

	public float getConditionAlphaFor(SkyLayer skyLayer) {
		return this.optiFineSkyLayerAlphaMap.getOrDefault(skyLayer, -1.0F);
	}
}
