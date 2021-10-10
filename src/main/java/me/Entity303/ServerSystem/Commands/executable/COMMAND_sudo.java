package me.Entity303.ServerSystem.Commands.executable;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class COMMAND_sudo extends ServerSystemCommand implements CommandExecutor {

    private Method getHandleMethod = null;


    public COMMAND_sudo(ss plugin) {
        super(plugin);
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
            special = Boolean.parseBoolean(args[0]);
            String[] nArgs = new String[args.length - 1];
            boolean skipped = false;
            System.arraycopy(args, 1, nArgs, 0, args.length - 1);
            args = nArgs;
        } catch (Exception ignored) {

        }

        Player target = this.getPlayer(cs, args[0]);
        if (target == null) {
            cs.sendMessage(this.getPrefix() + this.getNoTarget(args[0]));
            return true;
        }

        if (special && cs instanceof Player) {
            boolean failed = false;
            Class<?> dynamicType = null;
            try {
                //Hacky stuff to hook into "sendMessage", only works with players, no console
                dynamicType = new ByteBuddy()
                        .subclass(Class.forName("org.bukkit.craftbukkit." + this.plugin.getVersionManager().getNMSVersion() + ".entity.CraftPlayer"))
                        .method(ElementMatchers.named("sendMessage"))
                        .intercept(MethodCall.invokeSuper().withAllArguments().andThen(MethodCall.invokeSelf().on(cs).withAllArguments()))
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
                target = (Player) dynamicType.getDeclaredConstructors()[0].newInstance(Bukkit.getServer(), this.getHandleMethod.invoke(target));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (this.isAllowed(target, "sudo.exempt", true)) {
            if (cs instanceof Player) {
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
                } else
                    command.execute(target, first, msg.substring(first.length() + 1).trim().split(" "));
            } else
                target.chat(msg.toString().trim());
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
            } else
                command.execute(target, first, msg.substring(first.length() + 1).trim().split(" "));
        } else
            target.chat(msg.toString().trim());
        return true;
    }
}
