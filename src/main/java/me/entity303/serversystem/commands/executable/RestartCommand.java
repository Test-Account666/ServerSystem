package me.entity303.serversystem.commands.executable;


import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import me.entity303.serversystem.commands.ICommandExecutorOverload;
import org.bukkit.command.CommandSender;

public class RestartCommand extends CommandUtils implements ICommandExecutorOverload {
    private Boolean _restarting = false;

    public RestartCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] arguments) {
        if (!this._plugin.GetPermissions().HasPermission(commandSender, "restart")) {
            var permission = this._plugin.GetPermissions().GetPermission("restart");
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
            return true;
        }
        if (this._restarting) {
            var label1 = command.getName();
            var command1 = command.getName();
            commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage(label1, command1, commandSender, null, "Restart.AlreadyRestarting"));
            return true;
        }
        this._restarting = true;
        var sek = new String[] { "10" };
        var label12 = command.getName();
        var command12 = command.getName();
        Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() +
                                this._plugin.GetMessages().GetMessage(label12, command12, commandSender, null, "Restart.RestartTimer").replace("<TIME>", sek[0]));
        sek[0] = "9";
        Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
            var label11 = command.getName();
            var command11 = command.getName();
            Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() +
                                    this._plugin.GetMessages().GetMessage(label11, command11, commandSender, null, "Restart.RestartTimer").replace("<TIME>", sek[0]));
            sek[0] = "8";
            Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                var label10 = command.getName();
                var command10 = command.getName();
                Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() +
                                        this._plugin.GetMessages().GetMessage(label10, command10, commandSender, null, "Restart.RestartTimer").replace("<TIME>", sek[0]));
                sek[0] = "7";
                Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                    var label9 = command.getName();
                    var command9 = command.getName();
                    Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() +
                                            this._plugin.GetMessages().GetMessage(label9, command9, commandSender, null, "Restart.RestartTimer").replace("<TIME>", sek[0]));
                    sek[0] = "6";
                    Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                        var label8 = command.getName();
                        var command8 = command.getName();
                        Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                   .GetMessage(label8, command8, commandSender, null, "Restart.RestartTimer")
                                                                                                   .replace("<TIME>", sek[0]));
                        sek[0] = "5";
                        Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                            var label7 = command.getName();
                            var command7 = command.getName();
                            Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                       .GetMessage(label7, command7, commandSender, null,
                                                                                                                   "Restart.RestartTimer")
                                                                                                       .replace("<TIME>", sek[0]));
                            sek[0] = "4";
                            Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                                var label6 = command.getName();
                                var command6 = command.getName();
                                Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                           .GetMessage(label6, command6, commandSender, null,
                                                                                                                       "Restart.RestartTimer")
                                                                                                           .replace("<TIME>", sek[0]));
                                sek[0] = "3";
                                Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                                    var label5 = command.getName();
                                    var command5 = command.getName();
                                    Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                               .GetMessage(label5, command5, commandSender, null,
                                                                                                                           "Restart.RestartTimer")
                                                                                                               .replace("<TIME>", sek[0]));
                                    sek[0] = "2";
                                    Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                                        var label4 = command.getName();
                                        var command4 = command.getName();
                                        Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                                   .GetMessage(label4, command4, commandSender, null,
                                                                                                                               "Restart.RestartTimer")
                                                                                                                   .replace("<TIME>", sek[0]));
                                        sek[0] = "1";
                                        Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                                            var label3 = command.getName();
                                            var command3 = command.getName();
                                            Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                                       .GetMessage(label3, command3,
                                                                                                                                   commandSender, null,
                                                                                                                                   "Restart.RestartTimer")
                                                                                                                       .replace("<TIME>", sek[0]));
                                            sek[0] = "0";
                                            Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                                                var label2 = command.getName();
                                                var command2 = command.getName();
                                                Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                                           .GetMessage(label2, command2,
                                                                                                                                       commandSender, null,
                                                                                                                                       "Restart.RestartTimer")
                                                                                                                           .replace("<TIME>", sek[0]));
                                                sek[0] = "10";
                                                Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                                                    var label1 = command.getName();
                                                    var command1 = command.getName();
                                                    Bukkit.broadcastMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                                                               .GetMessage(label1, command1,
                                                                                                                                           commandSender,
                                                                                                                                           null,
                                                                                                                                           "Restart.RestartMessage")
                                                                                                                               .replace("<TIME>", "0"));
                                                    Bukkit.getScheduler()
                                                          .runTaskLater(this._plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop"), 20);
                                                }, 20);
                                            }, 20);
                                        }, 20);
                                    }, 20);
                                }, 20);
                            }, 20);
                        }, 20);
                    }, 20);
                }, 20);
            }, 20);
        }, 20);
        return true;
    }
}
