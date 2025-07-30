package btw.lowercase.lightconfig.lib.v1.field;

import btw.lowercase.lightconfig.lib.ConfigTranslate;
import btw.lowercase.lightconfig.lib.v1.Config;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Optional;

public class EnumConfigField<T extends Enum<T>> extends GenericConfigField<T> {
    private final Class<T> enumClazz;

    public EnumConfigField(Config config, String name, T defaultValue, Class<T> clazz) {
        super(config, name, defaultValue);
        this.enumClazz = clazz;
    }

    @Override
    public void load(JsonObject object) throws Exception {
        if (!object.has(this.name)) {
            throw new Exception("Failed to load value for '" + this.name + "', object didn't contain a value for it.");
        } else {
            final JsonElement element = object.get(this.name);
            if (!element.isJsonPrimitive() || (element instanceof final JsonPrimitive primitive && !primitive.isString())) {
                throw new Exception("Failed to load value for '" + this.name + "', type does not match.");
            } else {
                Optional<T> opt = Arrays.stream(this.enumClazz.getEnumConstants())
                        .filter(cons -> cons.name().equals(element.getAsString()))
                        .findFirst();
                this.setValue(opt.orElseThrow(() -> new Exception("Failed to load value for '" + this.name + "', invalid enum value.")));
            }
        }
    }

    @Override
    public void save(JsonObject object) {
        object.addProperty(this.name, this.value.name());
    }

    @Override
    public AbstractWidget createWidget() {
        final String translationKey = this.getTranslationKey();
        final Component translate = Component.translatable(translationKey);
        return Button.builder(getDisplayText(translate, translationKey), (button) -> {
                    T[] constants = this.enumClazz.getEnumConstants();
                    T next = constants[(this.value.ordinal() + 1) % constants.length];
                    this.setValue(next);
                    button.setMessage(getDisplayText(translate, translationKey));
                })
                .tooltip(Tooltip.create(ConfigTranslate.tooltip(translationKey)))
                .build();
    }

    private Component getDisplayText(final Component translate, final String translationKey) {
        return ConfigTranslate.TEMPLATE.apply(translate, Component.translatable(translationKey + '.' + this.getValue().name()));
    }
}
