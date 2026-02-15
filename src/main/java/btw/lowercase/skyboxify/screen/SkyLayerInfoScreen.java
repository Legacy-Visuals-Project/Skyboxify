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

package btw.lowercase.skyboxify.screen;

import btw.lowercase.skyboxify.screen.widget.SimpleButton;
import btw.lowercase.skyboxify.screen.widget.Text;
import btw.lowercase.skyboxify.skybox.SkyLayer;
import btw.lowercase.skyboxify.utils.CommonUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SkyLayerInfoScreen extends DebugScreen {
	private final SkyLayer skyLayer;

	public SkyLayerInfoScreen(final Screen parent, final SkyLayer skyLayer, final Component title) {
		super(title, parent);
		this.skyLayer = skyLayer;
	}

	@Override
	protected void init() {
		super.init();

		this.gidgets.add(Text.builder(this.title)
				.position(this.width / 2, 12)
				.centered()
				.build(this.font));

		addLine("Texture: " + this.skyLayer.texture());
		addLine("Rotates?: " + this.skyLayer.rotate());
		addLine("Axis: " + CommonUtils.vectorToString(this.skyLayer.axis()));
		addLine("Blend: " + this.skyLayer.blend());
		addLine("Speed: " + this.skyLayer.speed());
		addLine("Transition: " + this.skyLayer.transition());
		addLine("Fade: " + this.skyLayer.fade());
		addLine("Loop: " + this.skyLayer.loop());
		addLine("Biomes: " + this.skyLayer.biomes());
		addLine("Heights: " + this.skyLayer.heights());
		addLine("Weather Conditions: " + this.skyLayer.weatherConditions());

		this.gidgets.add(SimpleButton.builder(CommonComponents.GUI_BACK, button -> this.onClose())
				.position((this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2), this.height - SimpleButton.DEFAULT_HEIGHT - 4)
				.build());
	}

	private void addLine(final String text) {
		this.gidgets.add(Text.builder(text)
				.centered()
				.position(this.width / 2, 12 + (this.font.lineHeight * 3) + ((this.font.lineHeight + 2) * (this.gidgets.size() - 1)))
				.build(this.font));
	}
}