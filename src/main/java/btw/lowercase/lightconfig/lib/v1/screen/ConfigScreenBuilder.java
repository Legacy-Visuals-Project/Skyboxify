package btw.lowercase.lightconfig.lib.v1.screen;

import btw.lowercase.lightconfig.lib.v1.Config;
import btw.lowercase.optiboxes.config.OptiBoxesConfig;
import btw.lowercase.optiboxes.config.OptiBoxesConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class ConfigScreenBuilder {
    private final Config config;

    private ConfigScreenBuilder(Config config) {
        this.config = config;
    }

    public static ConfigScreenBuilder builder(Config config) {
        return new ConfigScreenBuilder(config);
    }

    // TODO

    public Screen build(@Nullable Screen parent) {
        return new OptiBoxesConfigScreen(parent, (OptiBoxesConfig) this.config);
        // TODO
//        return new ConfigScreen(
//                Component.literal(this.config.getModContainer().getMetadata().getId()),
//                this.config,
//                parent
//        );
    }
}
