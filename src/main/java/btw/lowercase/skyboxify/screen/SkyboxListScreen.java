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
import btw.lowercase.skyboxify.skybox.OptiFineSkybox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class SkyboxListScreen extends DebugScreen {
    private final List<OptiFineSkybox> skyboxes;

    public SkyboxListScreen(Screen parent, List<OptiFineSkybox> skyboxes) {
        super(Component.literal(skyboxes.isEmpty() ? "No skies enabled..." : skyboxes.size() + " Total Active Skyboxes"), parent);
        this.skyboxes = skyboxes;
    }

    @Override
    protected void init() {
        super.init();

        // TODO: isActive | button color
        this.gidgets.add(new Text.Builder(this.title, this.width / 2, 12).centered().build(this.font));

        List<Gidget> scrollableListGidgets = new ArrayList<>();
        int index = 0;
        for (OptiFineSkybox skybox : this.skyboxes) {
            scrollableListGidgets.add(new SimpleButton(
                    Component.literal(
                            //? >=1.21.11 {
                            /*skybox.getWorldResourceKey().identifier().toString()
                             *///?} else {
                            skybox.getWorldResourceKey().location().toString()
                            //?}
                    ),
                    (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                    ((SimpleButton.DEFAULT_HEIGHT + SimpleButton.DEFAULT_PADDING) * index),
                    (button) -> this.minecraft.setScreen(new SkyLayerListScreen(this, skybox))
            ));
            index++;
        }

        int pad = 20 + font.lineHeight;
        this.gidgets.add(new ScrollableList(scrollableListGidgets, 0, pad, this.width, this.height - pad - SimpleButton.DEFAULT_HEIGHT - 8));

        this.gidgets.add(new SimpleButton(
                CommonComponents.GUI_DONE,
                (this.width / 2) - (SimpleButton.DEFAULT_WIDTH / 2),
                this.height - SimpleButton.DEFAULT_HEIGHT - 4,
                (button) -> this.onClose()
        ));
    }
}
