package me.testaccount666.serversystem.extensions

import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder
import me.testaccount666.serversystem.utils.MessageBuilder.Builder
import me.testaccount666.serversystem.utils.MessageBuilder.Type

fun User.generalMsg(path: String, block: Builder.() -> Unit = {}): String {
    return MessageBuilder(path, Type.GENERAL, this).apply { Builder(this).apply(block) }.build()
}

fun User.commandMsg(path: String, block: Builder.() -> Unit = {}): String {
    return MessageBuilder(path, Type.COMMAND, this).apply { Builder(this).apply(block) }.build()
}

fun User.syntaxMsg(path: String, block: Builder.() -> Unit = {}): String {
    return MessageBuilder(path, Type.SYNTAX, this).apply { Builder(this).apply(block) }.build()
}

fun User.signMsg(path: String, block: Builder.() -> Unit = {}): String {
    return MessageBuilder(path, Type.CLICKABLE_SIGN, this).apply { Builder(this).apply(block) }.build()
}
