package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.screen.widget.Gidget;
import btw.lowercase.optiboxes.screen.widget.SimpleButton;
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
    private final List<Gidget> gidgets;

    private static final Identifier SKYLAYER_LOCATION = OptiBoxesClient.locationOrNull("skylayer");
    private static final Identifier INDEX_LOCATION = OptiBoxesClient.locationOrNull("index");

    public SkyboxInfoScreen(Screen parent, OptiFineSkybox skybox) {
        super(Component.empty());
        this.parent = parent;
        this.skybox = skybox;
        this.gidgets = new ArrayList<>();
    }

    @Override
    protected void init() {
        int index = 0;
        this.gidgets.clear();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose())
                .pos((this.width / 2) - (Button.DEFAULT_WIDTH / 2), this.height - Button.DEFAULT_HEIGHT - 4)
                .build());
        for (OptiFineSkyLayer layer : this.skybox.getLayers()) {
            SimpleButton.DataHolder<Object> button = new SimpleButton.DataHolder<>(
                    Component.literal(index + " - " + layer.source()),
                    (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                    36 + ((SimpleButton.DEFAULT_HEIGHT + 8) * index),
                    this::buttonClicked
            );
            button.put(SKYLAYER_LOCATION, layer);
            button.put(INDEX_LOCATION, index);
            this.gidgets.add(button);
            index++;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.skybox.getWorldResourceKey().identifier().toString(), this.width / 2, 12, ARGB.white(1.0F));
        guiGraphics.drawCenteredString(this.font, this.skybox.getLayers().size() + " layers", this.width / 2, 12 + this.font.lineHeight, ARGB.white(1.0F));
        // TODO: isActive | parent button color
        for (Gidget gidget : this.gidgets) {
            if (gidget instanceof SimpleButton button) {
                button.render(guiGraphics, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        for (Gidget gidget : this.gidgets) {
            if (gidget instanceof SimpleButton button && button.isInside(event.x(), event.y())) {
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
            OptiFineSkyLayer skyLayer = (OptiFineSkyLayer) holder.get(SKYLAYER_LOCATION);
            int index = (Integer) holder.get(INDEX_LOCATION);
            this.minecraft.setScreen(new SkyLayerInfoScreen(this, skyLayer, index));
        }
    }
}

