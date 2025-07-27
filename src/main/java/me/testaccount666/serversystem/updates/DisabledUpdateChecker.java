package me.testaccount666.serversystem.updates;

import me.testaccount666.serversystem.utils.Version;

import java.util.concurrent.CompletableFuture;

public class DisabledUpdateChecker extends AbstractUpdateChecker {
    public DisabledUpdateChecker() {
        super(null);
        setAutoUpdate(false);
    }

    @Override
    public CompletableFuture<Boolean> hasUpdate() {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> downloadUpdate() {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    protected Version parseLatestVersion(String responseBody) {
        return null;
    }

    @Override
    protected String getDownloadUrl() {
        return "";
    }

    @Override
    public void setAutoUpdate(boolean autoUpdate) {
        super.setAutoUpdate(false);
    }
}
