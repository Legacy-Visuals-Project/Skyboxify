package btw.lowercase.optiboxes.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;

public abstract class Gidget {
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean hovered = false;

    public Gidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphics guiGraphics, int mouseX, int mouseY);

    public void renderBackground(GuiGraphics guiGraphics) {
        final int backgroundColor = this.hovered ? ARGB.white(0.7F) : ARGB.white(0.58F);
        guiGraphics.fill(this.x(), this.y(), this.x() + this.width(), this.y() + this.height(), backgroundColor);
    }

    public void onMouseMove(double mouseX, double mouseY) {
        this.hovered = this.isInside(mouseX, mouseY);
    }

    public void onMouseClicked(double mouseX, double mouseY) {
    }

    public boolean isInside(double x, double y) {
        return (x >= this.x && x <= this.x + this.width) && (y >= this.y && y <= this.y + this.height);
    }

    public void reposition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
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

    public boolean hovered() {
        return this.hovered;
    }
}
