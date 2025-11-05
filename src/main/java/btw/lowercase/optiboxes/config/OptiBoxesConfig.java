package btw.lowercase.optiboxes.config;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.visuals.legacy.lightconfig.lib.v1.Config;
import org.visuals.legacy.lightconfig.lib.v1.field.BooleanConfigField;

import java.nio.file.Path;

public class OptiBoxesConfig extends Config {
    public final BooleanConfigField enabled = this.booleanFieldOf("enabled", true);
    public final BooleanConfigField processOptiFine = this.booleanFieldOf("processOptiFine", true);
    public final BooleanConfigField processMCPatcher = this.booleanFieldOf("processMCPatcher", false);
    public final BooleanConfigField renderSunMoon = this.booleanFieldOf("renderSunMoon", true);
    public final BooleanConfigField renderStars = this.booleanFieldOf("renderStars", true);
    public final BooleanConfigField showOverworldForUnknownDimension = this.booleanFieldOf("showOverworldForUnknownDimension", true);
    public final BooleanConfigField ignoreBrokenSkies = this.booleanFieldOf("ignoreBrokenSkies", false);

    public OptiBoxesConfig(ModContainer modContainer, Path path) {
        super(modContainer, path);
    }

    @Override
    public Screen getConfigScreen(Screen parent) {
        return new OptiBoxesConfigScreen(Component.translatable("options.optiboxes.title"), this, parent);
    }
}