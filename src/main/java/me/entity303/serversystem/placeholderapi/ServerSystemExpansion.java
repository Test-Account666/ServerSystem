package me.entity303.serversystem.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class ServerSystemExpansion extends PlaceholderExpansion {
    private final ServerSystem plugin;

    public ServerSystemExpansion(ServerSystem plugin) {
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
            return String.valueOf(Bukkit.getOnlinePlayers().size() - (int) Bukkit.getOnlinePlayers().stream().filter(player1 -> !player.canSee(player1)).count());
        }

        if (params.startsWith("baltop_formattedmoney_")) {
            String placeString = params.substring("baltop_formattedmoney_".length());
            try {
                int place = Integer.parseInt(placeString);

                if (place <= 0)
                    place = 1;

                if (place > 10)
                    throw new UnsupportedOperationException("Currently, only top 10 is supported!");

                return this.plugin.getEconomyManager().format(this.getTopXBalance(place));
            } catch (NumberFormatException e) {
                this.plugin.error("'" + placeString + "' is not a valid integer!");
                return "";
            }
        }

        if (params.startsWith("baltop_money_")) {
            String placeString = params.substring("baltop_money_".length());
            try {
                int place = Integer.parseInt(placeString);

                if (place <= 0)
                    place = 1;

                if (place > 10)
                    throw new UnsupportedOperationException("Currently, only top 10 is supported!");

                return String.valueOf(this.getTopXBalance(place));
            } catch (NumberFormatException e) {
                this.plugin.error("'" + placeString + "' is not a valid integer!");
                return "";
            }
        }

        if (params.startsWith("baltop_player_")) {
            String placeString = params.substring("baltop_player_".length());
            try {
                int place = Integer.parseInt(placeString);

                if (place <= 0)
                    place = 1;

                if (place > 10)
                    throw new UnsupportedOperationException("Currently, only top 10 is supported!");

                return this.getTopXName(place);
            } catch (NumberFormatException e) {
                this.plugin.error("'" + placeString + "' is not a valid integer!");
                return "";
            }
        }
        return super.onRequest(p, params);
    }

    private Map.Entry<OfflinePlayer, Double> getTopX(int place) {
        int currentPlace = 0;

        Map<OfflinePlayer, Double> topTenMap = this.plugin.getEconomyManager().getTopTen();

        Map.Entry<OfflinePlayer, Double> lastPlace = null;

        for (Map.Entry<OfflinePlayer, Double> topX : topTenMap.entrySet()) {
            currentPlace += 1;

            lastPlace = topX;

            if (currentPlace == place)
                return topX;
        }

        return lastPlace;
    }

    private Double getTopXBalance(int place) {
        return this.getTopX(place).getValue();
    }

    private String getTopXName(int place) {
        return this.getTopX(place).getKey().getName();
    }
}
