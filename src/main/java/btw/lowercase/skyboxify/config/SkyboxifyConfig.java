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

package btw.lowercase.skyboxify.config;

import btw.lowercase.skyboxify.SkyboxifyInfo;
import btw.lowercase.skyboxify.utils.FilteringMode;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.visuals.legacy.lightconfig.lib.v1.Config;
import org.visuals.legacy.lightconfig.lib.v1.field.BooleanConfigField;
import org.visuals.legacy.lightconfig.lib.v1.field.EnumConfigField;
import org.visuals.legacy.lightconfig.lib.v1.screen.ConfigScreenBuilder;

public class SkyboxifyConfig extends Config {
    public BooleanConfigField enabled = this.booleanFieldOf("enabled", true);
    public BooleanConfigField renderSky = this.booleanFieldOf("renderSky", true);
    public BooleanConfigField renderSunMoon = this.booleanFieldOf("renderSunMoon", true);
    public BooleanConfigField renderStars = this.booleanFieldOf("renderStars", false);
    public BooleanConfigField showOverworldForUnknownDimension = this.booleanFieldOf("showOverworldForUnknownDimension", true);
    public EnumConfigField<FilteringMode> filteringMode = this.enumFieldOf("filteringMode", FilteringMode.NEAREST);
    public BooleanConfigField debug = this.booleanFieldOf("debug", false);

    public SkyboxifyConfig() {
        super(SkyboxifyInfo.MOD_ID);
    }

    @Override
    public Screen getConfigScreen(@Nullable final Screen parent) {
        final ConfigScreenBuilder builder = ConfigScreenBuilder.builder(this);
        builder.setTitle(new TranslatableText("options.skyboxify.title"));
        return builder.build(parent);
    }
}