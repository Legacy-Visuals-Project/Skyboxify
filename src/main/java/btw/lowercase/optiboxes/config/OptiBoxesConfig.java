package btw.lowercase.optiboxes.config;

import btw.lowercase.optiboxes.OptiBoxesClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.visuals.legacy.lightconfig.lib.v1.Config;
import org.visuals.legacy.lightconfig.lib.v1.field.BooleanConfigField;
import org.visuals.legacy.lightconfig.lib.v1.serializer.JsonSerializer;

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
        super(OptiBoxesClient.getModContainer(), path, new JsonSerializer());
    }

    @Override
    public Screen getConfigScreen(Screen parent) {
        return new OptiBoxesConfigScreen(Component.translatable("options.optiboxes.title"), this, parent);
    }
}