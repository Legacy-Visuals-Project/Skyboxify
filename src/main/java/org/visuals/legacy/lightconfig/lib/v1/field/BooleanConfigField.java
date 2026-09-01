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

package org.visuals.legacy.lightconfig.lib.v1.field;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.ornithemc.osl.text.api.TextComponent;
import net.ornithemc.osl.text.impl.TranslatableTextComponent;
import org.visuals.legacy.lightconfig.lib.v1.Config;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigDeserializer;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigSerializer;
import org.visuals.legacy.lightconfig.lib.v1.type.Types;

import java.util.function.Function;

public class BooleanConfigField extends AbstractConfigField<Boolean> {
    public BooleanConfigField(final Config config, final String name, final boolean defaultValue) {
        super(config, name, defaultValue);
    }

    @Override
    public void load(final ConfigDeserializer<?> deserializer) throws Exception {
        final Boolean value = Types.BOOLEAN_TYPE.read(deserializer, this.name);
        if (value == null) {
            throw new Exception("Failed to load value for '" + this.name + "'");
        } else {
            this.setValue(value);
        }
    }

    @Override
    public void save(final ConfigSerializer<?> serializer) {
        Types.BOOLEAN_TYPE.write(serializer, this.name, this.value);
    }

    @Override
    public ButtonWidget createWidget() {
        return this.createWidget(Function::identity);
    }

    public ButtonWidget createWidget(final Runnable onClick) {
        final String translationKey = this.getTranslationKey();
        final TextComponent translate = new TranslatableTextComponent(translationKey);
//        return Button.builder(Translations.TEMPLATE.apply(translate, Translations.toggle(this.isEnabled())), (button) -> {
//                    this.toggle();
//                    onClick.run();
//                    button.setMessage(Translations.TEMPLATE.apply(translate, Translations.toggle(this.isEnabled())));
//                })
//                .tooltip(Tooltip.create(Translations.tooltip(translationKey)))
//                .build();
        return null;
    }

    public void toggle() {
        this.setValue(!this.value);
    }

    public boolean isEnabled() {
        return this.value;
    }
}
