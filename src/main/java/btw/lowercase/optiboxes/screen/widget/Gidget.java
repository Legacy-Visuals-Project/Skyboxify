package btw.lowercase.optiboxes.screen.widget;

import net.minecraft.client.gui.GuiGraphics;

public abstract class Gidget {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Gidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphics guiGraphics, int mouseX, int mouseY);

    public boolean isInside(double x, double y) {
        return (x >= this.x && x <= this.x + this.width) && (y >= this.y && y <= this.y + this.height);
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }
}
