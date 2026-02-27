package btw.lowercase.skyboxify;

import net.minecraft.client.Minecraft;

import java.nio.file.Path;

public final class SkyboxifyInfo {
	public static final String MOD_ID = "@MODID@";
	public static final String VERSION = "@VERSION@";
	public static final String COMMIT = "@COMMIT_HASH@";

	public static final Path DEBUG_FOLDER = Minecraft.getInstance().gameDirectory.toPath().resolve("debug_" + SkyboxifyInfo.MOD_ID);
}
