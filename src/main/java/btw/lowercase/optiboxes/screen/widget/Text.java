package btw.lowercase.optiboxes.screen.widget;

import btw.lowercase.optiboxes.screen.widget.components.Box;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class Text extends Gidget {
    private final Font font;
    private final Component text;
    private Positioned positioned;
    private boolean shadow;
    private int color;

    public Text(Font font, Component text, int x, int y, Positioned positioned, boolean shadow, int color) {
        super(new Box(x, y, font.width(text.getString()), font.lineHeight));
        this.font = font;
        this.text = text;
        this.positioned = positioned;
        this.shadow = shadow;
        this.color = color;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int finalX = this.box().x();
        if (this.positioned == Positioned.BOTH || this.positioned == Positioned.CENTER_HORIZONTAL) {
            finalX -= this.box().width() / 2;
        }

        int finalY = this.box().y();
        if (this.positioned == Positioned.BOTH || this.positioned == Positioned.CENTER_VERTICAL) {
            finalY -= this.box().height() / 2;
        }

        guiGraphics.drawString(this.font, this.text, finalX, finalY, this.color, this.shadow);
    }

    public Builder builder() {
        return new Builder(this.text, this.box().x(), this.box().y())
                .withColor(this.color)
                .positioned(this.positioned)
                .withShadow(this.shadow);
    }

    public Component getText() {
        return this.text;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    public void setPositioned(Positioned positioned) {
        this.positioned = positioned;
    }

    public Positioned getPositioned() {
        return this.positioned;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
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
            return new Text(font, this.text, this.x, this.y, this.positioned, this.shadow, this.color);
        }
    }

    public enum Positioned {
        CENTER_VERTICAL,
        CENTER_HORIZONTAL,
        BOTH,
        NONE
    }
}
