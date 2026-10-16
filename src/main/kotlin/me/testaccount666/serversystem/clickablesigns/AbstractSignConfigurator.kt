package me.testaccount666.serversystem.clickablesigns

import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.clickablesigns.cost.CostType
import me.testaccount666.serversystem.clickablesigns.util.SignUtils
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.managers.PermissionManager.getPermission
import me.testaccount666.serversystem.managers.PermissionManager.hasPermission
import me.testaccount666.serversystem.userdata.User
import org.bukkit.block.Sign
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.IOException
import java.util.logging.Level

/**
 * Abstract base class for sign configurators.
 * Handles common functionality such as permission checking and configuration saving.
 */
abstract class AbstractSignConfigurator : SignConfigurator {
    /**
     * The permission node for the permission required to create this sign.
     *
     * @return The permission node
     */
    protected abstract val createPermissionNode: String

    /**
     * The sign type for this configurator.
     *
     * @return The sign type
     */
    protected abstract val signType: SignType

    /**
     * Validates the sign configuration.
     * This method is called before saving the configuration.
     *
     * @param user   The user who is configuring the sign
     * @param sign   The sign being configured
     * @param config The sign configuration
     * @return true if the configuration is valid, false otherwise
     */
    protected abstract fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration): Boolean

    /**
     * Adds sign-specific configuration.
     * This method is called after basic configuration has been set.
     *
     * @param user   The user who is configuring the sign
     * @param sign   The sign being configured
     * @param config The sign configuration
     */
    protected abstract fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration)

    /**
     * Gets the success message key for when the sign is successfully created.
     *
     * @return The message key
     */
    protected abstract val successMessageKey: String

    override fun execute(user: User, sign: Sign) {
        if (!hasPermission(user, createPermissionNode, false)) {
            user.generalMsg("NoPermission") {
                postModifier { it.replace("<PERMISSION>", getPermission(createPermissionNode)!!) }
            }
            return
        }

        val signFile = SignUtils.getSignFile(sign.location)
        val config = YamlConfiguration.loadConfiguration(signFile)

        config["Key"] = signType.name

        config["Cost.Type"] = CostType.NONE.name
        config["Cost.Amount"] = 0

        if (!validateConfiguration(user, sign, config)) return
        addSignSpecificConfiguration(user, sign, config)

        try {
            config.save(signFile)
        } catch (exception: IOException) {
            user.sendMessage(exception.message!!)
            log.log(Level.SEVERE, "Failed to save sign configuration ${signFile.absolutePath}", exception)
            return
        }

        getService<SignManager>().addSignType(sign.location, signType)
        user.signMsg(successMessageKey)
    }
}