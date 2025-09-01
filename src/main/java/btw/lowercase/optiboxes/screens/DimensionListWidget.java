package btw.lowercase.optiboxes.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class DimensionListWidget extends ObjectSelectionList<DimensionListWidget.Entry> {
    public DimensionListWidget(Minecraft minecraft, int width, HeaderAndFooterLayout layout, Consumer<@Nullable ResourceKey<Level>> onPress) {
        super(minecraft, width, layout.getContentHeight(), layout.getHeaderHeight(), 20);
        this.addEntry(new Entry(Level.OVERWORLD, onPress));
        this.addEntry(new Entry(Level.NETHER, onPress));
        this.addEntry(new Entry(Level.END, onPress));
        this.centerListVertically = false;
    }

    @Override
    protected void renderSelection(GuiGraphics guiGraphics, AbstractSelectionList.Entry<?> entry, int i) {
    }

    public static class Entry extends ObjectSelectionList.Entry<Entry> {
        private final ResourceKey<Level> levelResourceKey;
        private final Button selectButton;

        public Entry(ResourceKey<Level> resourceKey, Consumer<@Nullable ResourceKey<Level>> onPress) {
            this.levelResourceKey = resourceKey;
            this.selectButton = Button.builder(Component.literal(this.levelResourceKey.location().toString()), (button) -> onPress.accept(this.levelResourceKey)).size(250, 18).build();
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean selected, float tickDelta) {
            int buttonX = this.getContentX() + ((this.getContentWidth() / 2) - (this.selectButton.getWidth() / 2));
            int buttonY = this.getContentY() + ((this.getContentHeight() / 2) - (this.selectButton.getHeight() / 2));
            this.selectButton.setPosition(buttonX, buttonY);
            this.selectButton.render(guiGraphics, mouseX, mouseY, tickDelta);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i, boolean bl) {
            return this.selectButton.mouseClicked(d, e, i, bl);
        }

        @Override
        public boolean keyPressed(int i, int j, int k) {
            return this.selectButton.keyPressed(i, j, k);
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.translatable(this.levelResourceKey.location().toLanguageKey());
        }
    }
}
