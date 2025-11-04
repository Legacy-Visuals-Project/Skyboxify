package btw.lowercase.optiboxes.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.visuals.legacy.lightconfig.lib.v1.ConfigTranslate;
import org.visuals.legacy.lightconfig.lib.v1.screen.InternalConfigScreen;

public class OptiBoxesConfigScreen extends InternalConfigScreen {
    public OptiBoxesConfigScreen(Component title, OptiBoxesConfig config, Screen parent) {
        super(title, config, parent);
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    protected void init() {
        OptiBoxesConfig optiBoxesConfig = (OptiBoxesConfig) this.config;

        HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);
        LinearLayout linearLayout = layout.addToHeader(LinearLayout.vertical().spacing(8));
        linearLayout.addChild(new StringWidget(this.getTitle(), this.font), LayoutSettings::alignHorizontallyCenter);

        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter().alignVerticallyMiddle();
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(2);
        // Didn't iterate fields here because I wanted custom order
        rowHelper.addChild(optiBoxesConfig.enabled.createWidget());
        rowHelper.addChild(optiBoxesConfig.showOverworldForUnknownDimension.createWidget());
        rowHelper.addChild(optiBoxesConfig.processOptiFine.createWidget(() -> this.minecraft.reloadResourcePacks()));
        rowHelper.addChild(optiBoxesConfig.processMCPatcher.createWidget(() -> this.minecraft.reloadResourcePacks()));
        rowHelper.addChild(optiBoxesConfig.renderSunMoon.createWidget());
        rowHelper.addChild(optiBoxesConfig.renderStars.createWidget());
        rowHelper.addChild(optiBoxesConfig.ignoreBrokenSkies.createWidget(() -> this.minecraft.reloadResourcePacks()));
        layout.addToContents(gridLayout);

        GridLayout footerGridLayout = new GridLayout();
        footerGridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper footerRowHelper = footerGridLayout.createRowHelper(2);
        footerRowHelper.addChild(Button.builder(ConfigTranslate.RESET, (button) -> this.reset()).width(125).build());
        footerRowHelper.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(125).build());
        layout.addToFooter(footerGridLayout);

        layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();
    }

    private void reset() {
        this.config.reset();
        this.minecraft.reloadResourcePacks();
    }
}
