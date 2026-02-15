package btw.lowercase.skyboxify.api;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class SkyboxifyImpl implements SkyboxifyApi {
	private static final SkyboxifyImpl INSTANCE = new SkyboxifyImpl();

	private final Map<Integer, Identifier> dimensionMapping = new HashMap<>();

	private SkyboxifyImpl() {
		this.registerDimensionMapping(-1, Identifier.withDefaultNamespace("the_nether"));
		this.registerDimensionMapping(0, Identifier.withDefaultNamespace("overworld"));
		this.registerDimensionMapping(1, Identifier.withDefaultNamespace("the_end"));
		this.registerDimensionMapping(4, Identifier.fromNamespaceAndPath("aether", "the_aether"));
		this.registerDimensionMapping(7, Identifier.fromNamespaceAndPath("twilightforest", "twilight_forest"));
	}

	public static SkyboxifyApi getInstance() {
		return INSTANCE;
	}

	@Override
	public Identifier getModernDimension(final int legacyId) {
		return this.dimensionMapping.getOrDefault(legacyId, null);
	}

	@Override
	public void registerDimensionMapping(final int legacyId, final Identifier modernId) {
		if (this.dimensionMapping.containsKey(legacyId)) {
			throw new IllegalArgumentException("Cannot register dimension mapping, world with legacy id " + legacyId + " is already taken by \"" + dimensionMapping.get(legacyId) + "\"!");
		}

		if (this.dimensionMapping.containsValue(modernId)) {
			int currentId = 0;
			for (final int key : this.dimensionMapping.keySet()) {
				if (Objects.equals(this.dimensionMapping.get(key), modernId)) {
					currentId = key;
				}
			}

			throw new IllegalArgumentException("Cannot register dimension mapping, world \"" + modernId + "\" is already mapped to legacy id " + currentId);
		}

		this.dimensionMapping.put(legacyId, modernId);
	}
}
