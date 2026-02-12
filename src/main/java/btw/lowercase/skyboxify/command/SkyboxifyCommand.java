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
import btw.lowercase.skyboxify.skybox.Skybox;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SkyboxifyCommand extends LiteralArgumentBuilder<FabricClientCommandSource> {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public SkyboxifyCommand(String name) {
		super(name);

		final Minecraft minecraft = Minecraft.getInstance();
		executes((context) -> {
			minecraft.schedule(() -> minecraft.setScreen(Skyboxify.getConfig().getConfigScreen(minecraft.screen)));
			return Command.SINGLE_SUCCESS;
		});

		then(ClientCommandManager.literal("debug").executes((context) -> {
			minecraft.schedule(() -> minecraft.setScreen(new SkyboxListScreen(minecraft.screen, SkyboxManager.getLoadedSkyboxes())));
			return Command.SINGLE_SUCCESS;
		}));

		then(ClientCommandManager.literal("dump").executes((context) -> {
			if (!Skyboxify.DEBUG_FOLDER.exists()) {
				Skyboxify.DEBUG_FOLDER.mkdirs();
			}

			for (final Skybox skybox : SkyboxManager.getLoadedSkyboxes()) {
				final JsonElement element = Skybox.CODEC.encode(skybox, JsonOps.INSTANCE, null).getOrThrow((message) -> {
					context.getSource().sendFeedback(Component.literal(message).withStyle(ChatFormatting.RED));
					return null;
				});
				if (element == null) {
					return 0;
				}

				try {
					final File outputFolder = Skyboxify.DEBUG_FOLDER.toPath().resolve(skybox.getPackName().replaceAll("/", "+").replaceAll(" ", "_")).resolve(skybox.getWorldKey().location().getPath()).toFile();
					if (!outputFolder.exists()) {
						outputFolder.mkdirs();
					}

					Files.writeString(outputFolder.toPath().resolve("output.json"), GSON.toJson(element));
				} catch (final IOException exception) {
					context.getSource().sendFeedback(Component.literal("Failed to save Active Skybox's!").withStyle(ChatFormatting.RED));
					context.getSource().sendFeedback(Component.literal(exception.toString()).withStyle(ChatFormatting.RED));
					return 0;
				}
			}

			context.getSource().sendFeedback(Component.literal("Active Skybox's have been dumped to " + Skyboxify.DEBUG_FOLDER).withStyle(ChatFormatting.GREEN));
			return Command.SINGLE_SUCCESS;
		}));
	}
}
