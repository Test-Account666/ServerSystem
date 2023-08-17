package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.MessageUtils;
import me.entity303.serversystem.utils.Morpher;
import me.entity303.serversystem.utils.interceptors.SudoInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SudoCommand extends MessageUtils implements CommandExecutor {

    private Method getHandleMethod = null;

    public SudoCommand(ServerSystem plugin) {
        super(plugin);
    }

    public static void sendMessage(CommandSender commandSender, Object... objects) {
        Method sendMessageMethod = null;
        for (Method method : CommandSender.class.getDeclaredMethods()) {
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length == objects.length) {
                boolean found = true;
                for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
                    Class<?> parameter = parameters[i];
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
        if (sendMessageMethod != null) try {
            sendMessageMethod.invoke(commandSender, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!this.isAllowed(cs, "sudo.use")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("sudo.use")));
            return true;
        }

        if (args.length <= 1) {
            cs.sendMessage(this.getPrefix() + this.getSyntax("Sudo", label, cmd.getName(), cs, null));
            return true;
        }

        boolean special = this.plugin.isSpecialSudo();

        if (args.length >= 3) try {
            if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("false")) {
                special = Boolean.parseBoolean(args[0]);
                String[] nArgs = new String[args.length - 1];
                boolean skipped = false;
                System.arraycopy(args, 1, nArgs, 0, args.length - 1);
                args = nArgs;
            }
        } catch (Exception ignored) {

        }

        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        if (special) {
            boolean failed = false;
            Class<?> dynamicType = null;
            try {
                //Hacky and stupid stuff ™ to hook into "sendMessage"
                dynamicType = new ByteBuddy()
                        .subclass(Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer"))
                        .method(ElementMatchers.named("sendMessage"))
                        .intercept(MethodCall.invokeSuper().withAllArguments().
                                andThen(MethodDelegation.withDefaultConfiguration().
                                        withBinders(Morph.Binder.install(Morpher.class)).
                                        to(new SudoInterceptor(cs))))
                        .make()
                        .load(this.getClass().getClassLoader())
                        .getLoaded();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                failed = true;
            }

            if (this.getHandleMethod == null) try {
                this.getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer").getDeclaredMethod("getHandle");
                this.getHandleMethod.setAccessible(true);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                failed = true;
            }

            if (!failed) try {
                Field permField = Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftHumanEntity").getDeclaredField("perm");

                permField.setAccessible(true);

                PermissibleBase permissibleBase = (PermissibleBase) permField.get(target);

                target = (Player) dynamicType.getDeclaredConstructors()[0].newInstance(Bukkit.getServer(), this.getHandleMethod.invoke(target));

                permField.set(target, permissibleBase);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     ClassNotFoundException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        /*StringBuilder msgBuilder = new StringBuilder();
            for (int i = 1; args.length > i; i++) msgBuilder.append(args[i]).append(" ");

            String msg = msgBuilder.toString().trim();

            while (msg.endsWith(" ")) msg = msg.substring(0, msg.length() - 1);

            String first = args[1];
            if (first.startsWith("/")) {
                first = first.substring(1);

                Command command = this.plugin.getCommandManager().getCommand(first);
                if (command == null) {
                    PlayerCommandPreprocessEvent commandEvent = new PlayerCommandPreprocessEvent(target, msg.toString().trim());
                    Bukkit.getPluginManager().callEvent(commandEvent);
                    return true;
                } else
                    command.execute(target, first, msg.substring(first.length() + 1).trim().split(" "));
            } else
                target.chat(msg.toString().trim());
            return true;*/
        if (this.isAllowed(target, "sudo.exempt", true)) if (cs instanceof Player) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Sudo", label, cmd.getName(), cs, target));
            return true;
        }
        StringBuilder msg = new StringBuilder();
        for (int i = 1; args.length > i; i++) msg.append(args[i]).append(" ");

        String first = args[1];
        if (first.startsWith("/")) {
            first = first.substring(1);

            Command command = this.plugin.getCommandManager().getCommand(first);
            if (command == null) {
                PlayerCommandPreprocessEvent commandEvent = new PlayerCommandPreprocessEvent(target, msg.toString().trim());
                Bukkit.getPluginManager().callEvent(commandEvent);
                return true;
            } else {
                String[] sudoArgs = msg.substring(first.length() + 1).trim().split(" ");

                if (sudoArgs.length < 2)
                    if (sudoArgs[0].isEmpty())
                        sudoArgs = new String[0];

                command.execute(target, first, sudoArgs);
            }
        } else
            target.chat(msg.toString().trim());
        return true;
    }
}
