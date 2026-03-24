/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025-2026 lowercasebtw
 * Copyright (C) 2025-2026 Contributors to the project retain their copyright
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

import btw.lowercase.skyboxify.SkyboxifyInfo;
import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.screen.SkyboxListScreen;
import btw.lowercase.skyboxify.skybox.SkyLayer;
import btw.lowercase.skyboxify.skybox.Skybox;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SkyboxifyCommand extends LiteralArgumentBuilder<FabricClientCommandSource> implements Command<FabricClientCommandSource> {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public SkyboxifyCommand() {
		super("skyboxify");
		executes(this)
				.then(ClientCommands.literal("debug").executes(new Debug()))
				.then(ClientCommands.literal("dump").executes(new Dump()));
	}

	@Override
	public int run(final CommandContext<FabricClientCommandSource> context) {
		final Minecraft minecraft = Minecraft.getInstance();
		minecraft.schedule(() -> minecraft.setScreen(SkyboxifyImpl.config().getConfigScreen(minecraft.screen)));
		return Command.SINGLE_SUCCESS;
	}

	private static class Debug implements Command<FabricClientCommandSource> {
		@Override
		public int run(final CommandContext<FabricClientCommandSource> context) {
			final Minecraft minecraft = Minecraft.getInstance();
			minecraft.schedule(() -> minecraft.setScreen(new SkyboxListScreen(minecraft.screen, SkyboxifyImpl.skyboxManager().getLoadedSkies())));
			return Command.SINGLE_SUCCESS;
		}
	}

	private static class Dump implements Command<FabricClientCommandSource> {
		@SneakyThrows
		@Override
		public int run(final CommandContext<FabricClientCommandSource> context) {
			if (!Files.isDirectory(SkyboxifyInfo.DEBUG_FOLDER)) {
				Files.delete(SkyboxifyInfo.DEBUG_FOLDER);
			}

			if (!Files.exists(SkyboxifyInfo.DEBUG_FOLDER)) {
				Files.createDirectories(SkyboxifyInfo.DEBUG_FOLDER);
			}

			for (final Skybox skybox : SkyboxifyImpl.skyboxManager().getLoadedSkies()) {
				final Path packFolder = SkyboxifyInfo.DEBUG_FOLDER.resolve(skybox.getPackName().replaceAll("/", "+").replaceAll(" ", "_"));
				if (!Files.exists(packFolder)) {
					Files.createDirectory(packFolder);
				}

				final Identifier dimension = skybox.getDimension().identifier();
				final Path dimensionFolder = packFolder.resolve(dimension.getNamespace()).resolve(dimension.getPath());
				if (!Files.exists(dimensionFolder)) {
					Files.createDirectories(dimensionFolder);
				}

				for (final SkyLayer layer : skybox.getLayers()) {
					try {
						String id = Path.of(layer.properties().getPath()).getFileName().toString();
						if (id.endsWith(".properties")) {
							id = id.substring(0, id.length() - 11);
						}

						final JsonElement element = SkyLayer.CODEC.encode(layer, JsonOps.INSTANCE, null).getOrThrow((message) -> {
							error(context, message);
							return null;
						});
						if (element == null) {
							error(context, "Failed to encode layer \"" + id + "\"");
							continue;
						}

						Files.writeString(dimensionFolder.resolve(id + ".json"), GSON.toJson(element));
					} catch (final IOException exception) {
						error(context, "Failed to encode layer " + layer.properties());
						error(context, exception.toString());
					}
				}
			}

			success(context, "Active Skybox's have been dumped to " + SkyboxifyInfo.DEBUG_FOLDER);
			return Command.SINGLE_SUCCESS;
		}
	}

	private static void error(final CommandContext<FabricClientCommandSource> context, final String message) {
		context.getSource().sendFeedback(Component.literal(message).withStyle(ChatFormatting.RED));
	}

	private static void success(final CommandContext<FabricClientCommandSource> context, final String message) {
		context.getSource().sendFeedback(Component.literal(message).withStyle(ChatFormatting.GREEN));
	}
}
