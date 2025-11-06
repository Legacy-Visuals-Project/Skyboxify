package btw.lowercase.optiboxes.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Button {
    public static final int WIDTH = 200;
    public static final int HEIGHT = 20;

    private final Component text;
    private final int x;
    private final int y;
    private final Consumer<Button> onClick;

    public Button(Component text, int x, int y, Consumer<Button> onClick) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.onClick = onClick;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        boolean hovered = this.isInside(mouseX, mouseY);
        guiGraphics.fill(this.x, this.y, this.x + WIDTH, this.y + HEIGHT, hovered ? 0xFF00FF00 : 0xFF00FFFF);

        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(this.text.getString());
        guiGraphics.drawString(font, this.text, this.x + ((WIDTH / 2) - (textWidth / 2)), this.y + ((HEIGHT / 2) - (font.lineHeight / 2)), 0xFFFFFFFF);
    }

    public boolean isInside(double x, double y) {
        return (x >= this.x && x <= this.x + WIDTH) && (y >= this.y && y <= this.y + HEIGHT);
    }

    public Component text() {
        return this.text;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public Consumer<Button> onClick() {
        return this.onClick;
    }

    public static class DataHolder<T> extends Button {
        private final Map<Identifier, T> data;

        public DataHolder(Component text, int x, int y, Consumer<Button> onClick) {
            super(text, x, y, onClick);
            this.data = new HashMap<>();
        }

        public void put(Identifier location, T data) {
            this.data.put(location, data);
        }

        public T get(Identifier location) {
            return this.data.get(location);
        }
    }
}
