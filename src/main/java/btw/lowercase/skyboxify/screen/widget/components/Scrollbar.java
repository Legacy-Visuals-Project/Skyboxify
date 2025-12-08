/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 Contributors to the project retain their copyright
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * "MINECRAFT" LINKING EXCEPTION TO THE GPL
 */

package btw.lowercase.skyboxify.screen.widget.components;

import btw.lowercase.skyboxify.screen.widget.Gidget;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;

public class Scrollbar extends Gidget {
    public static final int DEFAULT_WIDTH = 10;
    private final Knob knob;
    @Getter
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
        guiGraphics.fill(this.box().left(), this.box().top(), this.box().right(), this.box().bottom(), ARGB.color(128, 0x00FF95));
    }

    public void setScrollY(double scrollY) {
    }

    private class Knob extends Gidget {
        public static final int DEFAULT_HEIGHT = 30;

        public Knob(int x, int y, int height) {
            super(new Box(x, y, Scrollbar.this.box().width(), height));
        }

        @Override
        public void renderBackground(GuiGraphics guiGraphics) {
            guiGraphics.fill(this.box().left(), this.box().top(), this.box().right(), this.box().bottom(), ARGB.color(255, 0xAAFE00));
        }
    }
}
