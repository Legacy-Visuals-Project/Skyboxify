package btw.lowercase.optiboxes.config;

import btw.lowercase.lightconfig.lib.v1.Config;
import btw.lowercase.lightconfig.lib.v1.field.BooleanConfigField;
import btw.lowercase.lightconfig.lib.v1.screen.ConfigScreenBuilder;
import btw.lowercase.optiboxes.OptiBoxesClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

public class OptiBoxesConfig extends Config {
    public final BooleanConfigField enabled = this.booleanFieldOf("enabled", true);
    public final BooleanConfigField processOptiFine = this.booleanFieldOf("processOptiFine", true);
    public final BooleanConfigField processMCPatcher = this.booleanFieldOf("processMCPatcher", false);
    public final BooleanConfigField renderSunMoon = this.booleanFieldOf("renderSunMoon", true);
    public final BooleanConfigField renderStars = this.booleanFieldOf("renderStars", true);
    public final BooleanConfigField showOverworldForUnknownDimension = this.booleanFieldOf("showOverworldForUnknownDimension", true);
    public final BooleanConfigField ignoreBrokenSkies = this.booleanFieldOf("ignoreBrokenSkies", false);

    public OptiBoxesConfig(Path path) {
        super(OptiBoxesClient.INSTANCE.getModContainer(), path);
    }

    @Override
    public Screen getConfigScreen(Screen parent) {
        ConfigScreenBuilder builder = ConfigScreenBuilder.builder(this)
                .setTitle(Component.translatable("options.optiboxes.title"));
        // TODO: Add config stuff
        return builder.build(parent);
    }
}