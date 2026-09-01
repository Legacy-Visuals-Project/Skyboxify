/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025-2026 lowercasebtw
 * Copyright (C) 2025-2026 Contributors to the project retain their copyright
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

package btw.lowercase.skyboxify.skybox.renderer;

import btw.lowercase.skyboxify.utils.BlendFunction;
import btw.lowercase.skyboxify.utils.Id;
import net.minecraft.client.render.pipeline.RenderTarget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class FeatureRenderer<T extends FeatureRenderer.Submit> {
    protected final RenderTarget renderTarget;
    protected final List<T> submits = new ArrayList<>();

    protected FeatureRenderer(final RenderTarget renderTarget) {
        this.renderTarget = renderTarget;
    }

    protected abstract T createSubmit(final Pipeline pipeline, final Geometry geometry, final RenderUniforms uniforms, final Id location);

    public void submit(final Pipeline pipeline, final Geometry geometry, final RenderUniforms uniforms, final Id location) {
        if (geometry != null && !geometry.isClosed()) {
            this.submits.add(createSubmit(pipeline, geometry, uniforms, location));
        } else {
            throw new IllegalStateException("Cannot call submit with " + (geometry == null ? "null" : "closed") + " geometry!");
        }
    }

    // Override and implement your rendering
    public void endFrame() {
        this.clear();
    }

    public void clear() {
        this.submits.clear();
    }

    public record Pipeline(@Nullable BlendFunction blendFunction) {
    }

    protected interface Submit {
    }
}
