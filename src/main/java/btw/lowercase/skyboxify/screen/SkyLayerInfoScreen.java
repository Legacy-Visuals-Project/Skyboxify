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

package btw.lowercase.skyboxify.screen;

import btw.lowercase.skyboxify.screen.widget.SimpleButton;
import btw.lowercase.skyboxify.screen.widget.Text;
import btw.lowercase.skyboxify.skybox.SkyLayer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SkyLayerInfoScreen extends DebugScreen {
	private final SkyLayer skyLayer;

	public SkyLayerInfoScreen(Screen parent, SkyLayer skyLayer, int index) {
		super(Component.literal(index + " - " + skyLayer.source().toString()), parent);
		this.skyLayer = skyLayer;
	}

	@Override
	protected void init() {
		super.init();

		this.gidgets.add(Text.builder(this.title)
				.position(this.width / 2, 12)
				.centered()
				.build(this.font));

		addLine(Text.builder("Rotate: " + this.skyLayer.rotate()).centered().build(this.font));
		addLine(Text.builder("Axis: " + this.skyLayer.axis()).centered().build(this.font));
		addLine(Text.builder("Blend: " + this.skyLayer.blend()).centered().build(this.font));
		addLine(Text.builder("Speed: " + this.skyLayer.speed()).centered().build(this.font));
		addLine(Text.builder("Transition: " + this.skyLayer.transition()).centered().build(this.font));
		addLine(Text.builder("Fade: " + this.skyLayer.fade()).centered().build(this.font));
		addLine(Text.builder("Loop: " + this.skyLayer.loop()).centered().build(this.font));
		addLine(Text.builder("Biomes: " + this.skyLayer.biomes()).centered().build(this.font));
		addLine(Text.builder("Heights: " + this.skyLayer.heights()).centered().build(this.font));
		addLine(Text.builder("Weather Conditions: " + this.skyLayer.weatherConditions()).centered().build(this.font));

		this.gidgets.add(new SimpleButton(
				CommonComponents.GUI_BACK,
				(this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
				this.height - SimpleButton.DEFAULT_HEIGHT - 4,
				(button) -> this.onClose()
		));
	}

	private void addLine(final Text text) {
		text.move(this.width / 2, 12 + (this.font.lineHeight * 3) + ((this.font.lineHeight + 2) * (this.gidgets.size() - 1)));
		text.setText(Component.literal("    " + text.getText().getString()));
		this.gidgets.add(text);
	}
}