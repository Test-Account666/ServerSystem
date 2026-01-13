package me.testaccount666.serversystem.updates

enum class UpdateCheckerType(val new: () -> AbstractUpdateChecker) {
    DISABLED({ DisabledUpdateChecker() }),
    HANGAR({ HangarUpdateChecker() }),
    MAIN({ MainUpdateChecker() });

    companion object {
        fun of(name: String) = entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}