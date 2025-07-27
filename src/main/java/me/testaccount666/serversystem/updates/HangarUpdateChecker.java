package me.testaccount666.serversystem.updates;

import me.testaccount666.serversystem.utils.Version;

import java.net.URI;

public class HangarUpdateChecker extends AbstractUpdateChecker {
    private static final String _DOWNLOAD_URL_TEMPLATE = "https://hangar.papermc.io/api/v1/projects/TestAccount666/ServerSystem/versions/%s/PAPER/download";

    public HangarUpdateChecker() {
        super(URI.create("https://hangar.papermc.io/api/v1/projects/TestAccount666/ServerSystem/latest?channel=release"));
    }

    @Override
    protected Version parseLatestVersion(String responseBody) {
        return new Version(responseBody.trim());
    }

    @Override
    protected String getDownloadUrl() {
        return String.format(_DOWNLOAD_URL_TEMPLATE, latestVersion.toString());
    }
}
