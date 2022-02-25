package me.entity303.serversystem.commands.executable;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaltopCommand extends MessageUtils implements CommandExecutor {

    public BaltopCommand(ServerSystem plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            if (this.plugin.getPermissions().getCfg().getBoolean("Permissions.baltop.required"))
                if (!this.isAllowed(cs, "baltop.permission")) {
                    cs.sendMessage(this.getPrefix() + this.getNoPermission(this.Perm("baltop.permission")));
                    return;
                }

            String firstPlayer = null;
            String firstMoney = null;
            String secondPlayer = null;
            String secondMoney = null;
            String thirdPlayer = null;
            String thirdMoney = null;
            String fourthPlayer = null;
            String fourthMoney = null;
            String fifthPlayer = null;
            String fifthMoney = null;
            String sixthPlayer = null;
            String sixthMoney = null;
            String seventhPlayer = null;
            String seventhMoney = null;
            String eighthPlayer = null;
            String eighthMoney = null;
            String ninthPlayer = null;
            String ninthMoney = null;
            String tenthPlayer = null;
            String tenthMoney = null;

            LinkedHashMap<OfflinePlayer, Double> topTen = this.plugin.getEconomyManager().getTopTen();

            for (Map.Entry<OfflinePlayer, Double> entry : topTen.entrySet()) {
                if (firstPlayer == null) {
                    firstPlayer = entry.getKey().getName();
                    firstMoney = this.plugin.getEconomyManager().format(entry.getValue());
                    continue;
                }
                if (secondPlayer == null) {
                    secondPlayer = entry.getKey().getName();
                    secondMoney = this.plugin.getEconomyManager().format(entry.getValue());
                    continue;
                }
                if (thirdPlayer == null) {
                    thirdPlayer = entry.getKey().getName();
                    thirdMoney = this.plugin.getEconomyManager().format(entry.getValue());
                    continue;
                }
                if (fourthPlayer == null) {
                    fourthPlayer = entry.getKey().getName();
                    fourthMoney = this.plugin.getEconomyManager().format(entry.getValue());
                    continue;
                }
                if (fifthPlayer == null) {
                    fifthPlayer = entry.getKey().getName();
                    fifthMoney = this.plugin.getEconomyManager().format(entry.getValue());
                    continue;
                }
                if (sixthPlayer == null) {
                    sixthPlayer = entry.getKey().getName();
                    sixthMoney = this.plugin.getEconomyManager().format(entry.getValue());
                    continue;
                }
                if (seventhPlayer == null) {
                    seventhPlayer = entry.getKey().getName();
                    seventhMoney = this.plugin.getEconomyManager().format(entry.getValue());
                    continue;
                }
                if (eighthPlayer == null) {
                    eighthPlayer = entry.getKey().getName();
                    eighthMoney = this.plugin.getEconomyManager().format(entry.getValue());
                    continue;
                }
                if (ninthPlayer == null) {
                    ninthPlayer = entry.getKey().getName();
                    ninthMoney = this.plugin.getEconomyManager().format(entry.getValue());
                    continue;
                }
                tenthPlayer = entry.getKey().getName();
                tenthMoney = this.plugin.getEconomyManager().format(entry.getValue());
                break;
            }

            if (firstPlayer == null) {
                cs.sendMessage(this.getPrefix() + ChatColor.RED + "Error!");
                return;
            }

            if (secondPlayer == null) {
                secondPlayer = firstPlayer;
                secondMoney = firstMoney;
            }

            if (thirdPlayer == null) {
                thirdPlayer = secondPlayer;
                thirdMoney = secondMoney;
            }

            if (fourthPlayer == null) {
                fourthPlayer = thirdPlayer;
                fourthMoney = thirdMoney;
            }

            if (fifthPlayer == null) {
                fifthPlayer = fourthPlayer;
                fifthMoney = fourthMoney;
            }

            if (sixthPlayer == null) {
                sixthPlayer = fifthPlayer;
                sixthMoney = fifthMoney;
            }

            if (seventhPlayer == null) {
                seventhPlayer = sixthPlayer;
                seventhMoney = sixthMoney;
            }

            if (eighthPlayer == null) {
                eighthPlayer = seventhPlayer;
                eighthMoney = seventhMoney;
            }

            if (ninthPlayer == null) {
                ninthPlayer = eighthPlayer;
                ninthMoney = eighthMoney;
            }

            if (tenthPlayer == null) {
                tenthPlayer = ninthPlayer;
                tenthMoney = ninthMoney;
            }

            cs.sendMessage(this.getMessage("BalTop", label, cmd.getName(), cs, null).replace("<FIRST>", firstPlayer + " -> " + firstMoney).replace("<SECOND>", secondPlayer + " -> " + secondMoney).replace("<THIRD>", thirdPlayer + " -> " + thirdMoney).replace("<FOURTH>", fourthPlayer + " -> " + fourthMoney).replace("<FIFTH>", fifthPlayer + " -> " + fifthMoney).replace("<SIXTH>", sixthPlayer + " -> " + sixthMoney).replace("<SEVENTH>", seventhPlayer + " -> " + seventhMoney).replace("<EIGHTH>", eighthPlayer + " -> " + eighthMoney).replace("<NINTH>", ninthPlayer + " -> " + ninthMoney).replace("<TENTH>", tenthPlayer + " -> " + tenthMoney));
            return;
        });
        return true;
    }
}
