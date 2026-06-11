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

package btw.lowercase.skyboxify.skybox;

//? =1.21.4 {
/*import btw.lowercase.skyboxify.Skyboxify;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.client.renderer.ShaderDefines;
*///? }

@Deprecated
public final class SkyboxRenderer {
    //? =1.21.4 {
	/*private static final ShaderProgram CUSTOM_SKYBOX_SHADER;

	static {
		CUSTOM_SKYBOX_SHADER = new ShaderProgram(
				Skyboxify.locationOrNull("core/custom_skybox"),
				DefaultVertexFormat.POSITION_TEX,
				ShaderDefines.EMPTY
		);

		CoreShaders.getProgramsToPreload().add(CUSTOM_SKYBOX_SHADER);
	}
	*///? }
}