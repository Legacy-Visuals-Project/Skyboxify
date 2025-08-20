package btw.lowercase.lightconfig.lib.v1.screen;

import btw.lowercase.lightconfig.lib.v1.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config;

    public ConfigScreen(Component title, Config config, Screen parent) {
        super(title);
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void onClose() {
        this.config.save();
        this.minecraft.setScreen(this.parent);
    }
}
