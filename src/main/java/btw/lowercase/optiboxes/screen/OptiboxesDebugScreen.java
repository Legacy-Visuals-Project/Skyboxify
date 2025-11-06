package btw.lowercase.optiboxes.screen;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OptiboxesDebugScreen extends Screen {
    private final Screen parent;
    private final List<OptiFineSkybox> skyboxes;
    private final List<Button> buttons;

    private static final Identifier SKYBOX_LOCATION = OptiBoxesClient.locationOrNull("skybox");

    public OptiboxesDebugScreen(Screen parent, List<OptiFineSkybox> skyboxes) {
        super(Component.empty());
        this.parent = parent;
        this.skyboxes = skyboxes;
        this.buttons = new ArrayList<>();
    }

    @Override
    protected void init() {
        int index = 0;
        this.buttons.clear();
        for (OptiFineSkybox skybox : this.skyboxes) {
            Button.DataHolder<OptiFineSkybox> button = new Button.DataHolder<>(Component.literal(skybox.getWorldResourceKey().identifier().toString()), (this.width / 2) - (Button.WIDTH / 2), 30 + ((Button.HEIGHT + 8) * index), this::buttonClicked);
            button.put(SKYBOX_LOCATION, skybox);
            this.buttons.add(button);
            index++;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.skyboxes.isEmpty() ? "No skies enabled..." : this.skyboxes.size() + " Total Active Skyboxes", this.width / 2, 16, ARGB.white(1.0F));
        for (Button button : this.buttons) {
            button.render(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean isDoubleClick) {
        for (Button button : this.buttons) {
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

    private void buttonClicked(Button button) {
        if (button instanceof Button.DataHolder<?> holder) {
            OptiFineSkybox skybox = (OptiFineSkybox) holder.get(SKYBOX_LOCATION);
            System.out.println("Got skybox " + skybox.getWorldResourceKey());
        }
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
