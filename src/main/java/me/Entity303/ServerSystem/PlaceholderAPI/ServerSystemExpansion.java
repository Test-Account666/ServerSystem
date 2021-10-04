package me.Entity303.ServerSystem.PlaceholderAPI;

import me.Entity303.ServerSystem.Main.ss;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ServerSystemExpansion extends PlaceholderExpansion {
    private final ss plugin;

    public ServerSystemExpansion(ss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        if (this.isRegistered()) return false;
        return super.register();
    }

    @Override
    public String getIdentifier() {
        return "serversystem";
    }

    @Override
    public String getAuthor() {
        return "Entity303";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if (params.equalsIgnoreCase("money"))
            return String.valueOf(this.plugin.getEconomyManager().getMoneyAsNumber(p));
        if (params.equalsIgnoreCase("formattedmoney")) return this.plugin.getEconomyManager().getMoney(p);
        if (params.equalsIgnoreCase("drop")) {
            if (!p.isOnline()) return "false";
            return String.valueOf(this.plugin.getVanish().getAllowDrop().contains(p.getPlayer()));
        }
        if (params.equalsIgnoreCase("pickup")) {
            if (!p.isOnline()) return "false";
            return String.valueOf(this.plugin.getVanish().getAllowPickup().contains(p.getPlayer()));
        }
        if (params.equalsIgnoreCase("chat")) {
            if (!p.isOnline()) return "false";
            return String.valueOf(this.plugin.getVanish().getAllowChat().contains(p.getPlayer()));
        }
        if (params.equalsIgnoreCase("interact")) {
            if (!p.isOnline()) return "false";
            return String.valueOf(this.plugin.getVanish().getAllowInteract().contains(p.getPlayer()));
        }
        if (params.equalsIgnoreCase("vanish")) return String.valueOf(this.plugin.getVanish().isVanish(p));
        if (params.equalsIgnoreCase("god")) {
            if (!p.isOnline()) return "false";
            return String.valueOf(this.plugin.getGodList().contains(p.getPlayer()));
        }

        if (params.equalsIgnoreCase("onlineplayers")) {
            if (!p.isOnline()) return null;
            Player player = (Player) p;
            return String.valueOf(Bukkit.getOnlinePlayers().size() - /*(int) this.plugin.getVanish().getVanishList().stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).count()*/(int) Bukkit.getOnlinePlayers().stream().filter(player1 -> !player.canSee(player1)).count());
        }
        return super.onRequest(p, params);
    }
}
