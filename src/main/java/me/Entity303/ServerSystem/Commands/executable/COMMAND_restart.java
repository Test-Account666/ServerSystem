package me.Entity303.ServerSystem.Commands.executable;


import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class COMMAND_restart extends MessageUtils implements CommandExecutor {
    private Boolean restarting = false;

    public COMMAND_restart(ss plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!this.isAllowed(cs, "restart")) {
            cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("restart")));
            return true;
        }
        if (this.restarting) {
            cs.sendMessage(this.getPrefix() + this.getMessage("Restart.AlreadyRestarting", command.getName(), command.getName(), cs, null));
            return true;
        }
        this.restarting = true;
        String[] sek = {"10"};
        Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
        sek[0] = "9";
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
            sek[0] = "8";
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
                sek[0] = "7";
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
                    sek[0] = "6";
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
                        sek[0] = "5";
                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                            Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
                            sek[0] = "4";
                            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                                Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
                                sek[0] = "3";
                                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                                    Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
                                    sek[0] = "2";
                                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                                        Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
                                        sek[0] = "1";
                                        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                                            Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
                                            sek[0] = "0";
                                            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                                                Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartTimer", command.getName(), command.getName(), cs, null).replace("<TIME>", sek[0]));
                                                sek[0] = "10";
                                                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                                                    Bukkit.broadcastMessage(this.getPrefix() + this.getMessage("Restart.RestartMessage", command.getName(), command.getName(), cs, null).replace("<TIME>", "0"));
                                                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop"), 20);
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
