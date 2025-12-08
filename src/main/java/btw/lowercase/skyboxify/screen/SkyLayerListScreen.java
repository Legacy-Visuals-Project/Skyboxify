/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 Contributors to the project retain their copyright
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * "MINECRAFT" LINKING EXCEPTION TO THE GPL
 */

package btw.lowercase.skyboxify.screen;

import btw.lowercase.skyboxify.screen.widget.Gidget;
import btw.lowercase.skyboxify.screen.widget.ScrollableList;
import btw.lowercase.skyboxify.screen.widget.SimpleButton;
import btw.lowercase.skyboxify.screen.widget.Text;
import btw.lowercase.skyboxify.skybox.OptiFineSkyLayer;
import btw.lowercase.skyboxify.skybox.OptiFineSkybox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SkyLayerListScreen extends DebugScreen {
    private final OptiFineSkybox skybox;

    public SkyLayerListScreen(Screen parent, OptiFineSkybox skybox) {
        super(Component.literal(
                //? >=1.21.11 {
                /*skybox.getWorldResourceKey().identifier().toString()
                *///?} else {
                skybox.getWorldResourceKey().location().toString()
                 //?}
        ), parent);
        this.skybox = skybox;
    }

    @Override
    protected void init() {
        super.init();

        this.gidgets.add(new Text.Builder(this.title.getString() + " (" + this.skybox.getLayers().size() + " layer(s)" + ")", this.width / 2, 12).centered().build(this.font));

        List<Gidget> scrollableListGidgets = new ArrayList<>();
        int index = 0;
        for (OptiFineSkyLayer skyLayer : this.skybox.getLayers()) {
            int cidx = index;
            scrollableListGidgets.add(new SimpleButton(
                    Component.literal(index + " - " + skyLayer.source()),
                    (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                    ((SimpleButton.DEFAULT_HEIGHT + SimpleButton.DEFAULT_PADDING) * index),
                    (button) -> this.minecraft.setScreen(new SkyLayerInfoScreen(this, skyLayer, cidx))
            ));
            index++;
        }

        int pad = 20 + font.lineHeight;
        this.gidgets.add(new ScrollableList(scrollableListGidgets, 0, pad, this.width, this.height - pad - SimpleButton.DEFAULT_HEIGHT - 8));

        this.gidgets.add(new SimpleButton(
                CommonComponents.GUI_BACK,
                (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                this.height - SimpleButton.DEFAULT_HEIGHT - 4,
                (button) -> this.onClose()
        ));
    }
}

