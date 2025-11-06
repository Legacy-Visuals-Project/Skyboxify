package btw.lowercase.optiboxes.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleButton extends Gidget implements TextHolder {
    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 20;
    public static final int DEFAULT_PADDING = 8;

    private final Text text;
    private final Consumer<? super SimpleButton> onClick;

    public SimpleButton(Component text, int x, int y, Consumer<? super SimpleButton> onClick) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.text = new Text.Builder(text, this.x() + (this.width() / 2), this.y() + (this.height() / 2))
                .positioned(Text.Positioned.BOTH)
                .build(Minecraft.getInstance().font);
        this.resize(Math.max(this.text.width() + DEFAULT_PADDING, DEFAULT_WIDTH), this.height());
        this.onClick = onClick;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        text.setColor(this.hovered() ? ARGB.color(1.0F, 0xFFFFA0) : ARGB.color(1.0F, 0xE0E0E0));
        text.render(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY) {
        this.onClick.accept(this);
    }

    @Override
    public void reposition(int x, int y) {
        super.reposition(x, y);
        this.text.reposition(this.x() + (this.width() / 2), this.y() + (this.height() / 2));
    }

    @Override
    public Component getText() {
        return this.text.getText();
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
