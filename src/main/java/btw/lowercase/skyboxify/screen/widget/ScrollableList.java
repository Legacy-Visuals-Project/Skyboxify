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
import btw.lowercase.skyboxify.screen.widget.components.Scrollbar;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;

import java.util.List;

public class ScrollableList extends Gidget {
	private final List<Gidget> gidgets;
	private final Scrollbar scrollbar;

	public ScrollableList(List<Gidget> gidgets, int x, int y, int width, int height) {
		super(new Box(x, y, width, height));
		this.gidgets = gidgets;
		this.scrollbar = new Scrollbar(width - Scrollbar.DEFAULT_WIDTH - 8, y + 1, height - 1);
		this.updateGidgetsPosition();
	}

	private void updateGidgetsPosition() {
		final int rowHeight = SimpleButton.DEFAULT_HEIGHT + SimpleButton.DEFAULT_PADDING;
		final int contentHeight = this.gidgets.size() * rowHeight;
		final int scrollableHeight = Math.max(0, contentHeight - this.box().height());
		for (final Gidget gidget : this.gidgets) {
			final Box box = this.box();
			gidget.move(
					box.left() + ((box.width() - gidget.box().width()) / 2),
					(box.top() + (SimpleButton.DEFAULT_PADDING + 1)) +
							(this.gidgets.indexOf(gidget) * rowHeight) -
							(int) (this.scrollbar.getScrollY() * scrollableHeight)
			);
		}
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
		super.extractRenderState(guiGraphics, mouseX, mouseY);
		this.scrollbar.extractRenderState(guiGraphics, mouseX, mouseY);
		guiGraphics.enableScissor(this.box().left(), this.box().top(), this.box().right(), this.box().bottom());
		for (Gidget gidget : this.gidgets) {
			gidget.extractRenderState(guiGraphics, mouseX, mouseY);
		}
		guiGraphics.disableScissor();
	}

	@Override
	public void renderBackground(final GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
		guiGraphics.fill(this.box().left(), this.box().top(), this.box().right(), this.box().bottom(), ARGB.color(76, 0));
		final int lineColor = ARGB.color(170, 0xA0A0A0);
		guiGraphics.horizontalLine(this.box().left(), this.box().right(), this.box().top(), lineColor);
		guiGraphics.horizontalLine(this.box().left(), this.box().right(), this.box().top() + this.box().height(), lineColor);
	}

	@Override
	public boolean onMouseMove(double mouseX, double mouseY) {
		for (Gidget gidget : this.gidgets) {
			if (this.box().contains((int) mouseX, (int) mouseY) && gidget.onMouseMove(mouseX, mouseY)) {
				return true;
			}
		}

		if (this.scrollbar.box().contains((int) mouseX, (int) mouseY)) {
			this.scrollbar.onMouseMove(mouseX, mouseY);
		}

		return super.onMouseMove(mouseX, mouseY);
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY) {
		super.onMouseClicked(mouseX, mouseY);
		for (Gidget gidget : this.gidgets) {
			if (this.box().contains((int) mouseX, (int) mouseY) && gidget.box().contains((int) mouseX, (int) mouseY) && gidget.onMouseClicked(mouseX, mouseY)) {
				return true;
			}
		}

		if (this.scrollbar.box().contains((int) mouseX, (int) mouseY)) {
			this.scrollbar.onMouseClicked(mouseX, mouseY);
		}

		return true;
	}

	@Override
	public boolean onMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		for (Gidget gidget : this.gidgets) {
			if (gidget.box().contains((int) mouseX, (int) mouseY) && gidget.onMouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
				return true;
			}
		}

		final int rowHeight = SimpleButton.DEFAULT_HEIGHT + SimpleButton.DEFAULT_PADDING;
		final int scrollableHeight = Math.max(0, (this.gidgets.size() * rowHeight) - this.box().height());
		final double scrollAmount = (double) rowHeight / scrollableHeight;
		if (scrollY > 0.0) {
			this.scrollbar.setScrollY(this.scrollbar.getScrollY() - scrollAmount);
		} else {
			this.scrollbar.setScrollY(this.scrollbar.getScrollY() + scrollAmount);
		}

		this.updateGidgetsPosition();
		return super.onMouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean onKeyDown(int scancode, int key, int modifiers) {
		for (Gidget gidget : this.gidgets) {
			if (gidget.onKeyDown(scancode, key, modifiers)) {
				return true;
			}
		}

		return super.onKeyDown(scancode, key, modifiers);
	}

	@Override
	public boolean onKeyUp(int scancode, int key, int modifiers) {
		for (Gidget gidget : this.gidgets) {
			if (gidget.onKeyUp(scancode, key, modifiers)) {
				return true;
			}
		}

		return super.onKeyUp(scancode, key, modifiers);
	}
}
