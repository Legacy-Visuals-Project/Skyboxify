package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.screen.widget.Gidget;
import btw.lowercase.optiboxes.screen.widget.SimpleButton;
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

public class OptiboxesDebugScreen extends Screen {
    private final Screen parent;
    private final List<OptiFineSkybox> skyboxes;
    private final List<Gidget> gidgets;

    private static final Identifier SKYBOX_LOCATION = OptiBoxesClient.locationOrNull("skybox");

    public OptiboxesDebugScreen(Screen parent, List<OptiFineSkybox> skyboxes) {
        super(Component.empty());
        this.parent = parent;
        this.skyboxes = skyboxes;
        this.gidgets = new ArrayList<>();
    }

    @Override
    protected void init() {
        int index = 0;
        this.gidgets.clear();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose())
                .pos((this.width / 2) - (Button.DEFAULT_WIDTH / 2), this.height - Button.DEFAULT_HEIGHT - 4)
                .build());
        for (OptiFineSkybox skybox : this.skyboxes) {
            SimpleButton.DataHolder<OptiFineSkybox> button = new SimpleButton.DataHolder<>(
                    Component.literal(skybox.getWorldResourceKey().identifier().toString()),
                    (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                    30 + ((SimpleButton.DEFAULT_HEIGHT + 8) * index),
                    this::buttonClicked
            );
            button.put(SKYBOX_LOCATION, skybox);
            this.gidgets.add(button);
            index++;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.skyboxes.isEmpty() ? "No skies enabled..." : this.skyboxes.size() + " Total Active Skyboxes", this.width / 2, 12, ARGB.white(1.0F));
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
            this.minecraft.setScreen(new SkyboxInfoScreen(this, (OptiFineSkybox) holder.get(SKYBOX_LOCATION)));
        }
    }
}
