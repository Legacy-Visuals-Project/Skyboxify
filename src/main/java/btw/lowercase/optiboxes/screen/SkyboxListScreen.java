package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.screen.widget.SimpleButton;
import btw.lowercase.optiboxes.screen.widget.Text;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SkyboxListScreen extends DebugScreen {
    private final List<OptiFineSkybox> skyboxes;

    public SkyboxListScreen(Screen parent, List<OptiFineSkybox> skyboxes) {
        super(Component.literal(skyboxes.isEmpty() ? "No skies enabled..." : skyboxes.size() + " Total Active Skyboxes"), parent);
        this.skyboxes = skyboxes;
    }

    @Override
    protected void init() {
        super.init();

        // TODO: isActive | button color
        this.gidgets.add(new Text.Builder(this.font, this.title, this.width / 2, 12).centered().build());

        int index = 0;
        for (OptiFineSkybox skybox : this.skyboxes) {
            this.gidgets.add(new SimpleButton(
                    Component.literal(skybox.getWorldResourceKey().identifier().toString()),
                    (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                    30 + ((SimpleButton.DEFAULT_HEIGHT + 8) * index),
                    (button) -> this.minecraft.setScreen(new SkyLayerListScreen(this, skybox))
            ));
            index++;
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose())
                .pos((this.width / 2) - (Button.DEFAULT_WIDTH / 2), this.height - Button.DEFAULT_HEIGHT - 4)
                .build());
    }
}
