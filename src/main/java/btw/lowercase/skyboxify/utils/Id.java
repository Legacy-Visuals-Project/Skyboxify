/**
 * Skyboxify
 * A skybox mod that allows you to use OptiFine skies in Fabric 1.21+
 * <p>
 * Copyright (C) 2025-2026 lowercasebtw
 * Copyright (C) 2025-2026 Contributors to the project retain their copyright
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

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resource.Identifier;
import net.ornithemc.osl.core.api.util.NamespacedIdentifier;
import org.jetbrains.annotations.Nullable;

public final class Id {
    public static final Codec<Id> CODEC = Codec.STRING.comapFlatMap(Id::read, Id::toString).stable();
    public static final String DEFAULT_NAMESPACE = "minecraft";

    private final String namespace;
    private final String path;

    private Id(String string, String string2) {
        assert isValidNamespace(string);
        assert isValidPath(string2);
        this.namespace = string;
        this.path = string2;
    }

    public static Id fromVanilla(final NamespacedIdentifier identifier) {
        return new Id(identifier.namespace(), identifier.identifier());
    }

    private static Id createUntrusted(final String namespace, final String path) {
        return new Id(assertValidNamespace(namespace, path), assertValidPath(namespace, path));
    }

    public static Id fromNamespaceAndPath(final String namespace, final String path) {
        return createUntrusted(namespace, path);
    }

    public static Id parse(final String input) {
        return bySeparator(input, ':');
    }

    public static Id withDefaultNamespace(final String path) {
        return new Id(DEFAULT_NAMESPACE, assertValidPath(DEFAULT_NAMESPACE, path));
    }

    @Nullable
    public static Id tryParse(final String input) {
        return tryBySeparator(input, ':');
    }

    public static Id bySeparator(final String input, final char del) {
        int index = input.indexOf(del);
        if (index >= 0) {
            String string2 = input.substring(index + 1);
            if (index != 0) {
                String string3 = input.substring(0, index);
                return createUntrusted(string3, string2);
            } else {
                return withDefaultNamespace(string2);
            }
        } else {
            return withDefaultNamespace(input);
        }
    }

    @Nullable
    public static Id tryBySeparator(final String input, final char del) {
        int index = input.indexOf(del);
        if (index >= 0) {
            String string2 = input.substring(index + 1);
            if (!isValidPath(string2)) {
                return null;
            } else if (index != 0) {
                String string3 = input.substring(0, index);
                return isValidNamespace(string3) ? new Id(string3, string2) : null;
            } else {
                return new Id(DEFAULT_NAMESPACE, string2);
            }
        } else {
            return isValidPath(input) ? new Id(DEFAULT_NAMESPACE, input) : null;
        }
    }

    public static DataResult<Id> read(String string) {
        try {
            return DataResult.success(parse(string));
        } catch (Exception resourceLocationException) {
            return DataResult.error(() -> "Not a valid resource location: " + string + " " + resourceLocationException.getMessage());
        }
    }

    public NamespacedIdentifier vanilla() {
        return new Identifier(this.namespace, this.path);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getPath() {
        return this.path;
    }

    public Id withPath(final String path) {
        return new Id(this.namespace, assertValidPath(this.namespace, path));
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.path;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        } else {
            return !(object instanceof Id resourceLocation)
                    ? false
                    : this.namespace.equals(resourceLocation.namespace) && this.path.equals(resourceLocation.path);
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    public static boolean isValidNamespace(final String namespace) {
        for (int i = 0; i < namespace.length(); i++) {
            if (!validNamespaceChar(namespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidPath(final String path) {
        for (int i = 0; i < path.length(); i++) {
            if (!validPathChar(path.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static String assertValidNamespace(final String namespace, final String path) {
        if (!isValidNamespace(namespace)) {
            throw new RuntimeException("Non [a-z0-9_.-] character in namespace of location: " + namespace + ":" + path);
        } else {
            return namespace;
        }
    }

    private static String assertValidPath(final String namespace, final String path) {
        if (!isValidPath(path)) {
            throw new RuntimeException("Non [a-z0-9/._-] character in path of location: " + namespace + ":" + path);
        } else {
            return path;
        }
    }

    public static boolean validPathChar(final char ch) {
        return ch == '_' || ch == '-' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9' || ch == '/' || ch == '.';
    }

    private static boolean validNamespaceChar(final char ch) {
        return ch == '_' || ch == '-' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9' || ch == '.';
    }
}
