package btw.lowercase.optiboxes.screen.widget;

import btw.lowercase.optiboxes.screen.widget.components.Box;
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
        this.text = new Text.Builder(text, this.box().left() + (this.box().width() / 2), this.box().top() + (this.box().height() / 2))
                .positioned(Text.Positioned.BOTH)
                .build(Minecraft.getInstance().font);
        this.resize(Math.max(this.text.box().width() + DEFAULT_PADDING, DEFAULT_WIDTH), this.box().height());
        this.onClick = onClick;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.render(guiGraphics, mouseX, mouseY);
        text.setColor(this.hovered() ? ARGB.color(1.0F, 0xFFFFA0) : ARGB.color(1.0F, 0xE0E0E0));
        text.render(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY) {
        this.onClick.accept(this);
    }

    @Override
    public void move(int x, int y) {
        super.move(x, y);
        this.text.move(this.box().left() + (this.box().width() / 2), this.box().top() + (this.box().height() / 2));
    }

    public Component getText() {
        return this.text.getText();
    }
}
