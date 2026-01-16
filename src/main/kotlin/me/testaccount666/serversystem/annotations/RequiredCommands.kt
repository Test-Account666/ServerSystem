package me.testaccount666.serversystem.annotations

import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RequiredCommands(val requiredCommands: Array<KClass<out ServerSystemCommandExecutor>>)
