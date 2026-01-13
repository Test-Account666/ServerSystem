package me.testaccount666.serversystem.commands.executables.signcost

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.clickablesigns.cost.CostType
import me.testaccount666.serversystem.clickablesigns.util.SignUtils.getSignFile
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import me.testaccount666.serversystem.utils.ComponentColor.Companion.translateToComponent
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.sign
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.command.Command
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.IOException
import java.math.BigDecimal
import java.util.*
import java.util.logging.Level

@ServerSystemCommand("signcost", [], TabCompleterSignCost::class)
class CommandSignCost : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "SignCost.Use")) return

        if (commandSender is ConsoleUser) {
            general("NotPlayer", commandSender).build()
            return
        }

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val costTypeStr = arguments[0].lowercase(Locale.getDefault())
        if (!_COST_TYPES.contains(costTypeStr)) {
            sign("Cost.InvalidType", commandSender) {
                postModifier { it.replace("<TYPES>", _COST_TYPES.joinToString { ", " }) }
            }.build()
            return
        }

        val costType: CostType?
        try {
            costType = CostType.valueOf(costTypeStr.uppercase(Locale.getDefault()))
        } catch (_: IllegalArgumentException) {
            sign("Cost.InvalidType", commandSender) {
                postModifier { it.replace("<TYPES>", _COST_TYPES.joinToString { ", " }) }
            }.build()
            return
        }

        var amount = 0.0
        if (costType != CostType.NONE) {
            if (arguments.size < 2) {
                general("InvalidArguments", commandSender) {
                    syntax(getSyntaxPath(command))
                    label(label)
                }.build()
                return
            }

            try {
                amount = arguments[1].toDouble()
                if (amount <= 0) {
                    sign("Cost.InvalidAmount", commandSender).build()
                    return
                }
            } catch (_: NumberFormatException) {
                sign("Cost.InvalidAmount", commandSender).build()
                return
            }
        }
        val player = commandSender.getPlayer()!!

        val targetBlock = player.getTargetBlock(null, _MAX_DISTANCE)
        if (targetBlock.state !is Sign) {
            command("ClickableSigns.Cost.NotLookingAtSign", commandSender).build()
            return
        }
        val sign = targetBlock.state as Sign

        val signFile = getSignFile(sign.location)
        if (!signFile.exists()) {
            sign("Cost.NotClickableSign", commandSender).build()
            return
        }

        val config = YamlConfiguration.loadConfiguration(signFile)
        if (!config.contains("Key")) {
            sign("Cost.NotClickableSign", commandSender).build()
            return
        }

        config.set("Cost.Type", costType.name)
        config.set("Cost.Amount", amount)

        try {
            config.save(signFile)
        } catch (exception: IOException) {
            general("ErrorOccurred", commandSender).build()
            log.log(Level.SEVERE, "Error occurred while saving sign cost config '${signFile.absolutePath}'", exception)
            return
        }

        if (costType == CostType.NONE) {
            sign.setLine(3, "")
            sign.update()
            command("ClickableSigns.Cost.SetNone", commandSender).build()
            return
        }
        val costLine = if (costType == CostType.EXP) "${amount.toInt()} EXP"
        else instance.registry.getService<EconomyProvider>().formatMoney(BigDecimal(amount))

        sign.getSide(Side.FRONT).line(3, translateToComponent("&6${costLine}"))
        sign.getSide(Side.BACK).line(3, translateToComponent("&6${costLine}"))
        sign.update()

        val finalAmount = amount
        sign("Cost.Set", commandSender) {
            postModifier {
                it.replace("<TYPE>", costType.name)
                    .replace("<AMOUNT>", finalAmount.toString())
            }
        }.build()
    }

    override fun getSyntaxPath(command: Command?): String = "SignCost"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "SignCost.Use", false)
    }

    companion object {
        private val _COST_TYPES = setOf("none", "exp", "economy")
        private const val _MAX_DISTANCE = 5
    }
}