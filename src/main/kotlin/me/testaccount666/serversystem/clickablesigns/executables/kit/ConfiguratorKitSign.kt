package me.testaccount666.serversystem.clickablesigns.executables.kit

import me.testaccount666.paperktx.colors.ChatColor.Companion.stripColor
import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.paperktx.extensions.ComponentExtensions.asString
import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator
import me.testaccount666.serversystem.clickablesigns.SignType
import me.testaccount666.serversystem.commands.executables.kit.manager.KitManager
import me.testaccount666.serversystem.extensions.getService
import me.testaccount666.serversystem.extensions.signMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration

class ConfiguratorKitSign : AbstractSignConfigurator() {
    override val createPermissionNode = "ClickableSigns.Kit.Create"
    override val successMessageKey = "Kit.Created"
    override val signType = SignType.KIT

    override fun validateConfiguration(user: User, sign: Sign, config: YamlConfiguration): Boolean {
        val front = sign.getSide(Side.FRONT)
        val kitName = front.line(1).asString().takeUnless { it.isEmpty() } ?: run {
            user.signMsg("Kit.NoKitSpecified")
            return false
        }

        if (!getService<KitManager>().kitExists(kitName)) {
            user.signMsg("Kit.KitNotFound") {
                postModifier { it.replace("<KIT>", kitName) }
            }
            return false
        }

        front.line(0, SignType.KIT.signName.asComponent())
        front.line(1, "&2${kitName}".asComponent())
        val back = sign.getSide(Side.BACK)
        for (index in 0..3) back.line(index, front.line(index))
        sign.update()
        return true
    }

    override fun addSignSpecificConfiguration(user: User, sign: Sign, config: FileConfiguration) {
        var kitName = sign.getSide(Side.FRONT).getLine(1)
        kitName = stripColor(kitName)
        config["KitName"] = kitName
    }
}