package btw.lowercase.skyboxify.utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3fc;

/**
 * Subset of Mojang's math library used in <=1.19.2, to fix sky bobbing issues.
 * See https://github.com/sp614x/optifine/issues/7235#issuecomment-1581930719
 */
public class Quaternion {
	public static Matrix4f rotation(final Vector3fc axis, final float radians) {
		// Create Quat from Axis/Angles
		final Quaternionf quaternionf = new Quaternionf();
		final float scalar = (float) Math.sin(radians / 2.0F);
		quaternionf.x = axis.x() * scalar;
		quaternionf.y = axis.y() * scalar;
		quaternionf.z = axis.z() * scalar;
		quaternionf.w = (float) Math.cos(radians / 2.0F);

		// Create Rotation Matrix from Quat
		final float j = 2.0F * quaternionf.x * quaternionf.x;
		final float k = 2.0F * quaternionf.y * quaternionf.y;
		final float l = 2.0F * quaternionf.z * quaternionf.z;
		final float m = quaternionf.x * quaternionf.y;
		final float n = quaternionf.y * quaternionf.z;
		final float o = quaternionf.z * quaternionf.x;
		final float p = quaternionf.x * quaternionf.w;
		final float q = quaternionf.y * quaternionf.w;
		final float r = quaternionf.z * quaternionf.w;

		final Matrix4f matrix = new Matrix4f().zero(); // Mojang's Matrix class looks to default all to 0
		matrix.m00(1.0F - k - l);
		matrix.m01(2.0F * (m - r));
		matrix.m02(2.0F * (o + q));
		matrix.m10(2.0F * (m + r));
		matrix.m11(1.0F - l - j);
		matrix.m12(2.0F * (n - p));
		matrix.m20(2.0F * (o - q));
		matrix.m21(2.0F * (n + p));
		matrix.m22(1.0F - j - k);
		matrix.m33(1.0F);
		return matrix;
	}
}
