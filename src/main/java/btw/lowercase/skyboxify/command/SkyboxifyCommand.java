/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 Contributors to the project retain their copyright
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * "MINECRAFT" LINKING EXCEPTION TO THE GPL
 */

package btw.lowercase.skyboxify.command;

import btw.lowercase.skyboxify.Skyboxify;
import btw.lowercase.skyboxify.screen.SkyboxListScreen;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public class SkyboxifyCommand extends LiteralArgumentBuilder<FabricClientCommandSource> {
    public SkyboxifyCommand(String name) {
        super(name);

        Minecraft minecraft = Minecraft.getInstance();
        executes((context) -> {
            minecraft.schedule(() -> minecraft.setScreen(Skyboxify.getConfig().getConfigScreen(minecraft.screen)));
            return Command.SINGLE_SUCCESS;
        });

        then(ClientCommandManager.literal("debug").executes((context) -> {
            minecraft.schedule(() -> minecraft.setScreen(new SkyboxListScreen(minecraft.screen, SkyboxManager.getLoadedSkyboxes())));
            return Command.SINGLE_SUCCESS;
        }));
    }
}
