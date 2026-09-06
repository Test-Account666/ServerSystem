package me.testaccount666.serversystem.clickablesigns.executables.give

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.paperktx.extensions.isAir
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.clickablesigns.*
import me.testaccount666.serversystem.clickablesigns.cost.CostType
import me.testaccount666.serversystem.clickablesigns.util.SignUtils
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.managers.PermissionManager.getPermission
import me.testaccount666.serversystem.managers.PermissionManager.hasPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.BiDirectionalHashMap
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.io.IOException
import java.util.logging.Level

class ConfiguratorGiveSign : AbstractSignConfigurator(), Listener {
    override val createPermissionNode = "ClickableSigns.Give.Create"
    override val successMessageKey = "Give.Created"
    override val signType = SignType.GIVE

    override fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration) = true

    override fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration) {
    }

    override fun execute(user: User, sign: Sign) {
        if (!validatePermission(user)) return

        _CONFIGURATORS.put(user, sign)
        user.signMsg("Give.Configuring")
    }

    @EventHandler
    fun onSignRightClick(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val clickedBlock = event.clickedBlock ?: return
        val sign = clickedBlock.state as? Sign ?: return

        val user = _CONFIGURATORS.getKey(sign) ?: return
        if (user.uuid != event.getPlayer().uniqueId) return
        event.isCancelled = true

        val itemToGive = event.getPlayer().inventory.itemInMainHand
        if (itemToGive.isAir()) {
            user.signMsg("Give.NoItem")
            return
        }

        val signFile = SignUtils.getSignFile(sign.location)
        val config = YamlConfiguration.loadConfiguration(signFile)

        config["Key"] = signType.name

        config["Cost.Type"] = CostType.NONE.name
        config["Cost.Amount"] = 0

        config["Item"] = itemToGive

        try {
            config.save(signFile)
        } catch (exception: IOException) {
            user.sendMessage(exception.message ?: "null")
            log.log(Level.SEVERE, "Failed to save sign configuration ${signFile.absolutePath}", exception)
            return
        }

        getService<SignManager>().addSignType(sign.location, signType)
        _CONFIGURATORS.removeByValue(sign)

        val front = sign.getSide(Side.FRONT)
        front.line(0, SignType.GIVE.signName.asComponent())
        front.line(1, "&2${itemToGive.type.name}".asComponent())
        val back = sign.getSide(Side.BACK)
        for (index in 0..3) back.line(index, front.line(index))
        sign.update()
    }

    /**
     * Validates that the user has permission to create this sign.
     *
     * @param user The user to check
     * @return true if the user has permission, false otherwise
     */
    private fun validatePermission(user: User): Boolean {
        if (!hasPermission(user, createPermissionNode, false)) {
            user.generalMsg("NoPermission") {
                postModifier {
                    it.replace("<PERMISSION>", getPermission(createPermissionNode)!!)
                }
            }
            return false
        }
        return true
    }

    companion object {
        private val _CONFIGURATORS = BiDirectionalHashMap<User, Sign>()
    }
}