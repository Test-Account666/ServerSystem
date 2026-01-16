package me.testaccount666.serversystem.utils

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.messages.MessageManager
import me.testaccount666.serversystem.userdata.User

class MessageBuilder private constructor(
    private val messagePath: String,
    private val type: Type,
    private val receiver: User
) {
    private var send = true
    private var prefix = true
    private var format = true
    private var blankError = false
    private var preModifier: ((String) -> String)? = null
    private var postModifier: ((String) -> String)? = null
    private var sender: String? = null
    private var target: String? = null
    private var label: String? = null
    private var syntax: String? = null
    private var language: String? = null

    fun build(): String {
        val path = when (type) {
            Type.GENERAL -> "General.$messagePath"
            Type.COMMAND -> "Commands.$messagePath"
            Type.SYNTAX -> "Syntax.$messagePath"
            Type.CLICKABLE_SIGN -> "ClickableSigns.$messagePath"
        }

        var message = MessageManager.getMessage(receiver, path, language)

        if (message == null) {
            if (messagePath.equals("ErrorOccurred", ignoreCase = true)) {
                ServerSystem.log.severe("'ErrorOccurred' message could not be found!")
                receiver.sendMessage("Something went seriously wrong! Please contact an administrator!")
                return if (blankError) "" else "!!ERROR!!"
            }
            val err = general("ErrorOccurred", receiver).build()
            return if (blankError) "" else err
        }

        message = preModifier?.invoke(message) ?: message
        sender?.let { message = message.replace("<SENDER>", it) }
        val resolvedTarget = target ?: receiver.getNameSafe()

        syntax?.let {
            val usage = syntax(it, receiver).apply {
                sender = this@MessageBuilder.sender
                prefix = false
                target = resolvedTarget
                label = this@MessageBuilder.label
                send = false
            }.build()
            message = message?.replace("<USAGE>", usage)
        }

        message = postModifier?.invoke(message!!) ?: message

        val formatted = if (!format) message
        else MessageManager.formatMessage(message, receiver, resolvedTarget, label, prefix)

        formatted ?: return if (blankError) "" else "!!ERROR!!"

        if (send) receiver.sendMessage(formatted)
        return formatted
    }

    enum class Type { GENERAL, COMMAND, CLICKABLE_SIGN, SYNTAX }

    companion object {
        fun general(path: String, receiver: User, block: Builder.() -> Unit = {}) =
            MessageBuilder(path, Type.GENERAL, receiver).apply {
                Builder(this).apply(block)
            }

        fun command(path: String, receiver: User, block: Builder.() -> Unit = {}) =
            MessageBuilder(path, Type.COMMAND, receiver).apply {
                Builder(this).apply(block)
            }

        fun syntax(path: String, receiver: User, block: Builder.() -> Unit = {}) =
            MessageBuilder(path, Type.SYNTAX, receiver).apply {
                Builder(this).apply(block)
            }

        fun sign(path: String, receiver: User, block: Builder.() -> Unit = {}) =
            MessageBuilder(path, Type.CLICKABLE_SIGN, receiver).apply {
                Builder(this).apply(block)
            }
    }

    class Builder(private val builder: MessageBuilder) {
        fun sender(value: String) = apply { builder.sender = value }
        fun prefix(value: Boolean) = apply { builder.prefix = value }
        fun format(value: Boolean) = apply { builder.format = value }
        fun send(value: Boolean) = apply { builder.send = value }
        fun target(value: String?) = apply { builder.target = value }
        fun label(value: String) = apply { builder.label = value }
        fun syntax(value: String) = apply { builder.syntax = value }
        fun language(value: String) = apply { builder.language = value }
        fun preModifier(fn: (String) -> String) = apply { builder.preModifier = fn }
        fun postModifier(fn: (String) -> String) = apply { builder.postModifier = fn }
        fun blankError(fn: Boolean) = apply { builder.blankError = fn }
    }
}
