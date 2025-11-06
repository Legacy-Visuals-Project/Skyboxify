package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.skybox.OptiFineSkyLayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

public class SkyLayerInfoScreen extends Screen {
    private final Screen parent;
    private final OptiFineSkyLayer skyLayer;

    public SkyLayerInfoScreen(Screen parent, OptiFineSkyLayer skyLayer) {
        super(Component.empty());
        this.parent = parent;
        this.skyLayer = skyLayer;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose())
                .pos((this.width / 2) - (Button.DEFAULT_WIDTH / 2), this.height - Button.DEFAULT_HEIGHT - 4)
                .build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, "TODO", this.width / 2, 12, ARGB.white(1.0F));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}

// context.getSource().sendFeedback(Component.literal("  #" + index + " (" + skyLayer.source() + ")"));
// context.getSource().sendFeedback(Component.literal("    Rotate: " + skyLayer.rotate()));
// context.getSource().sendFeedback(Component.literal("    Axis: " + skyLayer.axis()));
// context.getSource().sendFeedback(Component.literal("    Blend: " + skyLayer.blend()));
// context.getSource().sendFeedback(Component.literal("    Speed: " + skyLayer.speed()));
// context.getSource().sendFeedback(Component.literal("    Transition: " + skyLayer.transition()));
// context.getSource().sendFeedback(Component.literal("    Fade: " + skyLayer.fade()));
// context.getSource().sendFeedback(Component.literal("    Loop: " + skyLayer.loop()));
// context.getSource().sendFeedback(Component.literal("    Include Biome: " + skyLayer.biomeInclusion()));
// context.getSource().sendFeedback(Component.literal("    Biomes: " + skyLayer.biomes()));
// context.getSource().sendFeedback(Component.literal("    Heights: " + skyLayer.heights()));
// context.getSource().sendFeedback(Component.literal("    Weather Conditions: " + skyLayer.weatherConditions()));