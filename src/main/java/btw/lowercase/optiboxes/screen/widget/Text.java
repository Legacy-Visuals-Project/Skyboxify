package btw.lowercase.optiboxes.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class Text extends Gidget {
    private final Font font;
    private final Component text;
    private final int color;
    private final boolean centered;
    private final boolean shadow;

    public Text(Font font, Component text, int x, int y, int color, boolean centered, boolean shadow) {
        super(x, y, font.width(text.getString()), font.lineHeight);
        this.font = font;
        this.text = text;
        this.color = color;
        this.centered = centered;
        this.shadow = shadow;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int finalX = this.x;
        if (this.centered) {
            finalX -= this.width / 2;
        }

        guiGraphics.drawString(this.font, this.text, finalX, this.y, this.color, this.shadow);
    }

    public Component getText() {
        return this.text;
    }

    public int getColor() {
        return this.color;
    }

    public boolean isCentered() {
        return this.centered;
    }

    public boolean hasShadow() {
        return this.shadow;
    }

    public static class Builder {
        private final Font font;
        private final Component text;
        private final int x;
        private final int y;

        private int color = ARGB.white(1.0F);
        private boolean centered = false;
        private boolean shadow = true;

        public Builder(Font font, Component text, int x, int y) {
            this.font = font;
            this.text = text;
            this.x = x;
            this.y = y;
        }

        public Builder(Font font, String text, int x, int y) {
            this(font, Component.literal(text), x, y);
        }

        public Builder withColor(int color) {
            this.color = color;
            return this;
        }

        public Builder withShadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder centered() {
            this.centered = true;
            return this;
        }

        public Text build() {
            return new Text(this.font, this.text, this.x, this.y, this.color, this.centered, this.shadow);
        }
    }
}
