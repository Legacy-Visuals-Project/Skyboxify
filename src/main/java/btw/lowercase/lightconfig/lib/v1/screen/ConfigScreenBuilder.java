package btw.lowercase.lightconfig.lib.v1.screen;

import btw.lowercase.lightconfig.lib.v1.Config;
import btw.lowercase.optiboxes.config.OptiBoxesConfig;
import btw.lowercase.optiboxes.config.OptiBoxesConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigScreenBuilder {
    private final Config config;
    private Component title = Component.empty();

    private ConfigScreenBuilder(Config config) {
        this.config = config;
    }

    public static ConfigScreenBuilder builder(Config config) {
        return new ConfigScreenBuilder(config);
    }

    // TODO

    public ConfigScreenBuilder setTitle(Component title) {
        this.title = title;
        return this;
    }

    public Screen build(@Nullable Screen parent) {
        return new OptiBoxesConfigScreen(parent, this.title, (OptiBoxesConfig) this.config);
        // TODO : return new ConfigScreen(this.title, this.config, parent);
    }
}
