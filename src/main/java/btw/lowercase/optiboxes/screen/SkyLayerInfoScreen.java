package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.screen.widget.Text;
import btw.lowercase.optiboxes.skybox.OptiFineSkyLayer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SkyLayerInfoScreen extends DebugScreen {
    private final OptiFineSkyLayer skyLayer;

    public SkyLayerInfoScreen(Screen parent, OptiFineSkyLayer skyLayer, int index) {
        super(Component.literal(index + " - " + skyLayer.source().toString()), parent);
        this.skyLayer = skyLayer;
    }

    @Override
    protected void init() {
        super.init();

        this.gidgets.add(new Text.Builder(this.font, this.title, this.width / 2, 12).centered().build());

        int y = 12 + (this.font.lineHeight * 3);
        this.gidgets.add(new Text.Builder(this.font, "    Rotate: " + this.skyLayer.rotate(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Axis: " + this.skyLayer.axis(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Blend: " + this.skyLayer.blend(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Speed: " + this.skyLayer.speed(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Transition: " + this.skyLayer.transition(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Fade: " + this.skyLayer.fade(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Loop: " + this.skyLayer.loop(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Include Biome: " + this.skyLayer.biomeInclusion(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Biomes: " + this.skyLayer.biomes(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Heights: " + this.skyLayer.heights(), this.width / 2, y).centered().build());
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder(this.font, "    Weather Conditions: " + this.skyLayer.weatherConditions(), this.width / 2, y).centered().build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose())
                .pos((this.width / 2) - (Button.DEFAULT_WIDTH / 2), this.height - Button.DEFAULT_HEIGHT - 4)
                .build());
    }
}