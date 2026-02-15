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
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class DebugScreen extends Screen {
	protected final List<Gidget> gidgets;
	private final Screen parent;

	public DebugScreen(final Component title, final Screen parent) {
		super(title);
		this.gidgets = new ArrayList<>();
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.gidgets.clear();
	}

	@Override
	public void render(final @NotNull GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float delta) {
		super.render(guiGraphics, mouseX, mouseY, delta);
		for (final Gidget gidget : this.gidgets) {
			gidget.render(guiGraphics, mouseX, mouseY);
		}
	}

	private boolean mouseClickedInternal(final double mouseX, final double mouseY) {
		for (final Gidget gidget : this.gidgets) {
			if (gidget.box().contains((int) mouseX, (int) mouseY) && gidget.onMouseClicked(mouseX, mouseY)) {
				return true;
			}
		}

		return false;
	}

	//? >=1.21.9 {
	@Override
	public boolean mouseClicked(final net.minecraft.client.input.MouseButtonEvent event, final boolean isDoubleClick) {
		return this.mouseClickedInternal(event.x(), event.y());
	}
	//?} else {
    /*@Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return this.mouseClickedInternal(mouseX, mouseY);
    }
    *///?}

	@Override
	public void mouseMoved(final double mouseX, final double mouseY) {
		for (final Gidget gidget : this.gidgets) {
			gidget.onMouseMove(mouseX, mouseY);
		}

		super.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(final double mouseX, final double mouseY, final double scrollX, final double scrollY) {
		for (final Gidget gidget : this.gidgets) {
			if (gidget.box().contains((int) mouseX, (int) mouseY) && gidget.onMouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
				return true;
			}
		}

		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	private boolean keyPressedInternal(final int keyCode, final int scanCode, final int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.minecraft.setScreen(null);
			return true;
		}

		for (final Gidget gidget : this.gidgets) {
			if (gidget.onKeyDown(scanCode, keyCode, modifiers)) {
				return true;
			}
		}

		return false;
	}

	//? >=1.21.9 {
	@Override
	public boolean keyPressed(final net.minecraft.client.input.KeyEvent event) {
		return this.keyPressedInternal(event.key(), event.scancode(), event.modifiers());
	}
	//?} else {
    /*@Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        return this.keyPressedInternal(keyCode, scanCode, modifiers);
    }
    *///?}

	private boolean keyReleasedInternal(final int keyCode, final int scanCode, final int modifiers) {
		for (final Gidget gidget : this.gidgets) {
			if (gidget.onKeyUp(scanCode, keyCode, modifiers)) {
				return true;
			}
		}

		return false;
	}

	//? >=1.21.9 {
	@Override
	public boolean keyReleased(final net.minecraft.client.input.KeyEvent event) {
		return this.keyReleasedInternal(event.key(), event.scancode(), event.modifiers());
	}
	//?} else {
    /*@Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        return this.keyReleasedInternal(keyCode, scanCode, modifiers);
    }
    *///?}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parent);
	}
}
