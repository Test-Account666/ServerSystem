package me.testaccount666.serversystem.updates;

import me.testaccount666.serversystem.utils.Version;

import java.net.URI;
import java.util.regex.Pattern;

public class MainUpdateChecker extends AbstractUpdateChecker {

    public MainUpdateChecker() {
        super(URI.create("https://pluginsupport.zapto.org/PluginSupport/ServerSystem2/"));
    }

    @Override
    protected Version parseLatestVersion(String responseBody) {
        var pattern = Pattern.compile("<a href=\"(\\d+(?:\\.\\d+)*)\">");
        var matcher = pattern.matcher(responseBody);
        Version foundLatestVersion = null;

        while (matcher.find()) {
            var versionString = matcher.group(1);
            var parsedVersion = new Version(versionString);

            if (foundLatestVersion == null) {
                foundLatestVersion = parsedVersion;
                continue;
            }

            if (parsedVersion.compareTo(foundLatestVersion) <= 0) continue;
            foundLatestVersion = parsedVersion;
        }

        if (foundLatestVersion == null) throw new IllegalStateException("No version found in response body");

        return foundLatestVersion;
    }

    @Override
    protected String getDownloadUrl() {
        return updateURI.toString() + latestVersion.getVersion();
    }
}
