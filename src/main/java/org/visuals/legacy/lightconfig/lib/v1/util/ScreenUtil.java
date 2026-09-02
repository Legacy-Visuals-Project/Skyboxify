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

package org.visuals.legacy.lightconfig.lib.v1.util;

import btw.lowercase.skyboxify.utils.ButtonExt;
import btw.lowercase.skyboxify.utils.Pressable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LabelWidget;
import net.minecraft.client.render.TextRenderer;
import net.ornithemc.osl.text.api.TextComponent;

public final class ScreenUtil {
    private ScreenUtil() {
    }

    public static final int SMALL_BUTTON_WIDTH = 125;
    public static final int BUTTON_HEIGHT = 20;
    private static final TextRenderer font = Minecraft.getInstance().textRenderer;

    public static ButtonBuilder button(final TextComponent text, final Pressable<ButtonWidget> pressable) {
        return new ButtonBuilder(text, pressable);
    }

    public static LabelWidget label(final String text, final int x, final int y, final int color) {
        final LabelWidget label = new LabelWidget(font, text.hashCode(), x, y, 0, 0, color);
        label.add(text);
        return label;
    }

    public static LabelWidget label(final TextComponent text, final int x, final int y, final int color) {
        return label(text.buildFormattedString(), x, y, color);
    }

    public static LabelWidget centeredLabel(final String text, final int x, final int y, final int color) {
        return label(text, x - (font.getWidth(text) / 2), y, color);
    }

    public static LabelWidget centeredLabel(final TextComponent text, final int x, final int y, final int color) {
        return centeredLabel(text.buildFormattedString(), x, y, color);
    }

    public static class ButtonBuilder {
        private final TextComponent text;
        private final Pressable<ButtonWidget> runnable;

        private int x = 0;
        private int y = 0;
        private int width = SMALL_BUTTON_WIDTH;
        private int height = BUTTON_HEIGHT;

        ButtonBuilder(final TextComponent text, final Pressable<ButtonWidget> runnable) {
            this.text = text;
            this.runnable = runnable;
        }

        public ButtonBuilder pos(final int x, final int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public ButtonBuilder bounds(final int x, final int y, final int width, final int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public ButtonWidget build() {
            final ButtonWidget widget = new ButtonWidget(-1, this.x, this.y, this.width, this.height, this.text.buildFormattedString());
            ((ButtonExt) widget).skyboxify$setup(this.runnable);
            return widget;
        }
    }
}
