package btw.lowercase.lightconfig.lib.v1.screen;

import btw.lowercase.lightconfig.lib.v1.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Config config;
    private final Screen parent;

    public ConfigScreen(Component title, Config config, Screen parent) {
        super(title);
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void onClose() {
        this.config.save();
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        // TODO
    }
}
