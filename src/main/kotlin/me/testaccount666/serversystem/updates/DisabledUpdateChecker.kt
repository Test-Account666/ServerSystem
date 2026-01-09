package me.testaccount666.serversystem.updates

import me.testaccount666.serversystem.utils.Version
import java.util.concurrent.CompletableFuture

class DisabledUpdateChecker : AbstractUpdateChecker(null) {

    override var autoUpdate: Boolean
        get() = false
        set(_) {}

    override fun hasUpdate(): CompletableFuture<Boolean> = CompletableFuture.completedFuture(false)

    override fun downloadUpdate(): CompletableFuture<Boolean> = CompletableFuture.completedFuture(false)

    override fun parseLatestVersion(responseBody: String): Version? = null

    override val downloadUrl: String
        get() = ""
}