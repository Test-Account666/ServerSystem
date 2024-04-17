package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.commands.CommandExecutorOverload;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Morpher;
import me.entity303.serversystem.utils.interceptors.SudoInterceptor;
import net.bytebuddy.ByteBuddy;
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

public class SudoCommand extends CommandUtils implements CommandExecutorOverload {

    private Method getHandleMethod = null;

    public SudoCommand(ServerSystem plugin) {
        super(plugin);
    }

    public static void sendMessage(CommandSender commandSender, Object... objects) {
        Method sendMessageMethod = null;
        for (var method : CommandSender.class.getDeclaredMethods()) {
            var parameters = method.getParameterTypes();
            if (parameters.length == objects.length) {
                var found = true;
                for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
                    var parameter = parameters[i];
                    if (!parameter.getCanonicalName().equals((objects[i].getClass().getCanonicalName()))) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    sendMessageMethod = method;
                    break;
                }
            }
        }
        if (sendMessageMethod != null)
            try {
                sendMessageMethod.invoke(commandSender, objects);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this.plugin.getPermissions().hasPermission(commandSender, "sudo.use")) {
            var permission = this.plugin.getPermissions().getPermission("sudo.use");
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoPermission(permission));
            return true;
        }

        if (arguments.length <= 1) {
            commandSender.sendMessage(
                    this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getSyntax(commandLabel, command, commandSender, null, "Sudo"));
            return true;
        }

        var special = this.plugin.isSpecialSudo();

        if (arguments.length >= 3)
            try {
                if (arguments[0].equalsIgnoreCase("true") || arguments[0].equalsIgnoreCase("false")) {
                    special = Boolean.parseBoolean(arguments[0]);
                    var nArgs = new String[arguments.length - 1];
                    var skipped = false;
                    System.arraycopy(arguments, 1, nArgs, 0, arguments.length - 1);
                    arguments = nArgs;
                }
            } catch (Exception ignored) {

            }

        var target = this.getPlayer(commandSender, arguments[0]);
        if (target == null) {
            commandSender.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getNoTarget(arguments[0]));
            return true;
        }

        if (special) {
            var failed = false;
            Class<?> dynamicType = null;
            try {
                //Hacky and stupid stuff ™ to hook into "sendMessage"
                dynamicType = new ByteBuddy().subclass(
                                                     Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer"))
                                             .method(ElementMatchers.named("sendMessage"))
                                             .intercept(MethodCall.invokeSuper()
                                                                  .withAllArguments()
                                                                  .andThen(MethodDelegation.withDefaultConfiguration()
                                                                                           .withBinders(Morph.Binder.install(Morpher.class))
                                                                                           .to(new SudoInterceptor(commandSender))))
                                             .make()
                                             .load(this.getClass().getClassLoader())
                                             .getLoaded();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                failed = true;
            }

            if (this.getHandleMethod == null)
                try {
                    this.getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer")
                                                .getDeclaredMethod("getHandle");
                    this.getHandleMethod.setAccessible(true);
                } catch (NoSuchMethodException | ClassNotFoundException e) {
                    e.printStackTrace();
                    failed = true;
                }

            if (!failed)
                try {
                    var permField = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftHumanEntity")
                                         .getDeclaredField("perm");

                    permField.setAccessible(true);

                    var permissibleBase = (PermissibleBase) permField.get(target);

                    target = (Player) dynamicType.getDeclaredConstructors()[0].newInstance(Bukkit.getServer(), this.getHandleMethod.invoke(target));

                    permField.set(target, permissibleBase);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
        }

        if (this.plugin.getPermissions().hasPermission(target, "sudo.exempt", true))
            if (commandSender instanceof Player) {
                commandSender.sendMessage(
                        this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage(commandLabel, command, commandSender, target, "Sudo"));
                return true;
            }
        var msg = new StringBuilder();
        for (var i = 1; arguments.length > i; i++)
            msg.append(arguments[i]).append(" ");

        var first = arguments[1];
        if (!first.startsWith("/")) {
            target.chat(msg.toString().trim());
            return true;
        }

        first = first.substring(1);

        var commandToExecute = this.plugin.getCommandManager().getCommand(first);
        if (commandToExecute == null) {
            var commandEvent = new PlayerCommandPreprocessEvent(target, msg.toString().trim());
            Bukkit.getPluginManager().callEvent(commandEvent);
            return true;
        }

        var sudoArgs = msg.substring(first.length() + 1).trim().split(" ");

        if (sudoArgs.length < 2)
            if (sudoArgs[0].isEmpty())
                sudoArgs = new String[0];

        commandToExecute.execute(target, first, sudoArgs);
        return true;
    }
}
