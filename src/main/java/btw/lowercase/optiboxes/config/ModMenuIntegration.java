package btw.lowercase.optiboxes.config;

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;

//? <1.21.11 {
import btw.lowercase.optiboxes.OptiBoxesClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
//?}

@Entrypoint("modmenu")
public class ModMenuIntegration
    //? <1.21.11
    implements ModMenuApi
{
    //? <1.21.11 {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return OptiBoxesClient.getConfig()::getConfigScreen;
    }
    //?}
}