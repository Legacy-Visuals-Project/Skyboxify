package btw.lowercase.optiboxes.config;

//? <1.21.11 {
/*import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
*///? }
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;

@Entrypoint("modmenu")
public class ModMenuIntegration
    //? <1.21.11
    /*implements ModMenuApi*/
{
    //? <1.21.11 {
    /*@Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return btw.lowercase.optiboxes.OptiBoxesClient.getConfig()::getConfigScreen;
    }
    *///? }
}