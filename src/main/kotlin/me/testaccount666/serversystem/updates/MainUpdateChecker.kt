package me.testaccount666.serversystem.updates

import me.testaccount666.serversystem.utils.Version
import java.net.URI
import java.util.regex.Pattern

class MainUpdateChecker : AbstractUpdateChecker(URI.create("https://pluginsupport.zapto.org/PluginSupport/ServerSystem2/")) {
    override fun parseLatestVersion(responseBody: String): Version {
        val pattern = Pattern.compile("""<a href="(\d+(?:\.\d+)*)">""")
        val matcher = pattern.matcher(responseBody)
        var foundLatestVersion: Version? = null

        while (matcher.find()) {
            val versionString = matcher.group(1)
            val parsedVersion = Version(versionString)

            if (foundLatestVersion == null) {
                foundLatestVersion = parsedVersion
                continue
            }

            if (parsedVersion <= foundLatestVersion) continue
            foundLatestVersion = parsedVersion
        }

        checkNotNull(foundLatestVersion) { "No version found in response body" }

        return foundLatestVersion
    }

    override val downloadUrl: String
        get() = updateURI.toString() + latestVersion?.version
}