package btw.lowercase.optiboxes.screens;

import btw.lowercase.optiboxes.skybox.SkyboxManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class SkyLayerListScreen extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private Screen lastScreen;
    private final ResourceKey<Level> resourceKey;
    private SkyLayerListWidget list;

    public SkyLayerListScreen(Screen lastScreen, ResourceKey<Level> resourceKey) {
        super(Component.literal("Sky Layers"));
        this.lastScreen = lastScreen;
        this.resourceKey = resourceKey;
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
        this.list = this.layout.addToContents(new SkyLayerListWidget(this.minecraft, this.width, this.layout, SkyboxManager.INSTANCE.getSkiesFor(this.resourceKey)));
    }

    @SuppressWarnings("DataFlowIssue")
    private void addFooter() {
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (button) -> this.minecraft.setScreen(this.lastScreen)).build(), LayoutSettings::alignHorizontallyLeft);
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (button) -> this.minecraft.setScreen(null)).build(), LayoutSettings::alignHorizontallyRight);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
    }
}
