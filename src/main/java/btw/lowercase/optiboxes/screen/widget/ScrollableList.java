package btw.lowercase.optiboxes.screen.widget;

import btw.lowercase.optiboxes.screen.widget.components.Box;
import btw.lowercase.optiboxes.screen.widget.components.Scrollbar;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;

import java.util.List;

public class ScrollableList extends Gidget {
    private final List<Gidget> gidgets;
    private final Scrollbar scrollbar;

    public ScrollableList(List<Gidget> gidgets, int x, int y, int width, int height) {
        super(new Box(x, y, width, height));
        this.gidgets = gidgets;
        this.scrollbar = new Scrollbar(width - Scrollbar.DEFAULT_WIDTH - 8, y, height);
        for (Gidget gidget : this.gidgets) {
            // TODO: scrollY
            gidget.move(this.box().left() + gidget.box().left(), this.box().top() + gidget.box().top() + 4);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.render(guiGraphics, mouseX, mouseY);
        this.scrollbar.render(guiGraphics, mouseX, mouseY);
        guiGraphics.enableScissor(this.box().left(), this.box().top(), this.box().right(), this.box().bottom());
        for (Gidget gidget : this.gidgets) {
            // TODO: scrollY
            gidget.render(guiGraphics, mouseX, mouseY);
        }
        guiGraphics.disableScissor();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.fill(this.box().left(), this.box().top(), this.box().right(), this.box().bottom(), ARGB.color(0.3F, 0));
        final int lineColor = ARGB.color(0.67F, 0xA0A0A0);
        guiGraphics.hLine(this.box().left(), this.box().right(), this.box().top(), lineColor);
        guiGraphics.hLine(this.box().left(), this.box().right(), this.box().top() + this.box().height(), lineColor);
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);
        for (Gidget gidget : this.gidgets) {
            if (this.box().contains((int) mouseX, (int) mouseY)) {
                gidget.onMouseMove(mouseX, mouseY);
            }
        }

        if (this.scrollbar.box().contains((int) mouseX, (int) mouseY)) {
            this.scrollbar.onMouseMove(mouseX, mouseY);
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY) {
        super.onMouseClicked(mouseX, mouseY);
        for (Gidget gidget : this.gidgets) {
            if (this.box().contains((int) mouseX, (int) mouseY) && gidget.box().contains((int) mouseX, (int) mouseY)) {
                gidget.onMouseClicked(mouseX, mouseY);
            }
        }

        if (this.scrollbar.box().contains((int) mouseX, (int) mouseY)) {
            this.scrollbar.onMouseClicked(mouseX, mouseY);
        }
    }

    @Override
    public void onMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (Gidget gidget : this.gidgets) {
            if (gidget.box().contains((int) mouseX, (int) mouseY)) {
                gidget.onMouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        }

        super.onMouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onKeyDown(int scancode, int key, int modifiers) {
        for (Gidget gidget : this.gidgets) {
            gidget.onKeyDown(scancode, key, modifiers);
        }

        super.onKeyDown(scancode, key, modifiers);
    }

    @Override
    public void onKeyUp(int scancode, int key, int modifiers) {
        for (Gidget gidget : this.gidgets) {
            gidget.onKeyUp(scancode, key, modifiers);
        }

        super.onKeyUp(scancode, key, modifiers);
    }
}
