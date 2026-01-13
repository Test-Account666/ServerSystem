package me.testaccount666.serversystem.clickablesigns.executables.give

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator
import me.testaccount666.serversystem.clickablesigns.SignManager
import me.testaccount666.serversystem.clickablesigns.SignType
import me.testaccount666.serversystem.clickablesigns.cost.CostType
import me.testaccount666.serversystem.clickablesigns.util.SignUtils
import me.testaccount666.serversystem.managers.PermissionManager.getPermission
import me.testaccount666.serversystem.managers.PermissionManager.hasPermission
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.BiDirectionalHashMap
import me.testaccount666.serversystem.utils.ComponentColor.Companion.translateToComponent
import me.testaccount666.serversystem.utils.ItemStackExtensions.Companion.isAir
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
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
        sign("Give.Configuring", user).build()
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
            sign("Give.NoItem", user).build()
            return
        }

        val signFile = SignUtils.getSignFile(sign.location)
        val config = YamlConfiguration.loadConfiguration(signFile)

        config.set("Key", signType.name)

        config.set("Cost.Type", CostType.NONE.name)
        config.set("Cost.Amount", 0)

        config.set("Item", itemToGive)

        try {
            config.save(signFile)
        } catch (exception: IOException) {
            user.sendMessage(exception.message ?: "null")
            log.log(Level.SEVERE, "Failed to save sign configuration ${signFile.absolutePath}", exception)
            return
        }

        instance.registry.getService<SignManager>().addSignType(sign.location, signType)
        _CONFIGURATORS.removeByValue(sign)

        val front = sign.getSide(Side.FRONT)
        front.line(0, translateToComponent(SignType.GIVE.signName()))
        front.line(1, translateToComponent("&2${itemToGive.type.name}"))
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
            general("NoPermission", user) {
                postModifier {
                    it.replace("<PERMISSION>", getPermission(createPermissionNode)!!)
                }
            }.build()
            return false
        }
        return true
    }

    companion object {
        private val _CONFIGURATORS = BiDirectionalHashMap<User, Sign>()
    }
}