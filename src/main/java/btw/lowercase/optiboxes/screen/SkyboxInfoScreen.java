package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.skybox.OptiFineSkyLayer;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SkyboxInfoScreen extends Screen {
    private final Screen parent;
    private final OptiFineSkybox skybox;
    private final List<SimpleButton> buttons;

    private static final Identifier SKYLAYER_LOCATION = OptiBoxesClient.locationOrNull("skylayer");

    public SkyboxInfoScreen(Screen parent, OptiFineSkybox skybox) {
        super(Component.empty());
        this.parent = parent;
        this.skybox = skybox;
        this.buttons = new ArrayList<>();
    }

    @Override
    protected void init() {
        int index = 0;
        this.buttons.clear();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose())
                .pos((this.width / 2) - (Button.DEFAULT_WIDTH / 2), this.height - Button.DEFAULT_HEIGHT - 4)
                .build());
        for (OptiFineSkyLayer layer : this.skybox.getLayers()) {
            SimpleButton.DataHolder<OptiFineSkyLayer> button = new SimpleButton.DataHolder<>(
                    Component.literal(index + " - " + layer.source().getPath()),
                    (this.width / 2) - (SimpleButton.WIDTH / 2),
                    36 + ((SimpleButton.HEIGHT + 8) * index),
                    this::buttonClicked
            );
            button.put(SKYLAYER_LOCATION, layer);
            this.buttons.add(button);
            index++;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.skybox.getWorldResourceKey().identifier().toString(), this.width / 2, 12, ARGB.white(1.0F));
        guiGraphics.drawCenteredString(this.font, this.skybox.getLayers().size() + " layers", this.width / 2, 12 + this.font.lineHeight, ARGB.white(1.0F));
        // TODO: isActive | parent button color
        for (SimpleButton button : this.buttons) {
            button.render(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        for (SimpleButton button : this.buttons) {
            if (button.isInside(event.x(), event.y())) {
                button.onClick().accept(button);
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void buttonClicked(SimpleButton button) {
        if (button instanceof SimpleButton.DataHolder<?> holder) {
            this.minecraft.setScreen(new SkyLayerInfoScreen(this, (OptiFineSkyLayer) holder.get(SKYLAYER_LOCATION)));
        }
    }
}

