package me.testaccount666.serversystem.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Version implements Comparable<Version> {
    @Getter
    private final String _version;

    public Version(String version) {
        if (version == null || version.trim().isEmpty()) throw new IllegalArgumentException("Version string cannot be null or empty");

        var normalizedVersion = version.trim();

        if (!normalizedVersion.matches("^\\d+(\\.\\d+)*$"))
            throw new IllegalArgumentException("Invalid version format: ${version}. Expected format: x.y.z (numeric segments separated by dots)");

        _version = normalizedVersion;
    }

    @Override
    public int compareTo(@NotNull Version other) {
        var thisVersion = _version.split("\\.");
        var otherVersion = other._version.split("\\.");

        for (var index = 0; index < Math.min(thisVersion.length, otherVersion.length); index++)
            try {
                var thisVersionInt = Integer.parseInt(thisVersion[index]);
                var otherVersionInt = Integer.parseInt(otherVersion[index]);

                if (thisVersionInt != otherVersionInt) return Integer.compare(thisVersionInt, otherVersionInt);
            } catch (NumberFormatException exception) {
                throw new IllegalStateException("Invalid numeric segment in version: ${_version} or ${other._version}", exception);
            }
        return Integer.compare(thisVersion.length, otherVersion.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Version version)) return false;
        return compareTo(version) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_version);
    }

    @Override
    public String toString() {
        return _version;
    }
}
