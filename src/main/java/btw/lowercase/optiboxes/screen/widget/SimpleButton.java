package btw.lowercase.optiboxes.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleButton extends Gidget implements Clickable, TextHolder {
    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 20;

    private final Component text;
    private final Consumer<? super SimpleButton> onClick;

    public SimpleButton(Component text, int x, int y, Consumer<? super SimpleButton> onClick) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.text = text;
        this.onClick = onClick;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        boolean hovered = this.isInside(mouseX, mouseY);

        int backgroundColor = hovered ? ARGB.color(0.7F, 0xFFFFFF) : ARGB.color(0.58F, 0xFFFFFF);
        guiGraphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, backgroundColor);

        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(this.text.getString());
        int textColor = hovered ? ARGB.color(1.0F, 0xFFFFA0) : ARGB.color(1.0F, 0xE0E0E0);
        guiGraphics.drawString(font, this.text, this.x + ((this.width / 2) - (textWidth / 2)), this.y + ((this.height / 2) - (font.lineHeight / 2)), textColor);
    }

    @Override
    public void click(double mouseX, double mouseY) {
        this.onClick.accept(this);
    }

    @Override
    public Component getText() {
        return this.text;
    }

    public static class Storage extends SimpleButton {
        private final Map<Identifier, Object> data;

        public Storage(Component text, int x, int y, Consumer<Storage> onClick) {
            super(text, x, y, (Consumer<? super SimpleButton>) (Consumer<?>) onClick);
            this.data = new HashMap<>();
        }

        public void put(Identifier location, Object data) {
            this.data.put(location, data);
        }

        public boolean has(Identifier location) {
            return this.data.containsKey(location);
        }

        public <T> T get(Identifier location) {
            return (T) this.data.get(location);
        }
    }
}
