package btw.lowercase.optiboxes.command;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.skybox.SkyboxManager;
import btw.lowercase.optiboxes.screen.SkyboxListScreen;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public class OptiboxesCommand extends LiteralArgumentBuilder<FabricClientCommandSource> {
    public OptiboxesCommand() {
        super("optiboxes");

        Minecraft minecraft = Minecraft.getInstance();
        executes((context) -> {
            minecraft.schedule(() -> minecraft.setScreen(OptiBoxesClient.getConfig().getConfigScreen(minecraft.screen)));
            return Command.SINGLE_SUCCESS;
        });

        then(ClientCommandManager.literal("debug").executes((context) -> {
            minecraft.schedule(() -> minecraft.setScreen(new SkyboxListScreen(minecraft.screen, SkyboxManager.INSTANCE.getActiveSkyboxes())));
            return Command.SINGLE_SUCCESS;
        }));
    }
}
