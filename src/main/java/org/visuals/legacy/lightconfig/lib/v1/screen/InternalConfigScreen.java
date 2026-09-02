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

import btw.lowercase.skyboxify.utils.Pressable;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LabelWidget;
import net.minecraft.client.render.TextRenderer;
import net.ornithemc.osl.text.api.TextComponent;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.input.Keyboard;
import org.visuals.legacy.lightconfig.lib.v1.Config;
import org.visuals.legacy.lightconfig.lib.v1.Translations;
import org.visuals.legacy.lightconfig.lib.v1.field.AbstractConfigField;

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
        final TextRenderer font = this.minecraft.textRenderer;
        final String title = this.title.buildFormattedString();
        final LabelWidget titleLabel = new LabelWidget(font, 100, (this.width / 2) - (font.getWidth(title) / 2), 33, 0, 0, -1);
        titleLabel.add(title);
        this.labels.add(titleLabel);
    }

    // TODO: Custom Entry Setup
    private void setupConfigList() {
        for (final AbstractConfigField<?> child : this.config.getConfigFields()) {
            final GuiElement element = child.createWidget();
            if (element == null) continue;
            // TODO
        }
    }

    private void setupFooter() {
        final int buttonWidth = 125;
        final int buttonHeight = 20;
        final int footerTop = this.height - (buttonHeight * 2);
        final int footerButtonMiddle = (this.width / 2) - (buttonWidth / 2);

        final ButtonWidget resetButton = new ButtonWidget(101, footerButtonMiddle - (buttonWidth / 2), footerTop + (buttonHeight / 2), buttonWidth, buttonHeight, Translations.RESET.buildFormattedString());
        ((Pressable) resetButton).skyboxify$setup(this.config::reset);

        final ButtonWidget doneButton = new ButtonWidget(102, footerButtonMiddle + (buttonWidth / 2), footerTop + (buttonHeight / 2), buttonWidth, buttonHeight, Translations.DONE.buildFormattedString());
        ((Pressable) doneButton).skyboxify$setup(this::onClose);

        this.buttons.add(resetButton);
        this.buttons.add(doneButton);
    }

    @Override
    protected void keyPressed(final char chr, final int key) {
        if (key == Keyboard.KEY_ESCAPE) {
            this.onClose();
        }
    }

    @Override
    protected void buttonClicked(final ButtonWidget button) {
        ((Pressable) button).skyboxify$onPress();
    }

    public void onClose() {
        this.config.save();
        this.minecraft.openScreen(this.parent);
    }
}
