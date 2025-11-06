package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.screen.widget.Gidget;
import btw.lowercase.optiboxes.screen.widget.SimpleButton;
import btw.lowercase.optiboxes.screen.widget.Text;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OptiboxesDebugScreen extends Screen {
    private final Screen parent;
    private final List<OptiFineSkybox> skyboxes;
    private final List<Gidget> gidgets;

    private static final Identifier SKYBOX_LOCATION = OptiBoxesClient.locationOrNull("skybox");

    public OptiboxesDebugScreen(Screen parent, List<OptiFineSkybox> skyboxes) {
        super(Component.literal(skyboxes.isEmpty() ? "No skies enabled..." : skyboxes.size() + " Total Active Skyboxes"));
        this.parent = parent;
        this.skyboxes = skyboxes;
        this.gidgets = new ArrayList<>();
    }

    @Override
    protected void init() {
        this.gidgets.clear();
        this.gidgets.add(new Text.Builder(this.font, this.title, this.width / 2, 12).centered().build());

        int index = 0;
        for (OptiFineSkybox skybox : this.skyboxes) {
            SimpleButton.Storage button = new SimpleButton.Storage(
                    Component.literal(skybox.getWorldResourceKey().identifier().toString()),
                    (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                    30 + ((SimpleButton.DEFAULT_HEIGHT + 8) * index),
                    this::buttonClicked
            );
            button.put(SKYBOX_LOCATION, skybox);
            this.gidgets.add(button);
            index++;
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose())
                .pos((this.width / 2) - (Button.DEFAULT_WIDTH / 2), this.height - Button.DEFAULT_HEIGHT - 4)
                .build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        for (Gidget gidget : this.gidgets) {
            gidget.render(guiGraphics, mouseX, mouseY);
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
        if (button instanceof SimpleButton.Storage storage) {
            this.minecraft.setScreen(new SkyboxInfoScreen(this, storage.get(SKYBOX_LOCATION)));
        }
    }
}
