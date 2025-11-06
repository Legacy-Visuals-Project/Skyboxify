package btw.lowercase.optiboxes.skybox;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.skybox.components.Range;
import btw.lowercase.optiboxes.utils.CommonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.IdentifierException;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

// TODO/Figure out a nicer/best way to go straight to the CODEC
@Deprecated(forRemoval = true)
public final class SkyboxParser {
    private static final Pattern OPTIFINE_RANGE_SEPARATOR = Pattern.compile("(\\d|\\))-(\\d|\\()");
    private static final Logger LOGGER = LoggerFactory.getLogger(SkyboxParser.class);

    private SkyboxParser() {
    }

    public static @Nullable JsonObject parseSkyProperties(Properties properties, Identifier propertiesIdentifier) {
        JsonObject output = new JsonObject();

        Optional<Identifier> sourceTexture = Optional.ofNullable(parseSourceTexture(properties.getProperty("source", null), propertiesIdentifier));
        if (sourceTexture.isEmpty() && OptiBoxesClient.getConfig().ignoreBrokenSkies.isEnabled()) {
            return null;
        }

        output.addProperty("source", sourceTexture.orElse(MissingTextureAtlasSprite.getLocation()).toString());

        // Blend
        if (properties.containsKey("blend")) {
            output.addProperty("blend", properties.getProperty("blend"));
        }

        // Convert fade
        JsonObject fade = new JsonObject();
        if (properties.containsKey("startFadeIn") && properties.containsKey("endFadeIn") && properties.containsKey("endFadeOut")) {
            int startFadeIn = toTickTime(properties.getProperty("startFadeIn"));
            int endFadeIn = toTickTime(properties.getProperty("endFadeIn"));
            int endFadeOut = toTickTime(properties.getProperty("endFadeOut"));
            int startFadeOut;
            if (properties.containsKey("startFadeOut")) {
                startFadeOut = toTickTime(properties.getProperty("startFadeOut"));
            } else {
                startFadeOut = endFadeOut - (endFadeIn - startFadeIn);
                if (startFadeIn <= startFadeOut && endFadeIn >= startFadeOut) {
                    startFadeOut = endFadeOut;
                }
            }

            fade.addProperty("startFadeIn", CommonUtils.normalizeTickTime(startFadeIn));
            fade.addProperty("endFadeIn", CommonUtils.normalizeTickTime(endFadeIn));
            fade.addProperty("startFadeOut", CommonUtils.normalizeTickTime(startFadeOut));
            fade.addProperty("endFadeOut", CommonUtils.normalizeTickTime(endFadeOut));
        } else {
            fade.addProperty("alwaysOn", true);
        }

        output.add("fade", fade);

        // Speed
        if (properties.containsKey("speed")) {
            final float value = CommonUtils.safeParseFloat(properties.getProperty("speed"), 1.0F);
            if (value != Float.MIN_VALUE) {
                output.addProperty("speed", value);
            } else {
                LOGGER.warn("Invalid speed provided in skybox.");
            }
        }

        // Rotation
        if (properties.containsKey("rotate")) {
            output.addProperty("rotate", CommonUtils.safeParseBoolean(properties.getProperty("rotate"), true));
        }

        // Transition
        if (properties.containsKey("transition")) {
            output.addProperty("transition", CommonUtils.safeParseFloat(properties.getProperty("transition"), 1.0F));
        }

        // Axis
        if (properties.containsKey("axis")) {
            output.add("axis", parseAxis(properties.getProperty("axis")));
        }

        // Weather
        if (properties.containsKey("weather")) {
            String[] weatherEntries = properties.getProperty("weather").trim().split(" ");
            JsonArray weather = new JsonArray();
            if (weatherEntries.length > 0) {
                Arrays.stream(weatherEntries).forEach(weather::add);
            } else {
                weather.add("clear");
            }

            output.add("weather", weather);
        }

        // Biomes
        if (properties.containsKey("biomes")) {
            String biomesString = properties.getProperty("biomes").trim();
            if (biomesString.startsWith("!")) {
                output.addProperty("biomeInclusion", false);
                biomesString = biomesString.substring(1);
            }

            String[] biomeEntries = biomesString.trim().split(" ");
            if (biomeEntries.length > 0) {
                JsonArray biomes = new JsonArray();
                Arrays.stream(biomeEntries).filter(Identifier::isValidPath).forEach(biomes::add);
                output.add("biomes", biomes);
            }
        }

        // Heights
        if (properties.containsKey("heights")) {
            List<Range> rangeEntries = parseRangeEntriesNegative(properties.getProperty("heights"));
            if (!rangeEntries.isEmpty()) {
                JsonArray heights = new JsonArray();
                rangeEntries.stream().map(range -> Range.CODEC.encode(range, JsonOps.INSTANCE, new JsonObject()).getOrThrow()).forEach(heights::add);
                output.add("heights", heights);
            }
        }

        // Days Loop -> Loop
        if (properties.containsKey("days")) {
            List<Range> rangeEntries = parseRangeEntries(properties.getProperty("days"));
            if (!rangeEntries.isEmpty()) {
                JsonArray ranges = new JsonArray();
                rangeEntries.stream().map(range -> Range.CODEC.encode(range, JsonOps.INSTANCE, new JsonObject()).getOrThrow()).forEach(ranges::add);

                int days = 8;
                if (properties.containsKey("daysLoop")) {
                    days = CommonUtils.safeParseInteger(properties.getProperty("daysLoop"), 8);
                }

                JsonObject loop = new JsonObject();
                loop.addProperty("days", days);
                loop.add("ranges", ranges);

                output.add("loop", loop);
            }
        }

        return output;
    }

    public static @Nullable Identifier parseSourceTexture(String source, Identifier propertiesId) {
        Identifier textureId;
        String namespace;
        String path;
        if (source == null) {
            namespace = propertiesId.getNamespace();
            path = propertiesId.getPath().replace(".properties", ".png");
        } else {
            if (source.startsWith("./")) {
                namespace = propertiesId.getNamespace();
                String fileName = propertiesId.getPath().split("/")[propertiesId.getPath().split("/").length - 1];
                path = propertiesId.getPath().replace(fileName, source.substring(2));
            } else {
                String[] parts = source.split("/", 3);
                if (parts.length == 3 && parts[0].equals("assets")) {
                    namespace = parts[1];
                    path = parts[2];
                } else {
                    final Identifier location = Identifier.tryParse(source);
                    if (location != null) {
                        namespace = location.getNamespace();
                        path = location.getPath();
                    } else {
                        return null;
                    }
                }
            }
        }

        try {
            textureId = Identifier.fromNamespaceAndPath(namespace, path);
        } catch (IdentifierException e) {
            LOGGER.error("Failed to read texture path '{}:{}' as resource location", namespace, path);
            return null;
        }

        return textureId;
    }

    public static int toTickTime(String time) {
        String[] parts = time.split(":");
        if (parts.length == 2) {
            int h = CommonUtils.safeParseInteger(parts[0], -1);
            int m = CommonUtils.safeParseInteger(parts[1], -1);
            if (h >= 0 && h <= 23 && m >= 0 && m <= 59) {
                h -= 6;
                if (h < 0) {
                    h += 24;
                }

                return h * 1000 + (int) (m / 60.0F * 1000.0F);
            }
        }

        LOGGER.warn("Invalid time: \"{}\" in skybox.", time);
        return -1;
    }

    public static List<Range> parseRangeEntries(String source) {
        List<Range> rangeEntries = new ArrayList<>();
        for (String part : source.trim().split(" ,")) {
            Range range = parseRangeEntry(part);
            if (range != null) {
                rangeEntries.add(range);
            }
        }

        return rangeEntries;
    }

    private static @Nullable Range parseRangeEntry(String part) {
        if (part != null) {
            if (part.contains("-")) {
                String[] parts = part.trim().split("-");
                if (parts.length == 2) {
                    int min = CommonUtils.safeParseInteger(parts[0], -1);
                    int max = CommonUtils.safeParseInteger(parts[1], -1);
                    if (min >= 0 && max >= 0) {
                        return new Range(min, max);
                    }
                }
            } else {
                int value = CommonUtils.safeParseInteger(part, -1);
                if (value >= 0) {
                    return new Range(value, value);
                }
            }
        }

        return null;
    }

    public static List<Range> parseRangeEntriesNegative(String source) {
        List<Range> rangeEntries = new ArrayList<>();
        for (String part : source.trim().split(" ,")) {
            final Range range = parseRangeEntryNegative(part);
            if (range != null) {
                rangeEntries.add(range);
            }
        }

        return rangeEntries;
    }

    private static @Nullable Range parseRangeEntryNegative(String part) {
        if (part != null) {
            final String s = OPTIFINE_RANGE_SEPARATOR.matcher(part).replaceAll("$1=$2");
            if (s.contains("=")) {
                String[] parts = s.split("=");
                if (parts.length == 2) {
                    final int j = CommonUtils.safeParseInteger(stripBrackets(parts[0]), Integer.MIN_VALUE);
                    final int k = CommonUtils.safeParseInteger(stripBrackets(parts[1]), Integer.MIN_VALUE);
                    if (j != Integer.MIN_VALUE && k != Integer.MIN_VALUE) {
                        return new Range(Math.min(j, k), Math.max(j, k));
                    }
                }
            } else {
                int i = CommonUtils.safeParseInteger(stripBrackets(part), Integer.MIN_VALUE);
                if (i != Integer.MIN_VALUE) {
                    return new Range(i, i);
                }
            }
        }

        return null;
    }

    private static String stripBrackets(String str) {
        return str.startsWith("(") && str.endsWith(")") ? str.substring(1, str.length() - 1) : str;
    }

    public static JsonArray parseAxis(String axisString) {
        JsonArray axis = new JsonArray(3);
        boolean valid = false;

        String[] parts = axisString.trim().replaceAll(" +", " ").split(" ");
        if (parts.length == 3) {
            float x = CommonUtils.safeParseFloat(parts[0], Float.MIN_VALUE);
            float y = CommonUtils.safeParseFloat(parts[1], Float.MIN_VALUE);
            float z = CommonUtils.safeParseFloat(parts[2], Float.MIN_VALUE);
            if (x * x + y * y + z * z > 1.0E-5F) {
                axis.add(z);
                axis.add(y);
                axis.add(-x);
                valid = true;
            }
        }

        if (!valid) {
            LOGGER.warn("Invalid axis provided in skybox, setting to default axis.");
            axis.add(1.0F);
            axis.add(0.0F);
            axis.add(0.0F);
        }

        return axis;
    }
}
