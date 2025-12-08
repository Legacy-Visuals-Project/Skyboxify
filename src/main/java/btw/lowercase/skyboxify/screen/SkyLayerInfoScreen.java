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

import btw.lowercase.skyboxify.screen.widget.SimpleButton;
import btw.lowercase.skyboxify.screen.widget.Text;
import btw.lowercase.skyboxify.skybox.OptiFineSkyLayer;
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