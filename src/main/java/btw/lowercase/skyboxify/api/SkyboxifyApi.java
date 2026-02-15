package btw.lowercase.skyboxify.api;

import net.minecraft.resources.Identifier;

public interface SkyboxifyApi {
	Identifier getModernDimension(final int legacyId);

	void registerDimensionMapping(final int legacyId, final Identifier modernId);

	default int getApiVersion() {
		return 1;
	}
}
