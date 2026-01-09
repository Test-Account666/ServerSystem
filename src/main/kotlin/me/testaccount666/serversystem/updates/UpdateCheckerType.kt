package me.testaccount666.serversystem.updates

import java.util.*
import java.util.function.Supplier

enum class UpdateCheckerType(val factory: Supplier<AbstractUpdateChecker>) {
    DISABLED({ DisabledUpdateChecker() }),
    HANGAR({ HangarUpdateChecker() }),
    MAIN({ MainUpdateChecker() });

    companion object {
        fun of(name: String): Optional<UpdateCheckerType> {
            return Arrays.stream(entries.toTypedArray())
                .filter { type -> type.name.equals(name, ignoreCase = true) }.findFirst()
        }
    }
}