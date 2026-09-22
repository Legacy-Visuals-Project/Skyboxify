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

import btw.lowercase.skyboxify.api.SkyboxifyImpl;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import btw.lowercase.skyboxify.skybox.SkyboxResourceListener;
import net.fabricmc.api.ClientModInitializer;
import net.ornithemc.osl.resource.loader.api.client.ClientResourceLoaderEvents;

public final class SkyboxifyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Skyboxify.initialize();

        final SkyboxManager skyboxManager = SkyboxifyImpl.skyboxManager();
//        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(new SkyboxifyCommand()));

        ClientResourceLoaderEvents.INIT_RESOURCE_MANAGER.register(resources -> resources.addReloader(new SkyboxResourceListener(skyboxManager)));
    }
}
