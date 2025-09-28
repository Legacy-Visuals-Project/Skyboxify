package btw.lowercase.optiboxes.command;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.skybox.OptiFineSkyLayer;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import btw.lowercase.optiboxes.skybox.SkyboxManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class OptiboxesCommand extends LiteralArgumentBuilder<FabricClientCommandSource> {
    public OptiboxesCommand() {
        super("optiboxes");

        Minecraft minecraft = Minecraft.getInstance();
        executes((context) -> {
            minecraft.schedule(() -> minecraft.setScreen(OptiBoxesClient.getConfig().getConfigScreen(minecraft.screen)));
            return Command.SINGLE_SUCCESS;
        });

        then(ClientCommandManager.literal("debug").executes((context) -> {
            final List<OptiFineSkybox> activeSkyboxes = SkyboxManager.INSTANCE.getActiveSkyboxes();
            for (OptiFineSkybox skybox : activeSkyboxes) {
                context.getSource().sendFeedback(Component.literal("Skybox World: " + skybox.getWorldResourceKey()));
                context.getSource().sendFeedback(Component.literal("Skybox Active: " + skybox.isActive()));
                context.getSource().sendFeedback(Component.literal("Layers:"));
                int index = 0;
                for (OptiFineSkyLayer skyLayer : skybox.getLayers()) {
                    context.getSource().sendFeedback(Component.literal("  #" + index + " (" + skyLayer.source() + ")"));
                    context.getSource().sendFeedback(Component.literal("    Rotate: " + skyLayer.rotate()));
                    context.getSource().sendFeedback(Component.literal("    Axis: " + skyLayer.axis()));
                    context.getSource().sendFeedback(Component.literal("    Include Biome: " + skyLayer.biomeInclusion()));
                    context.getSource().sendFeedback(Component.literal("    Fade: " + skyLayer.fade()));
                    context.getSource().sendFeedback(Component.literal("    Blend: " + skyLayer.blend()));
                    context.getSource().sendFeedback(Component.literal("    Speed: " + skyLayer.speed()));
                    context.getSource().sendFeedback(Component.literal("    Transition: " + skyLayer.transition()));
                    // TODO: Biomes, Height, Weathers
                    index++;
                }
            }

            if (activeSkyboxes.isEmpty()) {
                context.getSource().sendFeedback(Component.literal("No skies enabled..."));
            }

            return Command.SINGLE_SUCCESS;
        }));
    }
}
