package btw.lowercase.optiboxes;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen {
    private final Screen parent;

    public TestScreen(Screen screen) {
        super(Component.literal("Test"));
        this.parent = screen;
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void onClose() {
        minecraft.setScreen(this.parent);
    }
}
