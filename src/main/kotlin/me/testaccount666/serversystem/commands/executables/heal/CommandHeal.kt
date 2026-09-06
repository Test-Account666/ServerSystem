package me.testaccount666.serversystem.commands.executables.heal

import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.extensions.commandMsg
import me.testaccount666.serversystem.extensions.generalMsg
import me.testaccount666.serversystem.userdata.User
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.potion.PotionEffectType

@ServerSystemCommand("heal", ["feed"])
class CommandHeal : AbstractServerSystemCommand() {
    private val _badEffectsSet = setOf(
        PotionEffectType.INSTANT_DAMAGE, PotionEffectType.INFESTED,
        PotionEffectType.SLOWNESS, PotionEffectType.BAD_OMEN, PotionEffectType.DARKNESS, PotionEffectType.GLOWING,
        PotionEffectType.UNLUCK, PotionEffectType.BLINDNESS, PotionEffectType.HUNGER, PotionEffectType.MINING_FATIGUE,
        PotionEffectType.NAUSEA, PotionEffectType.OOZING, PotionEffectType.POISON, PotionEffectType.WITHER,
        PotionEffectType.WEAKNESS, PotionEffectType.LEVITATION
    )

    override fun getUsagePermission(command: Command): String {
        if (command.name.equals("heal", true)) return "Heal.Use"
        return "Feed.Use"
    }

    override fun getSyntaxPath(command: Command?): String {
        val commandName = command?.name?.lowercase() ?: error("(CommandHeal;SyntaxPath) Command is null")

        return when (commandName) {
            "heal" -> "Heal"
            "feed" -> "Feed"
            else -> error("(CommandHeal;SyntaxPath) Unexpected value: $commandName")
        }
    }

    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (command.name.equals("heal", true)) {
            handleHealCommand(commandSender, command, label, *arguments)
            return
        }

        handleFeedCommand(commandSender, command, label, *arguments)
    }

    private fun handleFeedCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Feed.Other", targetPlayer.name)) return

        targetPlayer.foodLevel = 20
        targetPlayer.saturation = 20f
        targetPlayer.sendHealthUpdate()

        val messagePath = if (isSelf) "Feed.Success" else "Feed.SuccessOther"

        commandSender.commandMsg(messagePath) { target(targetPlayer.name) }

        if (isSelf) return
        targetUser.commandMsg("Feed.Success") {
            sender(commandSender.nameSafe)
            target(targetPlayer.name)
        }
    }

    private fun handleHealCommand(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (isConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        val targetUser = getTargetUser(commandSender, arguments = arguments) ?: run {
            commandSender.generalMsg("PlayerNotFound") { target(arguments[0]) }
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        if (!isSelf && !checkPermission(commandSender, "Heal.Other", targetPlayer.name)) return

        targetPlayer.health = targetPlayer.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        targetPlayer.foodLevel = 20
        targetPlayer.saturation = 20f
        targetPlayer.fireTicks = 0
        _badEffectsSet.forEach(targetPlayer::removePotionEffect)
        targetPlayer.sendHealthUpdate()

        val messagePath = if (isSelf) "Heal.Success" else "Heal.SuccessOther"

        commandSender.commandMsg(messagePath) { target(targetPlayer.name) }

        if (isSelf) return
        targetUser.commandMsg("Heal.Success") {
            sender(commandSender.nameSafe)
            target(targetPlayer.name)
        }
    }
}
