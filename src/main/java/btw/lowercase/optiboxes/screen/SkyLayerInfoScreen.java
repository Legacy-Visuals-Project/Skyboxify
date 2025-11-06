package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.screen.widget.SimpleButton;
import btw.lowercase.optiboxes.screen.widget.Text;
import btw.lowercase.optiboxes.skybox.OptiFineSkyLayer;
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

        this.gidgets.add(new Text.Builder(this.title, this.width / 2, 12).centered().build(this.font));

        int y = 12 + (this.font.lineHeight * 3);
        this.gidgets.add(new Text.Builder("    Rotate: " + this.skyLayer.rotate(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Axis: " + this.skyLayer.axis(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Blend: " + this.skyLayer.blend(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Speed: " + this.skyLayer.speed(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Transition: " + this.skyLayer.transition(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Fade: " + this.skyLayer.fade(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Loop: " + this.skyLayer.loop(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Include Biome: " + this.skyLayer.biomeInclusion(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Biomes: " + this.skyLayer.biomes(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Heights: " + this.skyLayer.heights(), this.width / 2, y).centered().build(this.font));
        y += this.font.lineHeight + 2;
        this.gidgets.add(new Text.Builder("    Weather Conditions: " + this.skyLayer.weatherConditions(), this.width / 2, y).centered().build(this.font));

        this.gidgets.add(new SimpleButton(
                CommonComponents.GUI_BACK,
                (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                this.height - SimpleButton.DEFAULT_HEIGHT - 4,
                (button) -> this.onClose()
        ));
    }
}