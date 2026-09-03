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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * Subset impl backport of 26.2 Identifier
 */
public record Id(String namespace, String path) implements NamespacedIdentifier, Comparable<Id> {
    public static final Codec<Id> CODEC = Codec.STRING.comapFlatMap(Id::read, Id::toString).stable();
    public static final String DEFAULT_NAMESPACE = "minecraft";
    public static final Pattern VALID_NAMESPACE_REGEX = Pattern.compile("[a-z0-9_.-]*");
    public static final Pattern VALID_PATH_REGEX = Pattern.compile("[a-z0-9/._-]*");

    public Id {
        assert isValidNamespace(namespace);
        assert isValidPath(path);
    }

    public static Id fromVanilla(final NamespacedIdentifier it) {
        return new Id(it.namespace(), it.identifier());
    }

    private static Id createUntrusted(final String namespace, final String path) {
        return new Id(assertValidNamespace(namespace, path), assertValidPath(namespace, path));
    }

    public static Id fromNamespaceAndPath(final String namespace, final String path) {
        return createUntrusted(namespace, path);
    }

    public static Id parse(final String input) {
        return bySeparator(input, NamespacedIdentifier.SEPARATOR);
    }

    public static Id withDefaultNamespace(final String path) {
        return new Id(DEFAULT_NAMESPACE, assertValidPath(DEFAULT_NAMESPACE, path));
    }

    public static @Nullable Id tryParse(final String input) {
        return tryBySeparator(input, NamespacedIdentifier.SEPARATOR);
    }

    public static Id bySeparator(final String input, final char del) {
        final int index = input.indexOf(del);
        if (index >= 0) {
            final String path = input.substring(index + 1);
            if (index != 0) {
                final String namespace = input.substring(0, index);
                return createUntrusted(namespace, path);
            } else {
                return withDefaultNamespace(path);
            }
        } else {
            return withDefaultNamespace(input);
        }
    }

    public static @Nullable Id tryBySeparator(final String input, final char del) {
        final int index = input.indexOf(del);
        if (index >= 0) {
            final String path = input.substring(index + 1);
            if (!isValidPath(path)) {
                return null;
            } else if (index != 0) {
                final String namespace = input.substring(0, index);
                return isValidNamespace(namespace) ? new Id(namespace, path) : null;
            } else {
                return new Id(DEFAULT_NAMESPACE, path);
            }
        } else {
            return isValidPath(input) ? new Id(DEFAULT_NAMESPACE, input) : null;
        }
    }

    public static DataResult<Id> read(final String input) {
        try {
            return DataResult.success(parse(input));
        } catch (final IdException exception) {
            return DataResult.error(() -> "Not a valid resource location: " + input + " " + exception.getMessage());
        }
    }

    public static boolean isValidNamespace(final String namespace) {
        return VALID_NAMESPACE_REGEX.matcher(namespace).matches();
    }

    public static boolean isValidPath(final String path) {
        return VALID_PATH_REGEX.matcher(path).matches();
    }

    private static String assertValidNamespace(final String namespace, final String path) {
        if (!isValidNamespace(namespace)) {
            throw new IdException("Invalid character (" + VALID_NAMESPACE_REGEX.pattern() + ") in namespace of id: " + namespace + ":" + path);
        } else {
            return namespace;
        }
    }

    private static String assertValidPath(final String namespace, final String path) {
        if (!isValidPath(path)) {
            throw new IdException("Invalid character (" + VALID_PATH_REGEX.pattern() + ") in path of id: " + namespace + ":" + path);
        } else {
            return path;
        }
    }

    public Identifier vanilla() {
        return new Identifier(this.namespace, this.path);
    }

    @Override
    public String identifier() {
        return this.path;
    }

    public Id withNamespace(final String namespace) {
        return new Id(assertValidNamespace(namespace, this.path), this.path);
    }

    public Id withPath(final String path) {
        return new Id(this.namespace, assertValidPath(this.namespace, path));
    }

    @Override
    public @NotNull String toString() {
        return this.namespace + ":" + this.path;
    }

    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    @Override
    public int compareTo(final @NotNull Id id) {
        int result = this.path.compareTo(id.path);
        if (result == 0) {
            result = this.namespace.compareTo(id.namespace);
        }

        return result;
    }

    private static class IdException extends RuntimeException {
        public IdException(final String message) {
            super(message);
        }
    }
}
