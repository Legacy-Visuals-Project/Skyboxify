package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.screen.widget.Gidget;
import btw.lowercase.optiboxes.screen.widget.ScrollableList;
import btw.lowercase.optiboxes.screen.widget.SimpleButton;
import btw.lowercase.optiboxes.screen.widget.Text;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SkyboxListScreen extends DebugScreen {
    private final List<OptiFineSkybox> skyboxes;
    private ScrollableList scrollableList;

    public SkyboxListScreen(Screen parent, List<OptiFineSkybox> skyboxes) {
        super(Component.literal(skyboxes.isEmpty() ? "No skies enabled..." : skyboxes.size() + " Total Active Skyboxes"), parent);
        this.skyboxes = skyboxes;
    }

    @Override
    protected void init() {
        super.init();

        // TODO: isActive | button color
        this.gidgets.add(new Text.Builder(this.title, this.width / 2, 12).centered().build(this.font));

        List<Gidget> scrollableListGidgets = new ArrayList<>();
        int index = 0;
        for (OptiFineSkybox skybox : this.skyboxes) {
            scrollableListGidgets.add(new SimpleButton(
                    Component.literal(skybox.getWorldResourceKey().identifier().toString()),
                    (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                    ((SimpleButton.DEFAULT_HEIGHT + SimpleButton.DEFAULT_PADDING) * index),
                    (button) -> this.minecraft.setScreen(new SkyLayerListScreen(this, skybox))
            ));
            index++;
        }

        int pad = 20 + font.lineHeight;
        this.gidgets.add(new ScrollableList(scrollableListGidgets, 0, pad, this.width, this.height - pad - SimpleButton.DEFAULT_HEIGHT - 8));

        this.gidgets.add(new SimpleButton(
                CommonComponents.GUI_DONE,
                (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                this.height - SimpleButton.DEFAULT_HEIGHT - 4,
                (button) -> this.onClose()
        ));
    }
}
