package me.testaccount666.serversystem.clickablesigns

import me.testaccount666.serversystem.userdata.User
import org.bukkit.block.Sign

fun interface SignConfigurator {
    fun execute(user: User, sign: Sign)
}
