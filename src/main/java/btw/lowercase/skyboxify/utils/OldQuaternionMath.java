/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025 lowercasebtw
 * Copyright (C) 2025 Contributors to the project retain their copyright
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

package btw.lowercase.skyboxify.utils;

import lombok.experimental.UtilityClass;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3fc;

@UtilityClass
public final class OldQuaternionMath {

    // This method replicates the old Mojang-made Quaternion mulPose method, which was
    // used for the initial sky transformations which did not have any bobbing issues.
    // See https://github.com/sp614x/optifine/issues/7235#issuecomment-1581930719
    public static void mulPose(Matrix4f pose, float angle, Vector3fc axis) {
        // Quaternion fields
        float q_sin = Mth.sin(angle * 0.5F);
        float q_x = axis.x() * q_sin;
        float q_y = axis.y() * q_sin;
        float q_z = axis.z() * q_sin;
        float q_w = Mth.cos(angle * 0.5F);

        // Matrix4f/Matrix3f fields
        float m_xFactor = 2.0F * q_x * q_x;
        float m_yFactor = 2.0F * q_y * q_y;
        float m_zFactor = 2.0F * q_z * q_z;
        float m_xy = q_x * q_y;
        float m_yz = q_y * q_z;
        float m_zx = q_z * q_x;
        float m_xw = q_x * q_w;
        float m_yw = q_y * q_w;
        float m_zw = q_z * q_w;
        float m_m00 = 1.0F - m_yFactor - m_zFactor;
        float m_m01 = 2.0F * (m_xy - m_zw);
        float m_m02 = 2.0F * (m_zx + m_yw);
        float m_m10 = 2.0F * (m_xy + m_zw);
        float m_m11 = 1.0F - m_zFactor - m_xFactor;
        float m_m12 = 2.0F * (m_yz - m_xw);
        float m_m20 = 2.0F * (m_zx - m_yw);
        float m_m21 = 2.0F * (m_yz + m_xw);
        float m_m22 = 1.0F - m_xFactor - m_yFactor;

        // Multiply pose matrix
        pose.m00(m_m00 * pose.m00() + m_m01 * pose.m10() + m_m02 * pose.m20());
        pose.m01(m_m00 * pose.m01() + m_m01 * pose.m11() + m_m02 * pose.m21());
        pose.m02(m_m00 * pose.m02() + m_m01 * pose.m12() + m_m02 * pose.m22());
        pose.m03(m_m00 * pose.m03() + m_m01 * pose.m13() + m_m02 * pose.m23());
        pose.m10(m_m10 * pose.m00() + m_m11 * pose.m10() + m_m12 * pose.m20());
        pose.m11(m_m10 * pose.m01() + m_m11 * pose.m11() + m_m12 * pose.m21());
        pose.m12(m_m10 * pose.m02() + m_m11 * pose.m12() + m_m12 * pose.m22());
        pose.m13(m_m10 * pose.m03() + m_m11 * pose.m13() + m_m12 * pose.m23());
        pose.m20(m_m20 * pose.m00() + m_m21 * pose.m10() + m_m22 * pose.m20());
        pose.m21(m_m20 * pose.m01() + m_m21 * pose.m11() + m_m22 * pose.m21());
        pose.m22(m_m20 * pose.m02() + m_m21 * pose.m12() + m_m22 * pose.m22());
        pose.m23(m_m20 * pose.m03() + m_m21 * pose.m13() + m_m22 * pose.m23());
    }
}
