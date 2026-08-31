/**
 * LightConfig
 * A config library.
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 mixces
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

package org.visuals.legacy.lightconfig.lib.v1;

import net.minecraft.network.chat.Component;

import java.util.function.BiFunction;

public final class Translations {
    public static final Component RESET = Component.translatable("options.reset");
    public static final BiFunction<Component, Component, Component> TEMPLATE = (a, b) -> Component.translatable("options.template", a, b);
    public static final Component ON = Component.translatable("options.on");
    public static final Component OFF = Component.translatable("options.off");

    private Translations() {
    }

    public static Component tooltip(final String translate) {
        return Component.translatable(translate + ".tooltip");
    }

    public static Component toggle(final boolean value) {
        return value ? ON : OFF;
    }
}
