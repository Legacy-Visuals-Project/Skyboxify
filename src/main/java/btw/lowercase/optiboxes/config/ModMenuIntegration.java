package btw.lowercase.optiboxes.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;

@Entrypoint("modmenu")
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return btw.lowercase.optiboxes.OptiBoxesClient.getConfig()::getConfigScreen;
    }
}