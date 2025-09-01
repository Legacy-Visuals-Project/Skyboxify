package btw.lowercase.optiboxes.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DimensionsListScreen extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Screen lastScreen;
    private DimensionListWidget list;

    public DimensionsListScreen(Screen lastScreen) {
        super(Component.literal("Dimensions"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        this.addHeader();
        this.addContents();
        this.addFooter();
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    private void addHeader() {
        this.layout.addTitleHeader(this.title, this.font);
    }

    private void addContents() {
        this.list = this.layout.addToContents(new DimensionListWidget(this.minecraft, this.width, this.layout, this::onSelected));
    }

    private void addFooter() {
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).build());
    }

    @SuppressWarnings("DataFlowIssue")
    private void onSelected(@Nullable ResourceKey<Level> resourceKey) {
        if (resourceKey != null) {
            this.minecraft.setScreen(new SkyLayerListScreen(this, resourceKey));
        }
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void onClose() {
        minecraft.setScreen(this.lastScreen);
    }
}
