package btw.lowercase.optiboxes.utils;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import java.lang.reflect.Method;
import java.util.Arrays;

public final class IrisUtil {
    private static Object IRIS_INSTANCE = null;
    private static Method IRIS_ASSIGN_PIPELINE_METHOD = null;

    private IrisUtil() {
    }

    public static void assignPipeline(RenderPipeline pipeline, IrisPipeline program) {
        try {
            IRIS_ASSIGN_PIPELINE_METHOD.invoke(IRIS_INSTANCE, pipeline, program.internal());
        } catch (Exception ignored) {
        }
    }

    public static void assignPipeline(IrisPipeline program, RenderPipeline... pipelines) {
        for (RenderPipeline pipeline : pipelines) {
            assignPipeline(pipeline, program);
        }
    }

    static {
        try {
            // API
            Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            IRIS_INSTANCE = irisApiClass.getMethod("getInstance").invoke(null);

            // Enums
            @SuppressWarnings("rawtypes")
            Class<? extends Enum> irisProgramEnum = Class.forName("net.irisshaders.iris.api.v0.IrisProgram").asSubclass(Enum.class);
            Arrays.stream(IrisPipeline.VALUES).forEach((program) -> program.initialize(irisProgramEnum));

            // Methods
            IRIS_ASSIGN_PIPELINE_METHOD = IRIS_INSTANCE.getClass().getMethod("assignPipeline", RenderPipeline.class, irisProgramEnum);
        } catch (Exception ignored) {
        }
    }
}
