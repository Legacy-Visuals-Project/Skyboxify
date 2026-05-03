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

import btw.lowercase.skyboxify.screen.widget.Gidget;
import btw.lowercase.skyboxify.screen.widget.ScrollableList;
import btw.lowercase.skyboxify.screen.widget.SimpleButton;
import btw.lowercase.skyboxify.screen.widget.Text;
import btw.lowercase.skyboxify.skybox.impl.SkyLayer;
import btw.lowercase.skyboxify.skybox.impl.Skybox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SkyLayerListScreen extends DebugScreen {
	private final Skybox skybox;

	public SkyLayerListScreen(final Screen parent, final Skybox skybox) {
		super(Component.literal(skybox.getDimension().identifier().toString()), parent);
		this.skybox = skybox;
	}

	@Override
	protected void init() {
		super.init();

		this.gidgets.add(Text.builder(this.title.getString() + " (" + this.skybox.getLayers().size() + " layer(s)" + ")")
				.position(this.width / 2, 12)
				.centered()
				.build(this.font));

		final List<Gidget> gidgets = new ArrayList<>();
		for (final SkyLayer skyLayer : this.skybox.getLayers()) {
			final Component title = Component.literal(skyLayer.properties().toString());
			gidgets.add(SimpleButton.builder(title, button -> this.minecraft.gui.setScreen(new SkyLayerInfoScreen(this, skyLayer, title))).build());
		}

		final int pad = 20 + font.lineHeight;
		this.gidgets.add(new ScrollableList(gidgets, 0, pad, this.width, this.height - pad - SimpleButton.DEFAULT_HEIGHT - 8));

		this.gidgets.add(SimpleButton.builder(CommonComponents.GUI_BACK, button -> this.onClose())
				.position((this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2), this.height - SimpleButton.DEFAULT_HEIGHT - 4)
				.build());
	}
}

