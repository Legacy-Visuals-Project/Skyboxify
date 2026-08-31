/**
 * LightConfig
 * A config library.
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 mixces
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

package org.visuals.legacy.lightconfig.lib.v1.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.visuals.legacy.lightconfig.lib.v1.Config;
import org.visuals.legacy.lightconfig.lib.v1.Translations;
import org.visuals.legacy.lightconfig.lib.v1.field.AbstractConfigField;

@ApiStatus.Internal
public class InternalConfigScreen extends Screen {
    protected final Screen parent;
    protected final Config config;

    public InternalConfigScreen(final Component title, final Config config, final Screen parent) {
        super(title);
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);

        this.setupHeader(layout);
        this.setupConfigList(layout);
        this.setupFooter(layout);

        layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();
    }

    private void setupHeader(final HeaderAndFooterLayout layout) {
        LinearLayout linearLayout = layout.addToHeader(LinearLayout.vertical().spacing(8));
        linearLayout.addChild(new StringWidget(this.getTitle(), this.font), LayoutSettings::alignHorizontallyCenter);
    }

    // TODO: Custom Entry Setup
    private void setupConfigList(final HeaderAndFooterLayout layout) {
        final GridLayout gridLayout = layout.addToContents(new GridLayout());
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter().alignVerticallyMiddle();

        final GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(2);
        for (final AbstractConfigField<?> child : this.config.getConfigFields()) {
            rowHelper.addChild(child.createWidget());
        }
    }

    private void setupFooter(final HeaderAndFooterLayout layout) {
        final GridLayout footerGridLayout = layout.addToFooter(new GridLayout());
        footerGridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();

        final GridLayout.RowHelper footerRowHelper = footerGridLayout.createRowHelper(2);
        footerRowHelper.addChild(Button.builder(Translations.RESET, (button) -> this.config.reset()).width(125).build());
        footerRowHelper.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(125).build());
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void onClose() {
        this.config.save();
        this.minecraft.setScreen(this.parent);
    }
}
