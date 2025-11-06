package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.screen.widget.Gidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DebugScreen extends Screen {
    protected final List<Gidget> gidgets;
    private final Screen parent;

    public DebugScreen(Component title, Screen parent) {
        super(title);
        this.gidgets = new ArrayList<>();
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.gidgets.clear();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        for (Gidget gidget : this.gidgets) {
            gidget.render(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        for (Gidget gidget : this.gidgets) {
            if (gidget.isInside(event.x(), event.y())) {
                gidget.onMouseClicked(event.x(), event.y());
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (Gidget gidget : this.gidgets) {
            if (gidget.isInside(mouseX, mouseY)) {
                gidget.onMouseEnter(mouseX, mouseY);
            } else if (gidget.hovered()) {
                gidget.onMouseLeave(mouseX, mouseY);
            }
        }

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
