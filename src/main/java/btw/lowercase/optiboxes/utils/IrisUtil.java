package btw.lowercase.optiboxes.utils;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public final class IrisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(IrisUtil.class);

    private static Object IRIS_INSTANCE = null;
    private static Method IRIS_ASSIGN_PIPELINE_METHOD = null;
    private static Enum<?> IRIS_PROGRAM_SKY_BASIC = null;
    private static Enum<?> IRIS_PROGRAM_SKY_TEXTURED = null;

    private IrisUtil() {
    }

    private static void initialize() {
        try {
            // Instance/API
            Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            IRIS_INSTANCE = irisApiClass.getMethod("getInstance").invoke(null);
            Class<?> irisInstanceClass = IRIS_INSTANCE.getClass();

            // Enum
            Class<?> irisProgramEnum = Class.forName("net.irisshaders.iris.api.v0.IrisProgram");
            IRIS_PROGRAM_SKY_BASIC = Enum.valueOf(irisProgramEnum.asSubclass(Enum.class), "SKY_BASIC");
            IRIS_PROGRAM_SKY_TEXTURED = Enum.valueOf(irisProgramEnum.asSubclass(Enum.class), "SKY_TEXTURED");

            // Methods
            IRIS_ASSIGN_PIPELINE_METHOD = irisInstanceClass.getMethod("assignPipeline", RenderPipeline.class, irisProgramEnum);
        } catch (Exception exception) {
            LOGGER.error("Failed to initialize iris util! Iris may not be installed so just ignore this message!");
            exception.printStackTrace();
        }
    }

    public static void assignPipeline(RenderPipeline pipeline, Object enumValue) {
        if (IRIS_ASSIGN_PIPELINE_METHOD != null) {
            try {
                IRIS_ASSIGN_PIPELINE_METHOD.invoke(IRIS_INSTANCE, pipeline, enumValue);
            } catch (Exception exception) {
                LOGGER.error("Failed to assign iris shader pipeline!");
                exception.printStackTrace();
            }
        }
    }

    public static Enum<?> skyBasic() {
        return IRIS_PROGRAM_SKY_BASIC;
    }

    public static Enum<?> skyTextured() {
        return IRIS_PROGRAM_SKY_TEXTURED;
    }

    static {
        initialize();
    }
}
