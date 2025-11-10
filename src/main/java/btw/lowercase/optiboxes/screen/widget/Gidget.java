package btw.lowercase.optiboxes.screen.widget;

import btw.lowercase.optiboxes.screen.widget.components.Box;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.HashMap;
import java.util.Map;

public abstract class Gidget {
    private final Box box;
    private boolean hovered = false;
    private boolean focused = false;
    private final Map<Identifier, Object> data;

    public Gidget(Box box) {
        this.box = box;
        this.data = new HashMap<>();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
    }

    public void renderBackground(GuiGraphics guiGraphics) {
        final int backgroundColor = this.hovered ? ARGB.white(0.7F) : ARGB.white(0.58F);
        guiGraphics.fill(this.box.left(), this.box.top(), this.box.right(), this.box.bottom(), backgroundColor);
    }

    public void onMouseMove(double mouseX, double mouseY) {
        this.hovered = this.box().contains((int) mouseX, (int) mouseY);
    }

    public void onMouseClicked(double mouseX, double mouseY) {
    }

    public void onMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    }

    public void onKeyDown(int scancode, int key, int modifiers) {
    }

    public void onKeyUp(int scancode, int key, int modifiers) {
    }

    public void move(int x, int y) {
        this.box.move(x, y);
    }

    public void resize(int width, int height) {
        this.box.resize(width, height);
    }

    public void store(Identifier location, Object data) {
        this.data.put(location, data);
    }

    public boolean contains(Identifier location) {
        return this.data.containsKey(location);
    }

    public <T> T get(Identifier location) {
        return (T) this.data.get(location);
    }

    public Box box() {
        return this.box;
    }

    public boolean hovered() {
        return this.hovered;
    }
}
