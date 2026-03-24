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

package btw.lowercase.skyboxify;

import btw.lowercase.skyboxify.command.SkyboxifyCommand;
import btw.lowercase.skyboxify.skybox.SkyboxResourceHelper;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.server.packs.PackType;

@Entrypoint
public final class SkyboxifyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Skyboxify.initialize();
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(new SkyboxifyCommand()));
		//? >=1.21.10 {
		net.fabricmc.fabric.api.resource.v1.ResourceLoader.get(PackType.CLIENT_RESOURCES)
                //? >=26.1 {
                .registerReloadListener
                //? } else {
                /*.registerReloader
                *///? }
                (Skyboxify.locationOrNull("skybox_reader"), new SkyboxResourceHelper());
		//?} else {
		/*net.fabricmc.fabric.api.resource.ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SkyboxResourceHelper());
		 *///?}
	}
}
