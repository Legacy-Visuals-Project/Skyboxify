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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.util.function.Consumer;

public class SimpleButton extends Gidget {
	public static final int DEFAULT_WIDTH = 200;
	public static final int DEFAULT_HEIGHT = 20;
	public static final int DEFAULT_PADDING = 8;

	private final Text text;
	private final Consumer<? super SimpleButton> onClick;

	public SimpleButton(Component text, int x, int y, Consumer<? super SimpleButton> onClick) {
		super(new Box(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		this.text = Text.builder(text)
				.position(this.box().left() + (this.box().width() / 2), this.box().top() + (this.box().height() / 2))
				.aligned(Text.Alignment.BOTH)
				.build(Minecraft.getInstance().font);
		this.resize(Math.max(this.text.box().width() + DEFAULT_PADDING, DEFAULT_WIDTH), this.box().height());
		this.onClick = onClick;
	}

	public static Builder builder(final Component text, final Consumer<SimpleButton> onClick) {
		return new Builder(text, onClick);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		super.render(guiGraphics, mouseX, mouseY);
		text.setColor(this.box().contains(mouseX, mouseY) ? ARGB.color(255, 0xFFFFA0) : ARGB.color(255, 0xE0E0E0));
		text.render(guiGraphics, mouseX, mouseY);
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY) {
		this.onClick.accept(this);
		return true;
	}

	@Override
	public void move(int x, int y) {
		super.move(x, y);
		this.text.move(this.box().left() + (this.box().width() / 2), this.box().top() + (this.box().height() / 2));
	}

	public Component getText() {
		return this.text.getText();
	}

	public static class Builder {
		private final Component text;
		private final Consumer<SimpleButton> onClick;
		private int x = 0;
		private int y = 0;

		public Builder(final Component text, final Consumer<SimpleButton> onClick) {
			this.text = text;
			this.onClick = onClick;
		}

		public Builder position(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public SimpleButton build() {
			return new SimpleButton(this.text, this.x, this.y, this.onClick);
		}
	}
}
