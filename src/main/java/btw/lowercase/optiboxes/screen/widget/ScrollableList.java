package btw.lowercase.optiboxes.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;

import java.util.List;

public class ScrollableList extends Gidget {
    private final List<Gidget> gidgets;
    private double scrollY;

    public ScrollableList(List<Gidget> gidgets, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.gidgets = gidgets;
        this.scrollY = 0.0F;
        for (Gidget gidget : this.gidgets) {
            gidget.reposition(this.x() + gidget.x(), this.y() + gidget.y() + 4);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        guiGraphics.enableScissor(this.x(), this.y(), this.x() + this.width(), this.y() + this.height());
        for (Gidget gidget : this.gidgets) {
            gidget.render(guiGraphics, mouseX, mouseY);
        }
        guiGraphics.disableScissor();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.fill(this.x(), this.y(), this.x() + this.width(), this.y() + this.height(), ARGB.color(0.3F, 0));
        guiGraphics.hLine(this.x(), this.x() + this.width(), this.y(), ARGB.color(0.7F, 0));
        guiGraphics.hLine(this.x(), this.x() + this.width(), this.y() + this.height(), ARGB.color(0.7F, 0));
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);
        for (Gidget gidget : this.gidgets) {
            if (this.isInside(mouseX, mouseY)) {
                gidget.onMouseMove(mouseX, mouseY);
            }
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY) {
        super.onMouseClicked(mouseX, mouseY);
        for (Gidget gidget : this.gidgets) {
            if (this.isInside(mouseX, mouseY) && gidget.isInside(mouseX, mouseY)) {
                gidget.onMouseClicked(mouseX, mouseY);
            }
        }
    }
}
