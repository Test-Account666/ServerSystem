package me.testaccount666.serversystem.commands.executables.sudo

fun interface IMorpher {
    fun invoke(args: Array<Any>?): Any?
}
