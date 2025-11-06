package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.screen.widget.SimpleButton;
import btw.lowercase.optiboxes.screen.widget.Text;
import btw.lowercase.optiboxes.skybox.OptiFineSkyLayer;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SkyLayerListScreen extends DebugScreen {
    private final OptiFineSkybox skybox;

    public SkyLayerListScreen(Screen parent, OptiFineSkybox skybox) {
        super(Component.literal(skybox.getWorldResourceKey().identifier().toString()), parent);
        this.skybox = skybox;
    }

    @Override
    protected void init() {
        super.init();

        this.gidgets.add(new Text.Builder(this.font, this.title, this.width / 2, 12).centered().build());
        this.gidgets.add(new Text.Builder(this.font, this.skybox.getLayers().size() + " layers", this.width / 2, 12 + this.font.lineHeight).centered().build());

        int index = 0;
        for (OptiFineSkyLayer skyLayer : this.skybox.getLayers()) {
            int cidx = index;
            this.gidgets.add(new SimpleButton(
                    Component.literal(index + " - " + skyLayer.source()),
                    (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                    36 + ((SimpleButton.DEFAULT_HEIGHT + 8) * index),
                    (button) -> this.minecraft.setScreen(new SkyLayerInfoScreen(this, skyLayer, cidx))
            ));
            index++;
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose())
                .pos((this.width / 2) - (Button.DEFAULT_WIDTH / 2), this.height - Button.DEFAULT_HEIGHT - 4)
                .build());
    }
}

