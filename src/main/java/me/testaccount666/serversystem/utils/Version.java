package me.testaccount666.serversystem.utils;

public class Version implements Comparable<Version> {
    private final String _version;

    public Version(String version) {
        _version = version;
    }

    @Override
    public int compareTo(Version other) {
        var thisVersion = _version.split("\\.");
        var otherVersion = other._version.split("\\.");

        for (var index = 0; index < Math.min(thisVersion.length, otherVersion.length); index++) {
            var thisVersionInt = Integer.parseInt(thisVersion[index]);
            var otherVersionInt = Integer.parseInt(otherVersion[index]);

            if (thisVersionInt != otherVersionInt) return Integer.compare(thisVersionInt, otherVersionInt);
        }
        return Integer.compare(thisVersion.length, otherVersion.length);
    }
}
