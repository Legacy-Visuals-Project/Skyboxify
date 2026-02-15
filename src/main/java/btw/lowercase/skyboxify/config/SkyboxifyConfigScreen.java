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

package btw.lowercase.skyboxify.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.visuals.legacy.lightconfig.lib.v1.Translations;
import org.visuals.legacy.lightconfig.lib.v1.screen.InternalConfigScreen;

@SuppressWarnings("UnstableApiUsage")
public class SkyboxifyConfigScreen extends InternalConfigScreen {
	public SkyboxifyConfigScreen(final Component title, final SkyboxifyConfig config, final Screen parent) {
		super(title, config, parent);
	}

	@Override
	protected void init() {
		final SkyboxifyConfig skyboxifyConfig = (SkyboxifyConfig) this.config;

		final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);
		LinearLayout linearLayout = layout.addToHeader(LinearLayout.vertical().spacing(8));
		linearLayout.addChild(new StringWidget(this.getTitle(), this.font), LayoutSettings::alignHorizontallyCenter);

		final GridLayout gridLayout = new GridLayout();
		gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter().alignVerticallyMiddle();
		final GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(2);
		// Didn't iterate fields here because I wanted custom order
		rowHelper.addChild(skyboxifyConfig.enabled.createWidget());
		rowHelper.addChild(skyboxifyConfig.showOverworldForUnknownDimension.createWidget());
		rowHelper.addChild(skyboxifyConfig.renderSunMoon.createWidget());
		rowHelper.addChild(skyboxifyConfig.renderStars.createWidget());
		rowHelper.addChild(skyboxifyConfig.debug.createWidget());
		rowHelper.addChild(skyboxifyConfig.legacyRotationLogic.createWidget());
		layout.addToContents(gridLayout);

		final GridLayout footerGridLayout = new GridLayout();
		footerGridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
		final GridLayout.RowHelper footerRowHelper = footerGridLayout.createRowHelper(2);
		footerRowHelper.addChild(Button.builder(Translations.RESET, (button) -> this.reset()).width(125).build());
		footerRowHelper.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(125).build());
		layout.addToFooter(footerGridLayout);

		layout.visitWidgets(this::addRenderableWidget);
		layout.arrangeElements();
	}

	private void reset() {
		this.config.reset();
	}
}
