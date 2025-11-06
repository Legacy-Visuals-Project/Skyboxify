package btw.lowercase.optiboxes.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleButton extends Gidget {
    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 20;

    private final Component text;
    private final Consumer<SimpleButton> onClick;

    public SimpleButton(Component text, int x, int y, Consumer<SimpleButton> onClick) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.text = text;
        this.onClick = onClick;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        boolean hovered = this.isInside(mouseX, mouseY);
        guiGraphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, hovered ? 0xFF00FF00 : 0xFF00FFFF);

        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(this.text.getString());
        guiGraphics.drawString(font, this.text, this.x + ((this.width / 2) - (textWidth / 2)), this.y + ((this.height / 2) - (font.lineHeight / 2)), 0xFFFFFFFF);
    }

    public Component text() {
        return this.text;
    }

    public Consumer<SimpleButton> onClick() {
        return this.onClick;
    }

    public static class DataHolder<T> extends SimpleButton {
        private final Map<Identifier, T> data;

        public DataHolder(Component text, int x, int y, Consumer<SimpleButton> onClick) {
            super(text, x, y, onClick);
            this.data = new HashMap<>();
        }

        public void put(Identifier location, T data) {
            this.data.put(location, data);
        }

        public boolean has(Identifier location) {
            return this.data.containsKey(location);
        }

        public T get(Identifier location) {
            return this.data.get(location);
        }
    }
}
