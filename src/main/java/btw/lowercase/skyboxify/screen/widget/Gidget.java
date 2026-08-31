/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025-2026 lowercasebtw
 * Copyright (C) 2025-2026 Contributors to the project retain their copyright
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
import net.minecraft.util.ARGB;

public abstract class Gidget {
	protected final Box box;

    public Gidget(final Box box) {
        this.box = box;
    }

	public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
		this.renderBackground(guiGraphics, mouseX, mouseY);
	}

	public void renderBackground(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
		final int backgroundColor = this.box.contains(mouseX, mouseY) ? ARGB.white(0.7F) : ARGB.white(0.58F);
		guiGraphics.fill(this.box.left(), this.box.top(), this.box.right(), this.box.bottom(), backgroundColor);
	}

	public boolean onMouseMove(final double mouseX, final double mouseY) {
		return false;
	}

	public boolean onMouseClicked(final double mouseX, final double mouseY) {
		return false;
	}

	public boolean onMouseScrolled(final double mouseX, final double mouseY, final double scrollX, final double scrollY) {
		return false;
	}

	public boolean onKeyDown(final int scancode, final int key, final int modifiers) {
		return false;
	}

	public boolean onKeyUp(final int scancode, final int key, final int modifiers) {
		return false;
	}

	public void move(final int x, final int y) {
		this.box.move(x, y);
	}

	public void resize(final int width, final int height) {
		this.box.resize(width, height);
	}

	public Box box() {
		return this.box;
	}
}
