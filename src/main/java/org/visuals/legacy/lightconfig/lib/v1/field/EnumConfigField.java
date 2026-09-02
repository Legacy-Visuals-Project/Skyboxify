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
import org.visuals.legacy.lightconfig.lib.v1.Translations;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigDeserializer;
import org.visuals.legacy.lightconfig.lib.v1.serialization.ConfigSerializer;
import org.visuals.legacy.lightconfig.lib.v1.type.Type;
import org.visuals.legacy.lightconfig.lib.v1.type.Types;
import org.visuals.legacy.lightconfig.lib.v1.util.ScreenUtil;

public class EnumConfigField<T extends Enum<T>> extends AbstractConfigField<T> {
    private final Class<T> enumClass;
    private final Type<T> enumType;

    public EnumConfigField(final Config config, final String name, final T defaultValue) {
        super(config, name, defaultValue);

        final Class<T> enumClass = defaultValue.getDeclaringClass();
        this.enumClass = enumClass;
        this.enumType = Types.enumType(enumClass);
    }

    @Override
    public void load(final ConfigDeserializer<?> deserializer) throws Exception {
        final T value = this.enumType.read(deserializer, this.name);
        if (value == null) {
            throw new Exception("Failed to load value for '" + this.name + "'");
        } else {
            this.setValue(value);
        }
    }

    @Override
    public void save(final ConfigSerializer<?> serializer) {
        this.enumType.write(serializer, this.name, this.value);
    }

    @Override
    public ButtonWidget createWidget() {
        final String translationKey = this.getTranslationKey();
        final TextComponent translate = new TranslatableTextComponent(translationKey);
        return ScreenUtil.button(getDisplayText(translate, translationKey), button -> {
                    T[] constants = this.enumClass.getEnumConstants();
                    T next = constants[(this.value.ordinal() + 1) % constants.length];
                    this.setValue(next);
                    button.message = getDisplayText(translate, translationKey).buildFormattedString();
                })
//                .tooltip(Tooltip.create(Translations.tooltip(translationKey)))
                .build();
    }

    private TextComponent getDisplayText(final TextComponent translate, final String translationKey) {
        return Translations.TEMPLATE.apply(translate, new TranslatableTextComponent(translationKey + '.' + this.getValue().name()));
    }
}
