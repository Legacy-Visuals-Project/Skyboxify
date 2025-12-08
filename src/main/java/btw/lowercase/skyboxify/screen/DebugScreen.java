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

package btw.lowercase.skyboxify.screen;

import btw.lowercase.skyboxify.screen.widget.Gidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
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

    private void mouseClickedInternal(double mouseX, double mouseY) {
        for (Gidget gidget : this.gidgets) {
            if (gidget.box().contains((int) mouseX, (int) mouseY)) {
                gidget.onMouseClicked(mouseX, mouseY);
            }
        }
    }

    //? >=1.21.9 {
    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean isDoubleClick) {
        this.mouseClickedInternal(event.x(), event.y());
        return super.mouseClicked(event, isDoubleClick);
    }
    //?} else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClickedInternal(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }
    *///?}

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (Gidget gidget : this.gidgets) {
            gidget.onMouseMove(mouseX, mouseY);
        }

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (Gidget gidget : this.gidgets) {
            if (gidget.box().contains((int) mouseX, (int) mouseY)) {
                gidget.onMouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void keyPressedInternal(int keyCode, int scanCode, int modifiers) {
        for (Gidget gidget : this.gidgets) {
            gidget.onKeyDown(scanCode, keyCode, modifiers);
        }
    }

    //? >=1.21.9 {
    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        this.keyPressedInternal(event.key(), event.scancode(), event.modifiers());
        return super.keyPressed(event);
    }
    //?} else {
    /*@Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.keyPressedInternal(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    *///?}

    private void keyReleasedInternal(int keyCode, int scanCode, int modifiers) {
        for (Gidget gidget : this.gidgets) {
            gidget.onKeyUp(scanCode, keyCode, modifiers);
        }
    }

    //? >=1.21.9 {
    @Override
    public boolean keyReleased(net.minecraft.client.input.KeyEvent event) {
        this.keyPressedInternal(event.key(), event.scancode(), event.modifiers());
        return super.keyReleased(event);
    }
    //?} else {
    /*@Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.keyPressedInternal(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    *///?}

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
