package btw.lowercase.optiboxes.screen.widget.components;

import btw.lowercase.optiboxes.screen.widget.Gidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;

public class Scrollbar extends Gidget {
    public static final int DEFAULT_WIDTH = 10;
    private final Knob knob;
    private double scrollY;

    public Scrollbar(int x, int y, int height) {
        super(new Box(x, y, DEFAULT_WIDTH, height));
        this.knob = new Knob(x, y, Knob.DEFAULT_HEIGHT);
        this.scrollY = 0.0;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.render(guiGraphics, mouseX, mouseY);
        this.knob.render(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.fill(this.box().left(), this.box().top(), this.box().right(), this.box().bottom(), ARGB.color(0.5F, 0x00FF95));
    }

    public void setScrollY(double scrollY) {
    }

    public double getScrollY() {
        return this.scrollY;
    }

    private class Knob extends Gidget {
        public static final int DEFAULT_HEIGHT = 30;

        public Knob(int x, int y, int height) {
            super(new Box(x, y, Scrollbar.this.box().width(), height));
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics) {
            guiGraphics.fill(this.box().left(), this.box().top(), this.box().right(), this.box().bottom(), ARGB.color(1.0F, 0xAAFE00));
        }
    }
}
