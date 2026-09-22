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

package btw.lowercase.skyboxify.screen.widget;

import btw.lowercase.skyboxify.screen.widget.components.Box;
import net.minecraft.client.render.TextRenderer;
import net.ornithemc.osl.text.api.TextComponent;
import net.ornithemc.osl.text.impl.LiteralTextComponent;

public class Text extends Gidget {
    private final TextRenderer font;
    private TextComponent text;
    private Alignment alignment;
    private boolean shadow;
    private int color;

    public Text(final TextRenderer font, final TextComponent text, final int x, final int y, final Alignment alignment, final boolean shadow, final int color) {
        super(new Box(x, y, font.getWidth(text.buildFormattedString()), font.fontHeight));
        this.font = font;
        this.text = text;
        this.alignment = alignment;
        this.shadow = shadow;
        this.color = color;
    }

    public static Builder builder(final TextComponent component) {
        return new Builder(component);
    }

    public static Builder builder(final String text) {
        return new Builder(text);
    }

    public TextComponent getText() {
        return this.text;
    }

    public void setText(final TextComponent text) {
        this.text = text;
        this.resize(this.font.getWidth(text.buildFormattedString()), this.box().height());
    }

    public Alignment getAlignment() {
        return this.alignment;
    }

    public void setAlignment(final Alignment alignment) {
        this.alignment = alignment;
    }

    public boolean hasShadow() {
        return this.shadow;
    }

    public void setShadow(final boolean shadow) {
        this.shadow = shadow;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(final int color) {
        this.color = color;
    }

    /*@Override
    public void extractRenderState(final GuiGraphicsExtractor guiGraphics, final int mouseX, final int mouseY) {
        int finalX = this.box().left();
        if (this.alignment == Alignment.BOTH || this.alignment == Alignment.CENTER_HORIZONTAL) {
            finalX -= this.box().width() / 2;
        }

        int finalY = this.box().top();
        if (this.alignment == Alignment.BOTH || this.alignment == Alignment.CENTER_VERTICAL) {
            finalY -= this.box().height() / 2;
        }

        guiGraphics.text(this.font, this.text, finalX, finalY, this.color, this.shadow);
        //? >=1.21.9 {
        if (new Box(finalX, finalY, this.box.width(), this.box.height()).contains(mouseX, mouseY)) {
            guiGraphics.requestCursor(com.mojang.blaze3d.platform.cursor.CursorTypes.IBEAM);
        }
        //?}
    }*/

    public Builder builder() {
        return new Builder(this.text)
                .position(this.box().left(), this.box().top())
                .aligned(this.alignment)
                .withColor(this.color)
                .withShadow(this.shadow);
    }

    public enum Alignment {
        CENTER_VERTICAL,
        CENTER_HORIZONTAL,
        BOTH,
        NONE
    }

    public static class Builder {
        private final TextComponent text;

        private int color = -1; // TODO: ARGB.white(1.0F);
        private Alignment alignment = Alignment.NONE;
        private boolean shadow = true;
        private int x = 0;
        private int y = 0;

        public Builder(final TextComponent text) {
            this.text = text;
        }

        public Builder(final String text) {
            this(new LiteralTextComponent(text));
        }

        public Builder position(final int x, final int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder withColor(final int color) {
            this.color = color;
            return this;
        }

        public Builder withShadow(final boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder aligned(final Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public Builder centered() {
            this.alignment = Alignment.CENTER_HORIZONTAL;
            return this;
        }

        public Text build(final TextRenderer font) {
            return new Text(font, this.text, this.x, this.y, this.alignment, this.shadow, this.color);
        }
    }
}