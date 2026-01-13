package me.testaccount666.serversystem.clickablesigns.executables.kit

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator
import me.testaccount666.serversystem.clickablesigns.SignType
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.utils.ChatColor.Companion.stripColor
import me.testaccount666.serversystem.utils.ComponentColor.Companion.componentToString
import me.testaccount666.serversystem.utils.ComponentColor.Companion.translateToComponent
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

class ConfiguratorKitSign : AbstractSignConfigurator() {
    override val createPermissionNode = "ClickableSigns.Kit.Create"
    override val successMessageKey = "Kit.Created"
    override val signType = SignType.KIT

    override fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration): Boolean {
        val kitManager = instance.registry.getService<KitManager>()

        val front = sign.getSide(Side.FRONT)
        val kitName = componentToString(front.line(1))
        if (kitName.isEmpty()) {
            sign("Kit.NoKitSpecified", user).build()
            return false
        }

        if (!kitManager.kitExists(kitName.lowercase(Locale.getDefault()))) {
            sign("Kit.KitNotFound", user) {
                postModifier { it.replace("<KIT>", kitName) }
            }.build()
            return false
        }

        front.line(0, translateToComponent(SignType.KIT.signName()))
        front.line(1, translateToComponent("&2${kitName}"))
        val back = sign.getSide(Side.BACK)
        for (index in 0..3) back.line(index, front.line(index))
        sign.update()
        return true
    }

    override fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration) {
        var kitName = sign.getSide(Side.FRONT).getLine(1)
        kitName = stripColor(kitName)
        config.set("KitName", kitName)
    }
}