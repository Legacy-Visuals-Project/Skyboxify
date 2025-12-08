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

package btw.lowercase.skyboxify.screen.widget;

import btw.lowercase.skyboxify.screen.widget.components.Box;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.HashMap;
import java.util.Map;

public abstract class Gidget {
    private final Box box;
    private final Map<Identifier, Object> data;
    private boolean hovered = false;
    private boolean focused = false;

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
