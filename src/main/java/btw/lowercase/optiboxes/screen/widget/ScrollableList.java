package btw.lowercase.optiboxes.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;

import java.util.List;

public class ScrollableList extends Gidget {
    private final List<Gidget> gidgets;
    private final Scrollbar scrollbar;

    public ScrollableList(List<Gidget> gidgets, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.gidgets = gidgets;
        this.scrollbar = new Scrollbar(width - Scrollbar.DEFAULT_WIDTH - 8, y, height);
        for (Gidget gidget : this.gidgets) {
            gidget.reposition(this.x() + gidget.x(), this.y() + gidget.y() + 4);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        this.scrollbar.render(guiGraphics, mouseX, mouseY);
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

        if (this.scrollbar.isInside(mouseX, mouseY)) {
            this.scrollbar.onMouseMove(mouseX, mouseY);
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

        if (this.scrollbar.isInside(mouseX, mouseY)) {
            this.scrollbar.onMouseClicked(mouseX, mouseY);
        }
    }

    private static class Scrollbar extends Gidget {
        public static final int DEFAULT_WIDTH = 10;
        private final Knob knob;
        private double scrollY;

        protected Scrollbar(int x, int y, int height) {
            super(x, y, DEFAULT_WIDTH, height);
            this.knob = new Knob(x, y, Knob.DEFAULT_HEIGHT);
            this.scrollY = 0.0;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            this.renderBackground(guiGraphics);
            this.knob.render(guiGraphics, mouseX, mouseY);
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics) {
            guiGraphics.fill(this.x(), this.y(), this.x() + this.width(), this.y() + this.height(), ARGB.color(0.5F, 0x00FF95));
        }

        @Override
        public void onMouseClicked(double mouseX, double mouseY) {
            super.onMouseClicked(mouseX, mouseY);
        }

        @Override
        public void onMouseMove(double mouseX, double mouseY) {
            super.onMouseMove(mouseX, mouseY);
        }

        public double getScrollY() {
            return this.scrollY;
        }

        private class Knob extends Gidget {
            public static final int DEFAULT_HEIGHT = 30;

            public Knob(int x, int y, int height) {
                super(x, y, Scrollbar.this.width(), height);
            }

            @Override
            public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
                this.renderBackground(guiGraphics);
            }

            @Override
            public void renderBackground(GuiGraphics guiGraphics) {
                guiGraphics.fill(this.x(), this.y(), this.x() + this.width(), this.y() + this.height(), ARGB.color(1.0F, 0xAAFE00));
            }
        }
    }
}
