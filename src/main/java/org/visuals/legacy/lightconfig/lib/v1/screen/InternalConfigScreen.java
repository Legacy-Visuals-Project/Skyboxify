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

import btw.lowercase.skyboxify.utils.ButtonExt;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.ornithemc.osl.text.api.TextComponent;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.input.Keyboard;
import org.visuals.legacy.lightconfig.lib.v1.Config;
import org.visuals.legacy.lightconfig.lib.v1.Translations;
import org.visuals.legacy.lightconfig.lib.v1.field.AbstractConfigField;
import org.visuals.legacy.lightconfig.lib.v1.util.ScreenUtil;

@ApiStatus.Internal
public class InternalConfigScreen extends Screen {
    private final TextComponent title;
    protected final Screen parent;
    protected final Config config;

    public InternalConfigScreen(final TextComponent title, final Config config, final Screen parent) {
        super();
        this.title = title;
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void init() {
        this.buttons.clear();
        this.labels.clear();

        this.setupHeader();
        this.setupConfigList();
        this.setupFooter();
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float tickDelta) {
        super.renderBackground();
        super.render(mouseX, mouseY, tickDelta);
    }

    private void setupHeader() {
        this.labels.add(ScreenUtil.centeredLabel(this.title, this.width / 2, 33, -1));
    }

    // TODO: Custom Entry Setup
    private void setupConfigList() {
        final int padding = 8;

        final int rowSize = ScreenUtil.SMALL_BUTTON_WIDTH * 2 + padding;
        final int x = (this.width / 2) - (rowSize / 2);
        final int y = this.height / 4;

        int index = 0;
        for (final AbstractConfigField<?> child : this.config.getConfigFields()) {
            final GuiElement element = child.createWidget();
            if (!(element instanceof ButtonWidget buttonWidget)) continue;

            final int column = index % 2;
            final int row = index / 2;
            buttonWidget.x = x + column * (buttonWidget.getWidth() + padding);
            buttonWidget.y = y + row * (((ButtonExt) buttonWidget).skyboxify$getHeight() + padding);

            this.buttons.add(buttonWidget);
            index++;
        }
    }

    private void setupFooter() {
        final int padding = 4;
        final int footerTop = this.height - (ScreenUtil.BUTTON_HEIGHT * 2);
        final int footerButtonMiddle = (this.width / 2) - (ScreenUtil.SMALL_BUTTON_WIDTH / 2);
        this.buttons.add(ScreenUtil.button(Translations.RESET, button -> this.config.reset())
                .pos(footerButtonMiddle - (ScreenUtil.SMALL_BUTTON_WIDTH / 2) - padding, footerTop + (ScreenUtil.BUTTON_HEIGHT / 2))
                .build());
        this.buttons.add(ScreenUtil.button(Translations.DONE, button -> this.onClose())
                .pos(footerButtonMiddle + (ScreenUtil.SMALL_BUTTON_WIDTH / 2) + padding, footerTop + (ScreenUtil.BUTTON_HEIGHT / 2))
                .build());
    }

    @Override
    protected void keyPressed(final char chr, final int key) {
        if (key == Keyboard.KEY_ESCAPE) {
            this.onClose();
        }
    }

    @Override
    protected void buttonClicked(final ButtonWidget button) {
        ((ButtonExt) button).skyboxify$onPress();
    }

    public void onClose() {
        this.config.save();
        this.minecraft.openScreen(this.parent);
    }
}
