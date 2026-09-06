package me.testaccount666.serversystem.commands.executables.signcost

import me.testaccount666.paperktx.extensions.ComponentExtensions.asComponent
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.clickablesigns.cost.CostType
import me.testaccount666.serversystem.clickablesigns.util.SignUtils.getSignFile
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.SimpleCompletion
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.*
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.money.EconomyProvider
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.command.Command
import org.bukkit.configuration.file.YamlConfiguration
import java.io.IOException
import java.math.BigDecimal
import java.util.logging.Level

@ServerSystemCommand(
    "signcost", simpleCompletions = [
        SimpleCompletion(0, ["none", "exp", "economy"])
    ]
)
class CommandSignCost : AbstractServerSystemCommand() {
    override fun minRequiredArguments(command: Command) = 1
    override fun getUsagePermission(command: Command) = "SignCost.Use"
    override fun getSyntaxPath(command: Command?) = "SignCost"

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!isPlayer(commandSender)) return

        val costTypeStr = arguments[0].lowercase()
        if (!_COST_TYPES.contains(costTypeStr)) {
            commandSender.signMsg("Cost.InvalidType") {
                postModifier { it.replace("<TYPES>", _COST_TYPES.joinToString { ", " }) }
            }
            return
        }

        val costType = runCatching {
            CostType.valueOf(costTypeStr.uppercase())
        }.getOrNull() ?: run {
            commandSender.signMsg("Cost.InvalidType") {
                postModifier { it.replace("<TYPES>", _COST_TYPES.joinToString { ", " }) }
            }
            return
        }

        var amount = 0.0
        if (costType != CostType.NONE) {
            if (arguments.size < 2) {
                commandSender.generalMsg("InvalidArguments") {
                    syntax(getSyntaxPath(command))
                    label(label)
                }
                return
            }

            amount = arguments[1].toDoubleOrNull() ?: -1.0
            if (amount <= 0) {
                commandSender.signMsg("Cost.InvalidAmount")
                return
            }
        }
        val player = commandSender.getPlayer()!!

        val targetBlock = player.getTargetBlockExact(_MAX_DISTANCE).takeIf { it?.state is Sign } ?: run {
            commandSender.signMsg("Cost.NotLookingAtSign")
            return
        }
        val sign = targetBlock.state as Sign

        val signFile = getSignFile(sign.location)
        if (!signFile.exists()) {
            commandSender.signMsg("Cost.NotClickableSign")
            return
        }

        val config = YamlConfiguration.loadConfiguration(signFile)
        if ("Key" !in config) {
            commandSender.signMsg("Cost.NotClickableSign")
            return
        }

        config["Cost.Type"] = costType.name
        config["Cost.Amount"] = amount

        try {
            config.save(signFile)
        } catch (exception: IOException) {
            commandSender.generalMsg("ErrorOccurred")
            log.log(Level.SEVERE, "Error occurred while saving sign cost config '${signFile.absolutePath}'", exception)
            return
        }

        if (costType == CostType.NONE) {
            sign.setLine(3, "")
            sign.update()
            commandSender.signMsg("Cost.SetNone")
            return
        }
        val costLine = if (costType == CostType.EXP) "${amount.toInt()} EXP"
        else getService<EconomyProvider>().formatMoney(BigDecimal(amount))

        sign.getSide(Side.FRONT).line(3, "&6${costLine}".asComponent())
        sign.getSide(Side.BACK).line(3, "&6${costLine}".asComponent())
        sign.update()

        commandSender.signMsg("Cost.Set") {
            postModifier {
                it.replace("<TYPE>", costType.name)
                    .replace("<AMOUNT>", amount.toString())
            }
        }
    }

    companion object {
        private val _COST_TYPES = setOf("none", "exp", "economy")
        private const val _MAX_DISTANCE = 5
    }
}