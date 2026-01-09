package me.testaccount666.serversystem.utils

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.managers.messages.MessageManager
import me.testaccount666.serversystem.userdata.User
import java.util.*
import java.util.function.UnaryOperator

class MessageBuilder private constructor(
    private val messagePath: String,
    private val type: Type?,
    private val receiver: User
) {
    var send: Boolean = true
    var prefix: Boolean = true
    var format: Boolean = true
    var preModifier: UnaryOperator<String>? = null
    var postModifier: UnaryOperator<String>? = null
    var sender: String? = null
    var target: String? = null
    var label: String? = null
    var syntax: String? = null
    var language: String? = null

    fun build(): Optional<String> {
        val path = when (type) {
            Type.GENERAL -> "General.$messagePath"
            Type.COMMAND -> "Commands.$messagePath"
            Type.SYNTAX -> "Syntax.$messagePath"
            Type.CLICKABLE_SIGN -> "ClickableSigns.$messagePath"
            else -> error("Unknown message type: $type")
        }

        val messageOptional = if (language != null)
            MessageManager.getMessage(receiver, path, language!!)
        else
            MessageManager.getMessage(receiver, path)

        if (messageOptional.isEmpty) {
            if (messagePath.equals("ErrorOccurred", true)) {
                log.severe("'ErrorOccurred' message could not be found, this is a critical error!")
                log.severe("Please report this error to the server administrator!")
                receiver.sendMessage("Something went seriously wrong! Please contact an administrator of this server!")
                return Optional.empty()
            }
            general("ErrorOccurred", receiver).build()
            return Optional.empty()
        }

        var message = messageOptional.get()

        preModifier?.let { message = it.apply(message) }
        sender?.let { message = message.replace("<SENDER>", it) }
        target = target ?: receiver.getName().orElse("Unknown")

        syntax?.let {
            val syntaxOptional = syntax(it, receiver)
                .apply {
                    sender = this@MessageBuilder.sender
                    prefix = false
                    target = this@MessageBuilder.target
                    label = this@MessageBuilder.label
                    send = false
                }.build()
            message = message.replace("<USAGE>", syntaxOptional.orElse("!!ERROR!!"))
        }

        postModifier?.let { message = it.apply(message) }

        val formatted = if (format) MessageManager.formatMessage(message, receiver, target, label, prefix) else message
        if (send) receiver.sendMessage(formatted)

        return Optional.of(formatted)
    }

    /*TODO:
        ==========
        Remove after Kotlin migration!!!!
        ==========
     */

    fun sender(value: String) = apply { sender = value }
    fun prefix(value: Boolean) = apply { prefix = value }
    fun format(value: Boolean) = apply { format = value }
    fun send(value: Boolean) = apply { send = value }
    fun target(value: String) = apply { target = value }
    fun label(value: String) = apply { label = value }
    fun syntax(value: String) = apply { syntax = value }
    fun language(value: String) = apply { language = value }
    fun preModifier(value: UnaryOperator<String>) = apply { preModifier = value }
    fun postModifier(value: UnaryOperator<String>) = apply { postModifier = value }

    /*TODO:
        ==========
        Remove after Kotlin migration!!!!
        ==========
    */

    enum class Type { GENERAL, COMMAND, CLICKABLE_SIGN, SYNTAX }

    companion object {
        @JvmStatic
        fun general(path: String, receiver: User) = of(Type.GENERAL, path, receiver)

        @JvmStatic
        fun command(path: String, receiver: User) = of(Type.COMMAND, path, receiver)
        fun syntax(path: String, receiver: User) = of(Type.SYNTAX, path, receiver)

        @JvmStatic
        fun sign(path: String, receiver: User) = of(Type.CLICKABLE_SIGN, path, receiver)
        fun of(type: Type?, path: String, receiver: User) = MessageBuilder(path, type, receiver)
    }
}

