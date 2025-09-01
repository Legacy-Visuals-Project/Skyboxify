package btw.lowercase.optiboxes.screens;

import btw.lowercase.optiboxes.skybox.OptiFineSkyLayer;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkyLayerListWidget extends ObjectSelectionList<SkyLayerListWidget.SkyboxEntry> {
    public SkyLayerListWidget(Minecraft minecraft, int width, HeaderAndFooterLayout layout, List<OptiFineSkybox> skyboxes) {
        super(minecraft, width, layout.getContentHeight(), layout.getHeaderHeight(), 20);
        this.centerListVertically = false;
        int i = 0;
        for (OptiFineSkybox skybox : skyboxes) {
            this.addEntry(new SkyboxEntry(skybox, i));
            i++;
        }
    }

    public static class SkyboxEntry extends ObjectSelectionList.Entry<SkyboxEntry> {
        private final OptiFineSkybox skybox;
        private final int ordinal;

        public SkyboxEntry(OptiFineSkybox skybox, int ordinal) {
            this.skybox = skybox;
            this.ordinal = ordinal;
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean selected, float tickDelta) {
            Font font = Minecraft.getInstance().font;
            String title = "Layer #" + ordinal;
            int titleX = this.getContentX() + (this.getContentWidth() / 2);
            int titleY = this.getContentY() + ((this.getContentHeight() / 2) - (font.lineHeight / 2));
            guiGraphics.drawCenteredString(font, title, titleX, titleY, ARGB.white(1.0F));
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal(Integer.toString(this.ordinal));
        }
    }

    public static class LayerEntry extends ObjectSelectionList.Entry<LayerEntry> {
        private final OptiFineSkyLayer skyLayer;

        public LayerEntry(OptiFineSkyLayer skyLayer) {
            this.skyLayer = skyLayer;
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean selected, float tickDelta) {
            Font font = Minecraft.getInstance().font;
            String title = this.skyLayer.source().toString();
            int titleX = this.getContentX() + (this.getContentWidth() / 2);
            int titleY = this.getContentY() + ((this.getContentHeight() / 2) - (font.lineHeight / 2));
            guiGraphics.drawCenteredString(font, title, titleX, titleY, ARGB.white(1.0F));
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.literal(this.skyLayer.source().toString());
        }
    }
}