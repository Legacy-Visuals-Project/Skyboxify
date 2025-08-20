package btw.lowercase.optiboxes;

import btw.lowercase.optiboxes.command.OptiboxesCommand;
import btw.lowercase.optiboxes.config.OptiBoxesConfig;
import btw.lowercase.optiboxes.skybox.OptiFineSkybox;
import btw.lowercase.optiboxes.skybox.SkyboxManager;
import btw.lowercase.optiboxes.utils.CommonUtils;
import btw.lowercase.optiboxes.utils.SkyboxResourceHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entrypoint
public final class OptiBoxesClient implements ClientModInitializer {
    public static final String MOD_ID = "optiboxes";

    private static final String OPTIFINE_SKY_PARENT = "optifine/sky";
    private static final String SKY_PATTERN_ENDING = "(?<world>[\\w-]+)/(?<name>\\w+).properties$";
    private static final Pattern OPTIFINE_SKY_PATTERN = Pattern.compile(OPTIFINE_SKY_PARENT + "/" + SKY_PATTERN_ENDING);
    private static final String MCPATCHER_SKY_PARENT = "mcpatcher/sky";
    private static final Pattern MCPATCHER_SKY_PATTERN = Pattern.compile(MCPATCHER_SKY_PARENT + "/" + SKY_PATTERN_ENDING);

    private static OptiBoxesConfig CONFIG_INSTANCE = null;
    private static ModContainer MOD_CONTAINER = null;
    public static final Logger LOGGER = LoggerFactory.getLogger(OptiBoxesClient.class);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static OptiBoxesConfig getConfig() {
        if (CONFIG_INSTANCE == null) {
            CONFIG_INSTANCE = new OptiBoxesConfig(FabricLoader.getInstance().getConfigDir().resolve(OptiBoxesClient.MOD_ID + ".json"));
        }

        return CONFIG_INSTANCE;
    }

    public static ModContainer getModContainer() {
        if (MOD_CONTAINER == null) {
            MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(() -> new RuntimeException("Mod metadata container was null."));
        }

        return MOD_CONTAINER;
    }

    @Override
    public void onInitializeClient() {
        getConfig().load();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SkyboxResourceHelper());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(new OptiboxesCommand()));
    }

    public static void convert(SkyboxResourceHelper skyboxResourceHelper) {
        if (CONFIG_INSTANCE.processOptiFine.isEnabled()) {
            parseSkyboxes(skyboxResourceHelper, OPTIFINE_SKY_PARENT, OPTIFINE_SKY_PATTERN);
        }

        if (CONFIG_INSTANCE.processMCPatcher.isEnabled()) {
            parseSkyboxes(skyboxResourceHelper, MCPATCHER_SKY_PARENT, MCPATCHER_SKY_PATTERN);
        }
    }

    private static void parseSkyboxes(SkyboxResourceHelper skyboxResourceHelper, String skyParent, Pattern skyPattern) {
        final Map<String, JsonArray> layers = new HashMap<>();
        layers.put("world0", new JsonArray()); // Overworld
        layers.put("world-1", new JsonArray()); // Nether
        layers.put("world1", new JsonArray()); // The End
        skyboxResourceHelper.searchIn(skyParent).filter(id -> id.getPath().endsWith(".properties")).sorted(Comparator.comparing(ResourceLocation::getPath, (id1, id2) -> {
            final Matcher matcherId1 = skyPattern.matcher(id1);
            final Matcher matcherId2 = skyPattern.matcher(id2);
            if (matcherId1.find() && matcherId2.find()) {
                final int a = CommonUtils.safeParseInteger(matcherId1.group("name").replace("sky", ""), -1);
                final int b = CommonUtils.safeParseInteger(matcherId2.group("name").replace("sky", ""), -1);
                if (a >= 0 && b >= 0) {
                    return a - b;
                }
            }
            return 0;
        })).forEach(id -> {
            Matcher matcher = skyPattern.matcher(id.getPath());
            if (matcher.find()) {
                final String world = matcher.group("world");
                final String name = matcher.group("name");
                if (world == null || name == null) {
                    return;
                }

                if (name.equals("moon_phases") || name.equals("sun")) {
                    // TODO/NOTE: Support moon/sun
                    LOGGER.warn("Skipping {}, moon_phases/sun aren't currently supported!", id);
                    return;
                }

                final InputStream inputStream = skyboxResourceHelper.getInputStream(id);
                if (inputStream == null) {
                    LOGGER.error("Error trying to read namespaced identifier: {}", id);
                    return;
                }

                final Properties properties = new Properties();
                try {
                    properties.load(inputStream);
                } catch (IOException e) {
                    LOGGER.error("Error trying to read properties from: {}", id);
                    return;
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("Error trying to close input stream at namespaced identifier: {}", id);
                    }
                }

                final JsonObject json = CommonUtils.convertOptiFineSkyProperties(skyboxResourceHelper, properties, id);
                // NOTE: Don't add broken skies (returns null if broken)
                if (json != null && layers.containsKey(world)) {
                    layers.get(world).add(json);
                }
            }
        });

        for (Map.Entry<String, JsonArray> entry : layers.entrySet()) {
            JsonObject skyJson = new JsonObject();
            skyJson.add("layers", entry.getValue());
            skyJson.addProperty("world",
                    (switch (entry.getKey()) {
                        case "world-1" -> Level.NETHER;
                        case "world1" -> Level.END;
                        default -> Level.OVERWORLD;
                    }).location().toString());
            SkyboxManager.INSTANCE.addSkybox(OptiFineSkybox.CODEC.decode(JsonOps.INSTANCE, skyJson).getOrThrow().getFirst());
        }
    }
}
