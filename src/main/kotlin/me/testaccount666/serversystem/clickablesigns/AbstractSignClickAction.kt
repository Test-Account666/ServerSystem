package me.testaccount666.serversystem.clickablesigns

import me.testaccount666.serversystem.clickablesigns.cost.CostHandler
import me.testaccount666.serversystem.clickablesigns.util.SignUtils
import me.testaccount666.serversystem.managers.PermissionManager.getPermission
import me.testaccount666.serversystem.managers.PermissionManager.hasPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration

/**
 * Abstract base class for sign click actions.
 * Handles common functionality such as permission checking and cost handling.
 */
abstract class AbstractSignClickAction : SignClickAction {
    override val usePermissionNode: String = "$basePermissionNode.Use"
    override val destroyPermissionNode: String = "$basePermissionNode.Destroy"

    /**
     * Executes the sign-specific action.
     * This method is called after permission and cost checks have passed.
     * 
     * @param user   The user who clicked the sign
     * @param sign   The sign that was clicked
     * @param config The sign configuration
     * @return true if the action was successful, false otherwise
     */
    protected abstract fun executeAction(user: User, sign: Sign, config: FileConfiguration): Boolean

    override fun execute(user: User, sign: Sign) {
        if (!hasPermission(user, usePermissionNode, false)) {
            general("NoPermission", user) {
                postModifier { it.replace("<PERMISSION>", getPermission(usePermissionNode)!!) }
            }.build()
            return
        }

        val config = SignUtils.loadSignConfig(sign.location)

        if (!CostHandler.deductCost(user, config)) return
        if (!executeAction(user, sign, config)) CostHandler.refundCost(user, config)
    }
}