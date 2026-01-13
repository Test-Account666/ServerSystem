package me.testaccount666.serversystem.clickablesigns

import me.testaccount666.serversystem.userdata.User
import org.bukkit.block.Sign

interface SignClickAction {
    fun execute(user: User, sign: Sign)

    /**
     * The permission node for the permission required to use this sign.
     * 
     * @return The permission node
     */
    val usePermissionNode: String

    /**
     * The permission node for the permission required to break this+ sign.
     * 
     * @return The permission node
     */
    val destroyPermissionNode: String

    val basePermissionNode: String
}
