package me.testaccount666.serversystem.updates

import me.testaccount666.serversystem.utils.Version
import java.net.URI

class HangarUpdateChecker : AbstractUpdateChecker(URI.create("${_URL_START}latest?channel=release")) {
    override fun parseLatestVersion(responseBody: String): Version = Version(responseBody.trim { it <= ' ' })

    override val downloadUrl: String
        get() = String.format(_DOWNLOAD_URL_TEMPLATE, latestVersion.toString())

    companion object {
        private const val _URL_START = "https://hangar.papermc.io/api/v1/projects/TestAccount666/ServerSystem/";
        private const val _DOWNLOAD_URL_TEMPLATE = "${_URL_START}versions/%s/PAPER/download"
    }
}