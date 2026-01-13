package me.testaccount666.serversystem.commands.executables.sudo

import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.ServerSystem.Companion.log
import me.testaccount666.serversystem.commands.ServerSystemCommand
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand
import me.testaccount666.serversystem.commands.management.CommandManager
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.ConsoleUser
import me.testaccount666.serversystem.userdata.User
import me.testaccount666.serversystem.userdata.UserManager
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.command
import me.testaccount666.serversystem.utils.MessageBuilder.Companion.general
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.NamedElement
import net.bytebuddy.description.method.MethodDescription
import net.bytebuddy.implementation.MethodCall
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.implementation.bind.annotation.Morph
import net.bytebuddy.matcher.ElementMatchers
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.permissions.PermissibleBase
import java.lang.reflect.Method
import java.util.logging.Level

@ServerSystemCommand("sudo")
class CommandSudo : AbstractServerSystemCommand() {
    override fun execute(commandSender: User, command: Command, label: String, vararg arguments: String) {
        if (!checkBasePermission(commandSender, "Sudo.Use")) return
        if (handleConsoleWithNoTarget(commandSender, getSyntaxPath(command), label, arguments = arguments)) return

        if (arguments.isEmpty()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val targetUser = getTargetUser(commandSender, returnSender = false, arguments = arguments)
        if (targetUser == null) {
            general("PlayerNotFound", commandSender) { target(arguments[0]) }.build()
            return
        }

        val targetPlayer = targetUser.getPlayer()!!
        val isSelf = targetUser === commandSender

        // No inception here. You can't sudo yourself. Nice try, DiCaprio.
        if (isSelf) {
            command("Sudo.CannotSudoSelf", commandSender).build()
            return
        }

        if (arguments.size <= 1) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        val sudoCommand: String = arguments[1]

        if (sudoCommand.isBlank()) {
            general("InvalidArguments", commandSender) {
                syntax(getSyntaxPath(command))
                label(label)
            }.build()
            return
        }

        if (commandSender !is ConsoleUser && hasCommandPermission(targetPlayer, "Sudo.Exempt", false)) {
            command("Sudo.CannotSudoExempt", commandSender) { target(targetPlayer.name) }.build()
            return
        }

        val cachedSender = instance.registry.getService<UserManager>().getUserOrNull(commandSender.uuid)
        if (cachedSender == null) {
            log.warning("(CommandSudo) Couldn't find cached command sender?!")
            general("ErrorOccurred", commandSender) { label(label) }.build()
            return
        }

        targetUser.addMessageListener(cachedSender)

        command("Sudo.Success", commandSender) {
            target(targetPlayer.name)
            postModifier { it.replace("<COMMAND>", sudoCommand) }
        }.build()

        if (!sudoCommand.startsWith("/")) {
            targetPlayer.chat(sudoCommand)
            return
        }

        val hookedTargetPlayer = createHookedPlayer(targetPlayer, commandSender.commandSender!!)

        if (hookedTargetPlayer == null) {
            general("ErrorOccurred", commandSender) { label(label) }.build()
            return
        }

        val sudoArguments = arguments.drop(2).toTypedArray()

        val commandManager = instance.registry.getService(CommandManager::class.java)
        val foundCommand = commandManager.getCommand(sudoCommand.substring(1))
        if (foundCommand == null) {
            val tempArgumentList = ArrayList<String>()
            tempArgumentList.add(sudoCommand)
            tempArgumentList.addAll(sudoArguments)

            val commandEvent = PlayerCommandPreprocessEvent(hookedTargetPlayer, tempArgumentList.joinToString { " " }.trim { it <= ' ' })
            Bukkit.getPluginManager().callEvent(commandEvent)
            targetUser.removeMessageListener(cachedSender)
            return
        }

        foundCommand.execute(hookedTargetPlayer, sudoCommand.substring(1), sudoArguments)
        targetUser.removeMessageListener(cachedSender)
    }

    private fun createHookedPlayer(targetPlayer: Player, commandSender: CommandSender): Player? {
        var targetPlayer = targetPlayer
        ByteBuddy().subclass(targetPlayer.javaClass)
            .method(
                ElementMatchers.named<NamedElement>("sendMessage")
                    .and<MethodDescription> { it.isPublic }
            )
            .intercept(
                MethodCall.invokeSuper().withAllArguments()
                    .andThen(
                        MethodDelegation.withDefaultConfiguration()
                            .withBinders(Morph.Binder.install(IMorpher::class.java))
                            .to(MessageInterceptor(commandSender))
                    )
            )
            .make().use { hookedPlayer ->
                val loadedClass = hookedPlayer.load(javaClass.classLoader).getLoaded()
                if (_GetHandleMethod == null) try {
                    _GetHandleMethod = targetPlayer.javaClass.getDeclaredMethod("getHandle")
                    _GetHandleMethod!!.isAccessible = true
                } catch (exception: NoSuchMethodException) {
                    log.log(Level.WARNING, "(CommandSudo) Couldn't find getHandle method!", exception)
                    return null
                }
                try {
                    //TODO: Replace with FieldAccessor
                    val permField = Class.forName("org.bukkit.craftbukkit.entity.CraftHumanEntity").getDeclaredField("perm")

                    permField.isAccessible = true

                    val permissibleBase = permField.get(targetPlayer) as PermissibleBase?

                    targetPlayer =
                        loadedClass.declaredConstructors[0].newInstance(Bukkit.getServer(), _GetHandleMethod!!.invoke(targetPlayer)) as Player

                    permField.set(targetPlayer, permissibleBase)
                } catch (exception: Exception) {
                    log.log(Level.WARNING, "(CommandSudo) Couldn't hook player!", exception)
                    return null
                }
            }
        return targetPlayer
    }

    override fun getSyntaxPath(command: Command?): String = "Sudo"

    override fun hasCommandAccess(player: Player, command: Command): Boolean {
        return hasCommandPermission(player, "Sudo.Use", false)
    }

    companion object {
        private var _GetHandleMethod: Method? = null
    }
}
