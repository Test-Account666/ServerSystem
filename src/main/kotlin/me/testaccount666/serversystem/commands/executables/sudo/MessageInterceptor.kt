package me.testaccount666.serversystem.commands.executables.sudo

import me.testaccount666.serversystem.ServerSystem.Companion.log
import net.bytebuddy.implementation.bind.annotation.AllArguments
import net.bytebuddy.implementation.bind.annotation.Morph
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.This
import org.bukkit.command.CommandSender
import java.lang.reflect.Method
import java.util.logging.Level

class MessageInterceptor(private val _commandSender: CommandSender?) {
    @RuntimeType
    fun intercept(@This obj: Any?, @AllArguments allArguments: Array<Any>, @Morph morpher: IMorpher?) {
        val sendMessageMethod = findMatchingMethod(allArguments) ?: return

        invokeSendMessage(sendMessageMethod, allArguments)
    }

    private fun findMatchingMethod(allArguments: Array<Any>): Method? {
        return CommandSender::class.java.declaredMethods.firstOrNull { hasMatchingParameters(it, allArguments) }
    }

    private fun hasMatchingParameters(method: Method, arguments: Array<Any>): Boolean {
        val parameters = method.parameterTypes

        if (parameters.size != arguments.size) return false

        for (index in parameters.indices) {
            if (parameters[index].canonicalName == arguments[index].javaClass.canonicalName) continue
            return false
        }

        return true
    }

    private fun invokeSendMessage(method: Method, arguments: Array<Any>) {
        try {
            method.invoke(_commandSender, *arguments)
        } catch (exception: Exception) {
            log.log(Level.WARNING, "(MessageInterceptor) Couldn't invoke sendMessage method!", exception)
        }
    }
}
