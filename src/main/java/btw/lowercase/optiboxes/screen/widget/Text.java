package btw.lowercase.optiboxes.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class Text extends Gidget implements TextHolder {
    private final Font font;
    private final Component text;
    private final int color;
    private final Positioned positioned;
    private final boolean shadow;

    public Text(Font font, Component text, int x, int y, int color, Positioned positioned, boolean shadow) {
        super(x, y, font.width(text.getString()), font.lineHeight);
        this.font = font;
        this.text = text;
        this.color = color;
        this.positioned = positioned;
        this.shadow = shadow;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int finalX = this.x();
        if (this.positioned == Positioned.BOTH || this.positioned == Positioned.CENTER_HORIZONTAL) {
            finalX -= this.width() / 2;
        }

        int finalY = this.y();
        if (this.positioned == Positioned.BOTH || this.positioned == Positioned.CENTER_VERTICAL) {
            finalY -= this.height() / 2;
        }

        guiGraphics.drawString(this.font, this.text, finalX, finalY, this.color, this.shadow);
    }

    @Override
    public Component getText() {
        return this.text;
    }

    public int getColor() {
        return this.color;
    }

    public Positioned getPositioned() {
        return this.positioned;
    }

    public boolean hasShadow() {
        return this.shadow;
    }

    public static class Builder {
        private final Component text;
        private final int x;
        private final int y;

        private int color = ARGB.white(1.0F);
        private Positioned positioned = Positioned.NONE;
        private boolean shadow = true;

        public Builder(Component text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }

        public Builder(String text, int x, int y) {
            this(Component.literal(text), x, y);
        }

        public Builder withColor(int color) {
            this.color = color;
            return this;
        }

        public Builder withShadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder positioned(Positioned positioned) {
            this.positioned = positioned;
            return this;
        }

        public Builder centered() {
            this.positioned = Positioned.CENTER_HORIZONTAL;
            return this;
        }

        public Text build(Font font) {
            return new Text(font, this.text, this.x, this.y, this.color, this.positioned, this.shadow);
        }
    }

    public enum Positioned {
        CENTER_VERTICAL,
        CENTER_HORIZONTAL,
        BOTH,
        NONE
    }
}
