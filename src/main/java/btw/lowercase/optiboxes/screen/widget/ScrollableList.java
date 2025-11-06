package btw.lowercase.optiboxes.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class ScrollableList extends Gidget {
    private final List<Gidget> gidgets;

    public ScrollableList(List<Gidget> gidgets, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.gidgets = gidgets;
        for (Gidget gidget : this.gidgets) {
            gidget.reposition(this.x() + gidget.x(), this.y() + gidget.y());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        RenderSystem.enableScissorForRenderTypeDraws(this.x(), this.y(), this.width(), this.height());
        for (Gidget gidget : this.gidgets) {
            gidget.render(guiGraphics, mouseX, mouseY);
        }
        RenderSystem.disableScissorForRenderTypeDraws();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {

    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        super.onMouseMove(mouseX, mouseY);
        for (Gidget gidget : this.gidgets) {
            gidget.onMouseMove(mouseX, mouseY);
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY) {
        super.onMouseClicked(mouseX, mouseY);
        for (Gidget gidget : this.gidgets) {
            if (gidget.isInside(mouseX, mouseY)) {
                gidget.onMouseClicked(mouseX, mouseY);
            }
        }
    }
}
