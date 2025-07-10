package me.testaccount666.serversystem.commands.executables.sudo;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "sudo")
public class CommandSudo extends AbstractServerSystemCommand {
    private static Method _GetHandleMethod;

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Sudo.Use")) return;
        if (handleConsoleWithNoTarget(commandSender, arguments)) return;

        var targetUserOptional = getTargetUser(commandSender, false, arguments);
        if (targetUserOptional.isEmpty()) {
            general("PlayerNotFound", commandSender).target(arguments[0]).build();
            return;
        }

        var targetUser = targetUserOptional.get();
        var targetPlayer = targetUser.getPlayer();
        var isSelf = targetUser == commandSender;

        // No inception here. You can't sudo yourself. Nice try, DiCaprio.
        if (isSelf) {
            command("Sudo.CannotSudoSelf", commandSender).build();
            return;
        }

        if (arguments.length <= 1) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        var sudoCommand = arguments[1];

        if (sudoCommand.isBlank()) {
            general("InvalidArguments", commandSender).label(label).build();
            return;
        }

        if (!(commandSender instanceof ConsoleUser) && PermissionManager.hasPermission(targetPlayer, "Commands.Sudo.Exempt", false)) {
            command("Sudo.CannotSudoExempt", commandSender).target(targetPlayer.getName()).build();
            return;
        }

        var cachedSenderOptional = ServerSystem.Instance.getUserManager().getUser(commandSender.getUuid());
        if (cachedSenderOptional.isEmpty()) {
            Bukkit.getLogger().warning("(CommandSudo) Couldn't find cached command sender?!");
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }

        var cachedSender = cachedSenderOptional.get();
        targetUser.addMessageListener(cachedSender);

        command("Sudo.Success", commandSender).target(targetPlayer.getName())
                .modifier(message -> message.replace("<COMMAND>", sudoCommand)).build();

        if (!sudoCommand.startsWith("/")) {
            targetPlayer.chat(sudoCommand);
            return;
        }

        var hookedTargetPlayer = createHookedPlayer(targetPlayer, commandSender.getCommandSender());

        if (hookedTargetPlayer == null) {
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }

        var sudoArguments = new String[arguments.length - 2];
        System.arraycopy(arguments, 2, sudoArguments, 0, sudoArguments.length);

        var commandOptional = ServerSystem.Instance.getCommandManager().getCommand(sudoCommand.substring(1));
        if (commandOptional.isEmpty()) {
            var tempArgumentList = new ArrayList<String>();
            tempArgumentList.add(sudoCommand);
            tempArgumentList.addAll(Arrays.stream(sudoArguments).toList());

            var commandEvent = new PlayerCommandPreprocessEvent(hookedTargetPlayer, String.join(" ", tempArgumentList).trim());
            Bukkit.getPluginManager().callEvent(commandEvent);
            targetUser.removeMessageListener(cachedSender);
            return;
        }

        var foundCommand = commandOptional.get();
        foundCommand.execute(hookedTargetPlayer, sudoCommand.substring(1), sudoArguments);
        targetUser.removeMessageListener(cachedSender);
    }

    private Player createHookedPlayer(Player targetPlayer, CommandSender commandSender) {
        try (var hookedPlayer = new ByteBuddy().subclass(targetPlayer.getClass())
                .method(ElementMatchers.named("sendMessage").and(ModifierReviewable.OfByteCodeElement::isPublic))
                .intercept(MethodCall.invokeSuper().withAllArguments()
                        .andThen(MethodDelegation.withDefaultConfiguration()
                                .withBinders(Morph.Binder.install(IMorpher.class))
                                .to(new MessageInterceptor(commandSender))))
                .make()) {
            var loadedClass = hookedPlayer.load(getClass().getClassLoader()).getLoaded();

            if (_GetHandleMethod == null) try {
                _GetHandleMethod = targetPlayer.getClass().getDeclaredMethod("getHandle");
                _GetHandleMethod.setAccessible(true);
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
                return null;
            }

            try {
                //TODO: Replace with FieldAccessor
                var permField = Class.forName("org.bukkit.craftbukkit.entity.CraftHumanEntity").getDeclaredField("perm");

                permField.setAccessible(true);

                var permissibleBase = (PermissibleBase) permField.get(targetPlayer);

                targetPlayer = (Player) loadedClass.getDeclaredConstructors()[0].newInstance(Bukkit.getServer(), _GetHandleMethod.invoke(targetPlayer));

                permField.set(targetPlayer, permissibleBase);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException |
                     NoSuchFieldException exception) {
                exception.printStackTrace();
                return null;
            }
        }

        return targetPlayer;
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Sudo.Use", false);
    }
}
