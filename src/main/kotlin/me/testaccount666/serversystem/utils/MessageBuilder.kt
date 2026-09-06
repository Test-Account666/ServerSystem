package me.testaccount666.serversystem.utils

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.extensions.syntaxMsg
import me.testaccount666.serversystem.managers.messages.MessageManager
import me.testaccount666.serversystem.userdata.User

class MessageBuilder internal constructor(
    private val messagePath: String,
    private val type: Type,
    private val receiver: User,
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

        var message = MessageManager.getMessage(receiver, path, language) ?: run {
            if (messagePath.equals("ErrorOccurred", true)) {
                ServerSystem.log.severe("'ErrorOccurred' message could not be found!")
                receiver.sendMessage("Something went seriously wrong! Please contact an administrator!")
                return if (blankError) "" else "!!ERROR!!"
            }
            return if (blankError) "" else receiver.generalMsg("ErrorOccurred")
        }

        message = preModifier?.invoke(message) ?: message
        sender?.let { message = message.replace("<SENDER>", it) }
        val resolvedTarget = target ?: receiver.nameSafe

        syntax?.let {
            val usage = receiver.syntaxMsg(it) {
                builder.sender = sender
                builder.prefix = false
                builder.target = resolvedTarget
                builder.label = label
                builder.send = false
            }
            message = message.replace("<USAGE>", usage)
        }

        message = postModifier?.invoke(message) ?: message

        val formatted = if (!format) message
        else MessageManager.formatMessage(message, receiver, resolvedTarget, label, prefix)

        return formatted.also { if (send) receiver.sendMessage(it) }
    }

    enum class Type { GENERAL, COMMAND, CLICKABLE_SIGN, SYNTAX }

    class Builder(internal val builder: MessageBuilder) {
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
