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
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class Text extends Gidget {
	private final Font font;
	@Getter
	private Component text;
	@Getter
	@Setter
	private Alignment alignment;
	@Setter
	private boolean shadow;
	@Getter
	@Setter
	private int color;

	public Text(
			final Font font,
			final Component text,
			final int x,
			final int y,
			final Alignment alignment,
			final boolean shadow,
			final int color
	) {
		super(new Box(x, y, font.width(text.getString()), font.lineHeight));
		this.font = font;
		this.text = text;
		this.alignment = alignment;
		this.shadow = shadow;
		this.color = color;
	}

	public static Builder builder(final Component component) {
		return new Builder(component);
	}

	public static Builder builder(final String text) {
		return new Builder(text);
	}

	@Override
	public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
		int finalX = this.box().left();
		if (this.alignment == Alignment.BOTH || this.alignment == Alignment.CENTER_HORIZONTAL) {
			finalX -= this.box().width() / 2;
		}

		int finalY = this.box().top();
		if (this.alignment == Alignment.BOTH || this.alignment == Alignment.CENTER_VERTICAL) {
			finalY -= this.box().height() / 2;
		}

		guiGraphics.drawString(this.font, this.text, finalX, finalY, this.color, this.shadow);
		//? >=1.21.9 {
		if (new Box(finalX, finalY, this.box.width(), this.box.height()).contains(mouseX, mouseY)) {
			guiGraphics.requestCursor(com.mojang.blaze3d.platform.cursor.CursorTypes.IBEAM);
		}
		//?}
	}

	public Builder builder() {
		return new Builder(this.text)
				.position(this.box().left(), this.box().top())
				.aligned(this.alignment)
				.withColor(this.color)
				.withShadow(this.shadow);
	}

	public boolean hasShadow() {
		return this.shadow;
	}

	public void setText(final Component text) {
		this.text = text;
		this.resize(this.font.width(text.getString()), this.box().height());
	}

	public enum Alignment {
		CENTER_VERTICAL,
		CENTER_HORIZONTAL,
		BOTH,
		NONE
	}

	public static class Builder {
		private final Component text;

		private int color = ARGB.white(1.0F);
		private Alignment alignment = Alignment.NONE;
		private boolean shadow = true;
		private int x = 0;
		private int y = 0;

		public Builder(final Component text) {
			this.text = text;
		}

		public Builder(final String text) {
			this(Component.literal(text));
		}

		public Builder position(final int x, final int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder withColor(final int color) {
			this.color = color;
			return this;
		}

		public Builder withShadow(final boolean shadow) {
			this.shadow = shadow;
			return this;
		}

		public Builder aligned(final Alignment alignment) {
			this.alignment = alignment;
			return this;
		}

		public Builder centered() {
			this.alignment = Alignment.CENTER_HORIZONTAL;
			return this;
		}

		public Text build(final Font font) {
			return new Text(font, this.text, this.x, this.y, this.alignment, this.shadow, this.color);
		}
	}
}
