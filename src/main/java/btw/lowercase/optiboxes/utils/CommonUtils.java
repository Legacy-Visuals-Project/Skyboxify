package btw.lowercase.optiboxes.utils;

import btw.lowercase.optiboxes.OptiBoxesClient;
import btw.lowercase.optiboxes.utils.components.Range;
import btw.lowercase.optiboxes.utils.components.Weather;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class CommonUtils {
    private static final Pattern OPTIFINE_RANGE_SEPARATOR = Pattern.compile("(\\d|\\))-(\\d|\\()");
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    public static final UVRange[] TEXTURE_UV_RANGE_FACES = new UVRange[]{
            new UVRange(0.0F, 0.0F, 0.33333334F, 0.5F), // 0 (Bottom)
            new UVRange(0.33333334F, 0.0F, 0.6666667F, 0.5F), // 1 (Top)
            new UVRange(0.6666667F, 0.0F, 1.0F, 0.5F), // 2 (East)
            new UVRange(0.0F, 0.5F, 0.33333334F, 1.0F), // 3 (South)
            new UVRange(0.33333334F, 0.5F, 0.6666667F, 1.0F), // 4 (West)
            new UVRange(0.6666667F, 0.5F, 1.0F, 1.0F), // 5 (North)
    };

    private static final Matrix4f[] MATRIX4F_ROTATED_FACES = new Matrix4f[]{
            new Matrix4f().rotateY((float) Math.toRadians(90.0F)), // 0 (Bottom)
            new Matrix4f().rotateX((float) Math.toRadians(180.0F)).rotateY((float) Math.toRadians(-90.0F)), // 1 (Top)
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(90.0F)), // 2 (East)
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(180.0F)), // 3 (South)
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)).rotateZ((float) Math.toRadians(-90.0F)), // 4 (West)
            new Matrix4f().rotateX((float) Math.toRadians(90.0F)) // 5 (North)
    };

    private CommonUtils() {
    }

    public static @Nullable JsonObject convertOptiFineSkyProperties(SkyboxResourceHelper skyboxResourceHelper, Properties properties, ResourceLocation propertiesResourceLocation) {
        JsonObject jsonObject = new JsonObject();
        Optional<ResourceLocation> sourceTexture = Optional.ofNullable(parseSourceTexture(properties.getProperty("source", null), skyboxResourceHelper, propertiesResourceLocation));
        if (sourceTexture.isEmpty() && OptiBoxesClient.getConfig().ignoreBrokenSkies.isEnabled()) {
            return null;
        }

        jsonObject.addProperty("source", sourceTexture.orElse(MissingTextureAtlasSprite.getLocation()).toString());

        // Blend
        if (properties.containsKey("blend")) {
            jsonObject.addProperty("blend", properties.getProperty("blend"));
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

            fade.addProperty("startFadeIn", normalizeTickTime(startFadeIn));
            fade.addProperty("endFadeIn", normalizeTickTime(endFadeIn));
            fade.addProperty("startFadeOut", normalizeTickTime(startFadeOut));
            fade.addProperty("endFadeOut", normalizeTickTime(endFadeOut));
        } else {
            fade.addProperty("alwaysOn", true);
        }
        jsonObject.add("fade", fade);

        // Speed
        if (properties.containsKey("speed")) {
            final float value = safeParseFloat(properties.getProperty("speed"), 1.0F);
            if (value != Float.MIN_VALUE) {
                jsonObject.addProperty("speed", value);
            } else {
                LOGGER.warn("Invalid speed provided in skybox.");
            }
        }

        // Rotation
        if (properties.containsKey("rotate")) {
            jsonObject.addProperty("rotate", safeParseBoolean(properties.getProperty("rotate"), true));
        }

        // Transition
        if (properties.containsKey("transition")) {
            jsonObject.addProperty("transition", safeParseFloat(properties.getProperty("transition"), 1.0F));
        }

        // Axis
        if (properties.containsKey("axis")) {
            jsonObject.add("axis", parseAxis(properties.getProperty("axis")));
        }

        // Weather
        if (properties.containsKey("weather")) {
            String[] weathers = properties.getProperty("weather").trim().split(" ");
            JsonArray jsonWeather = new JsonArray();
            if (weathers.length > 0) {
                Arrays.stream(weathers).forEach(jsonWeather::add);
            } else {
                jsonWeather.add("clear");
            }

            jsonObject.add("weather", jsonWeather);
        }

        // Biomes
        if (properties.containsKey("biomes")) {
            String biomesString = properties.getProperty("biomes").trim();
            if (biomesString.startsWith("!")) {
                jsonObject.addProperty("biomeInclusion", false);
                biomesString = biomesString.substring(1);
            }

            String[] biomes = biomesString.trim().split(" ");
            if (biomes.length > 0) {
                JsonArray jsonBiomes = new JsonArray();
                Arrays.stream(biomes).filter(ResourceLocation::isValidPath).forEach(jsonBiomes::add);
                jsonObject.add("biomes", jsonBiomes);
            }
        }

        // Heights
        if (properties.containsKey("heights")) {
            List<Range> rangeEntries = parseRangeEntriesNegative(properties.getProperty("heights"));
            if (!rangeEntries.isEmpty()) {
                JsonArray jsonYRanges = new JsonArray();
                rangeEntries.stream().map(range -> Range.CODEC.encode(range, JsonOps.INSTANCE, new JsonObject()).getOrThrow()).forEach(jsonYRanges::add);
                jsonObject.add("heights", jsonYRanges);
            }
        }

        // Days Loop -> Loop
        if (properties.containsKey("days")) {
            List<Range> rangeEntries = parseRangeEntries(properties.getProperty("days"));
            if (!rangeEntries.isEmpty()) {
                JsonObject loopObject = new JsonObject();
                JsonArray loopRange = new JsonArray();
                rangeEntries.stream().map(range -> Range.CODEC.encode(range, JsonOps.INSTANCE, new JsonObject()).getOrThrow()).forEach(loopRange::add);

                int value = 8;
                if (properties.containsKey("daysLoop")) {
                    value = safeParseInteger(properties.getProperty("daysLoop"), 8);
                }

                loopObject.addProperty("days", value);
                loopObject.add("ranges", loopRange);
                jsonObject.add("loop", loopObject);
            }
        }

        return jsonObject;
    }

    public static @Nullable ResourceLocation parseSourceTexture(String source, SkyboxResourceHelper skyboxResourceHelper, ResourceLocation propertiesId) {
        ResourceLocation textureId;
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
                    ResourceLocation location = ResourceLocation.tryParse(source);
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
            textureId = ResourceLocation.fromNamespaceAndPath(namespace, path);
        } catch (ResourceLocationException e) {
            LOGGER.error("Failed to read texture path '{}:{}' as resource location", namespace, path);
            return null;
        }

        final InputStream textureInputStream = skyboxResourceHelper.getInputStream(textureId);
        if (textureInputStream == null) {
            LOGGER.error("Failed to load texture input stream for texture '{}'", textureId);
            return null;
        }

        try {
            textureInputStream.close();
        } catch (Exception ignored) {
        }

        return textureId;
    }

    public static int toTickTime(String time) {
        String[] parts = time.split(":");
        if (parts.length == 2) {
            int h = safeParseInteger(parts[0], -1);
            int m = safeParseInteger(parts[1], -1);
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

    public static int normalizeTickTime(int tickTime) {
        int result = tickTime % 24000;
        if (result < 0) {
            result += 24000;
        }

        return result;
    }

    public static List<Range> parseRangeEntries(String source) {
        List<Range> rangeEntries = new ArrayList<>();
        String[] parts = source.trim().split(" ,");
        for (String part : parts) {
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
                    int min = safeParseInteger(parts[0], -1);
                    int max = safeParseInteger(parts[1], -1);
                    if (min >= 0 && max >= 0) {
                        return new Range(min, max);
                    }
                }
            } else {
                int value = safeParseInteger(part, -1);
                if (value >= 0) {
                    return new Range(value, value);
                }
            }
        }

        return null;
    }

    public static List<Range> parseRangeEntriesNegative(String source) {
        List<Range> rangeEntries = new ArrayList<>();
        String[] parts = source.trim().split(" ,");
        for (String part : parts) {
            Range range = parseRangeEntryNegative(part);
            if (range != null) {
                rangeEntries.add(range);
            }
        }

        return rangeEntries;
    }

    private static @Nullable Range parseRangeEntryNegative(String part) {
        if (part != null) {
            String s = OPTIFINE_RANGE_SEPARATOR.matcher(part).replaceAll("$1=$2");
            if (s.contains("=")) {
                String[] parts = s.split("=");
                if (parts.length == 2) {
                    final int j = safeParseInteger(stripBrackets(parts[0]), Integer.MIN_VALUE);
                    final int k = safeParseInteger(stripBrackets(parts[1]), Integer.MIN_VALUE);
                    if (j != Integer.MIN_VALUE && k != Integer.MIN_VALUE) {
                        return new Range(Math.min(j, k), Math.max(j, k));
                    }
                }
            } else {
                int i = safeParseInteger(stripBrackets(part), Integer.MIN_VALUE);
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
        String[] parts = axisString.trim().replaceAll(" +", " ").split(" ");
        JsonArray newAxis = new JsonArray(3);
        boolean valid = false;
        if (parts.length == 3) {
            float x = safeParseFloat(parts[0], Float.MIN_VALUE);
            float y = safeParseFloat(parts[1], Float.MIN_VALUE);
            float z = safeParseFloat(parts[2], Float.MIN_VALUE);
            if (x * x + y * y + z * z > 1.0E-5F) {
                newAxis.add(z);
                newAxis.add(y);
                newAxis.add(-x);
                valid = true;
            }
        }

        if (!valid) {
            LOGGER.warn("Invalid axis provided in skybox, setting to default axis.");
            newAxis.add(1.0F);
            newAxis.add(0.0F);
            newAxis.add(0.0F);
        }

        return newAxis;
    }

    public static boolean checkRanges(double value, List<Range> rangeEntries) {
        return rangeEntries.isEmpty() || rangeEntries.stream()
                .anyMatch(range -> com.google.common.collect.Range.closed(range.min(), range.max()).contains((float) value));
    }

    public static boolean isInTimeInterval(int currentTime, int startTime, int endTime) {
        if (currentTime < 0 || currentTime >= 24000) {
            return false; // Invalid time
        } else if (startTime <= endTime) {
            return currentTime >= startTime && currentTime <= endTime;
        } else {
            return currentTime >= startTime || currentTime <= endTime;
        }
    }

    public static float calculateFadeAlphaValue(float maxAlpha, float minAlpha, int currentTime, int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut) {
        if (isInTimeInterval(currentTime, endFadeIn, startFadeOut)) {
            return maxAlpha;
        } else if (isInTimeInterval(currentTime, startFadeIn, endFadeIn)) {
            final int fadeInDuration = calculateCyclicTimeDistance(startFadeIn, endFadeIn);
            final int timePassedSinceFadeInStart = calculateCyclicTimeDistance(startFadeIn, currentTime);
            return minAlpha + ((float) timePassedSinceFadeInStart / fadeInDuration) * (maxAlpha - minAlpha);
        } else if (isInTimeInterval(currentTime, startFadeOut, endFadeOut)) {
            final int fadeOutDuration = calculateCyclicTimeDistance(startFadeOut, endFadeOut);
            final int timePassedSinceFadeOutStart = calculateCyclicTimeDistance(startFadeOut, currentTime);
            return maxAlpha + ((float) timePassedSinceFadeOutStart / fadeOutDuration) * (minAlpha - maxAlpha);
        } else {
            return minAlpha;
        }
    }

    public static int calculateCyclicTimeDistance(int startTime, int endTime) {
        return (endTime - startTime + 24000) % 24000;
    }

    public static float calculateConditionAlphaValue(float maxAlpha, float minAlpha, float lastAlpha, int duration, boolean in) {
        if (duration == 0) {
            return lastAlpha;
        } else if (in && maxAlpha == lastAlpha) {
            return maxAlpha;
        } else if (!in && lastAlpha == minAlpha) {
            return minAlpha;
        } else {
            float alphaChange = (maxAlpha - minAlpha) / duration;
            float result = in ? lastAlpha + alphaChange : lastAlpha - alphaChange;
            return Mth.clamp(result, minAlpha, maxAlpha);
        }
    }

    public static Codec<Double> getClampedDoubleCodec(double min, double max) {
        if (min > max) {
            throw new UnsupportedOperationException("Maximum value was lesser than than the minimum value");
        } else {
            return Codec.DOUBLE.xmap(value -> Mth.clamp(value, min, max), Function.identity());
        }
    }

    public static float getWeatherAlpha(List<Weather> weatherConditions, float rainStrength, float thunderStrength) {
        final float alpha = 1.0F - rainStrength;
        final float calculatedRainStrength = rainStrength - thunderStrength;
        float weatherAlpha = 0.0F;
        if (weatherConditions.contains(Weather.CLEAR)) {
            weatherAlpha += alpha;
        }

        if (weatherConditions.contains(Weather.RAIN)) {
            weatherAlpha += calculatedRainStrength;
        }

        if (weatherConditions.contains(Weather.THUNDER)) {
            weatherAlpha += thunderStrength;
        }

        return Mth.clamp(weatherAlpha, 0.0F, 1.0F);
    }

    public static UVRange getUvRangeForFace(int face) {
        if (face < 0 || face >= TEXTURE_UV_RANGE_FACES.length) {
            throw new RuntimeException("Face is out of bounds");
        } else {
            return TEXTURE_UV_RANGE_FACES[face];
        }
    }

    public static Matrix4f getRotationMatrixForFace(int face) {
        if (face < 0 || face >= MATRIX4F_ROTATED_FACES.length) {
            throw new RuntimeException("Face is out of bounds");
        } else {
            return MATRIX4F_ROTATED_FACES[face];
        }
    }

    public static Vector3f getMatrixTransform(Matrix4f matrix4f, float x, float y, float z) {
        return new Vector3f(
                matrix4f.m00() * x + matrix4f.m10() * y + matrix4f.m20() * z + matrix4f.m30(),
                matrix4f.m01() * x + matrix4f.m11() * y + matrix4f.m21() * z + matrix4f.m31(),
                matrix4f.m02() * x + matrix4f.m12() * y + matrix4f.m22() * z + matrix4f.m32()
        );
    }

    // Safety
    public static int safeParseInteger(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    public static float safeParseFloat(String value, float defaultValue) {
        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    public static boolean safeParseBoolean(String value, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(value.trim());
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }
}
