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

package btw.lowercase.skyboxify.api;

import btw.lowercase.skyboxify.config.SkyboxifyConfig;
import btw.lowercase.skyboxify.skybox.SkyboxManager;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import dev.isxander.yacl3.platform.YACLPlatform;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SkyboxifyImpl implements SkyboxifyApi {
    private static final SkyboxifyImpl INSTANCE = new SkyboxifyImpl();

    private final SkyboxManager skyboxManager = new SkyboxManager(this);
    private final Map<Integer, Identifier> dimensionMapping = new Int2ObjectArrayMap<>();
    private final ConfigClassHandler<SkyboxifyConfig> config = ConfigClassHandler.createBuilder(SkyboxifyConfig.class)
            .serializer((config) -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("skyboxify.json"))
                    .build()
            ).build();

    private SkyboxifyImpl() {
        this.registerDimensionMapping(-1, Identifier.withDefaultNamespace("the_nether"));
        this.registerDimensionMapping(0, Identifier.withDefaultNamespace("overworld"));
        this.registerDimensionMapping(1, Identifier.withDefaultNamespace("the_end"));
        this.registerDimensionMapping(4, Identifier.fromNamespaceAndPath("aether", "the_aether"));
        this.registerDimensionMapping(7, Identifier.fromNamespaceAndPath("twilightforest", "twilight_forest"));
    }

    public static SkyboxifyApi getInstance() {
        return INSTANCE;
    }

    public static SkyboxifyConfig config() {
        return getInstance().getConfig();
    }

    @Override
    public SkyboxifyConfig getConfig() {
        return this.config.instance();
    }

    @Override
    public ConfigClassHandler<SkyboxifyConfig> getConfigHandler() {
        return this.config;
    }

    @Override
    public Screen getConfigScreen(final Screen parent) {
        return YetAnotherConfigLib.create(this.config, (defaults, config, builder) -> {
            final Component title = Component.translatable("options.skyboxify.title");
            builder.title(title);

            final ConfigCategory.Builder category = ConfigCategory.createBuilder();
            category.name(title);
            category.option(option("enabled", defaults.enabled, () -> config.enabled, val -> config.enabled = val));
            category.option(option("renderSunMoon", defaults.renderSunMoon, () -> config.renderSunMoon, val -> config.renderSunMoon = val));
            category.option(option("renderStars", defaults.renderStars, () -> config.renderStars, val -> config.renderStars = val));
            category.option(option("showOverworldForUnknownDimension", defaults.showOverworldForUnknownDimension, () -> config.showOverworldForUnknownDimension, val -> config.showOverworldForUnknownDimension = val));
            category.option(option("debug", defaults.debug, () -> config.debug, val -> config.debug = val));
            builder.category(category.build());

            return builder;
        }).generateScreen(parent);
    }

    private Option<Boolean> option(final String name, final boolean defaultValue, final Supplier<Boolean> getter, final Consumer<Boolean> setter) {
        final String key = "options.skyboxify." + name;
        final String tooltip = key + ".tooltip";
        final Option.Builder<Boolean> builder = Option.createBuilder();
        builder.name(Component.translatable(key));
        builder.description(OptionDescription.of(Component.translatable(tooltip)));
        builder.binding(defaultValue, getter, setter);
        builder.controller(TickBoxControllerBuilderImpl::new);
        return builder.build();
    }

    public static SkyboxManager skyboxManager() {
        return getInstance().getSkyboxManager();
    }

    @Override
    public SkyboxManager getSkyboxManager() {
        return this.skyboxManager;
    }

    @Override
    public Identifier getModernDimension(final int legacyId) {
        return this.dimensionMapping.getOrDefault(legacyId, null);
    }

    @Override
    public void registerDimensionMapping(final int legacyId, final Identifier modernId) {
        if (this.dimensionMapping.containsKey(legacyId)) {
            throw new IllegalArgumentException("Cannot register dimension mapping, world with legacy properties " + legacyId + " is already taken by \"" + dimensionMapping.get(legacyId) + "\"!");
        }

        if (this.dimensionMapping.containsValue(modernId)) {
            int currentId = 0;
            for (final int key : this.dimensionMapping.keySet()) {
                if (Objects.equals(this.dimensionMapping.get(key), modernId)) {
                    currentId = key;
                    break;
                }
            }

            throw new IllegalArgumentException("Cannot register dimension mapping, world \"" + modernId + "\" is already mapped to legacy properties " + currentId);
        }

        this.dimensionMapping.put(legacyId, modernId);
    }
}
