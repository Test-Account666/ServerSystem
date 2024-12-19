package me.entity303.serversystem.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class ServerSystemExpansion extends PlaceholderExpansion {
    private final ServerSystem _plugin;

    public ServerSystemExpansion(ServerSystem plugin) {
        this._plugin = plugin;
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
    public boolean register() {
        if (this.isRegistered()) return false;
        return super.register();
    }

    @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        if (params.equalsIgnoreCase("money")) return String.valueOf(this._plugin.GetEconomyManager().GetMoneyAsNumber(offlinePlayer));
        if (params.equalsIgnoreCase("formattedmoney")) return this._plugin.GetEconomyManager().GetMoney(offlinePlayer);
        if (params.equalsIgnoreCase("drop")) {
            if (!offlinePlayer.isOnline()) return "false";
            return String.valueOf(this._plugin.GetVanish().GetAllowDrop().contains(offlinePlayer.getPlayer()));
        }
        if (params.equalsIgnoreCase("pickup")) {
            if (!offlinePlayer.isOnline()) return "false";
            return String.valueOf(this._plugin.GetVanish().GetAllowPickup().contains(offlinePlayer.getPlayer()));
        }
        if (params.equalsIgnoreCase("chat")) {
            if (!offlinePlayer.isOnline()) return "false";
            return String.valueOf(this._plugin.GetVanish().GetAllowChat().contains(offlinePlayer.getPlayer()));
        }
        if (params.equalsIgnoreCase("interact")) {
            if (!offlinePlayer.isOnline()) return "false";
            return String.valueOf(this._plugin.GetVanish().GetAllowInteract().contains(offlinePlayer.getPlayer()));
        }
        if (params.equalsIgnoreCase("vanish")) return String.valueOf(this._plugin.GetVanish().IsVanish(offlinePlayer));
        if (params.equalsIgnoreCase("god")) {
            if (!offlinePlayer.isOnline()) return "false";
            return String.valueOf(this._plugin.GetGodList().contains(offlinePlayer.getPlayer()));
        }

        if (params.equalsIgnoreCase("onlineplayers")) {
            if (!offlinePlayer.isOnline()) return null;
            var player = (Player) offlinePlayer;
            return String.valueOf(Bukkit.getOnlinePlayers().size() - (int) Bukkit.getOnlinePlayers().stream().filter(player1 -> !player.canSee(player1)).count());
        }

        if (params.startsWith("baltop_formattedmoney_")) {
            var placeString = params.substring("baltop_formattedmoney_".length());
            try {
                var place = Integer.parseInt(placeString);

                if (place <= 0) place = 1;

                if (place > 10) throw new UnsupportedOperationException("Currently, only top 10 is supported!");

                return this._plugin.GetEconomyManager().Format(this.GetTopXBalance(place));
            } catch (NumberFormatException exception) {
                this._plugin.Error("'" + placeString + "' is not a valid integer!");
                return "";
            }
        }

        if (params.startsWith("baltop_money_")) {
            var placeString = params.substring("baltop_money_".length());
            try {
                var place = Integer.parseInt(placeString);

                if (place <= 0) place = 1;

                if (place > 10) throw new UnsupportedOperationException("Currently, only top 10 is supported!");

                return String.valueOf(this.GetTopXBalance(place));
            } catch (NumberFormatException exception) {
                this._plugin.Error("'" + placeString + "' is not a valid integer!");
                return "";
            }
        }

        if (params.startsWith("baltop_player_")) {
            var placeString = params.substring("baltop_player_".length());
            try {
                var place = Integer.parseInt(placeString);

                if (place <= 0) place = 1;

                if (place > 10) throw new UnsupportedOperationException("Currently, only top 10 is supported!");

                return this.GetTopXName(place);
            } catch (NumberFormatException exception) {
                this._plugin.Error("'" + placeString + "' is not a valid integer!");
                return "";
            }
        }
        return super.onRequest(offlinePlayer, params);
    }

    private Double GetTopXBalance(int place) {
        return this.GetTopX(place).getValue();
    }

    private String GetTopXName(int place) {
        return this.GetTopX(place).getKey().getName();
    }

    private Map.Entry<OfflinePlayer, Double> GetTopX(int place) {
        var currentPlace = 0;

        Map<OfflinePlayer, Double> topTenMap = this._plugin.GetEconomyManager().GetTopTen();

        Map.Entry<OfflinePlayer, Double> lastPlace = null;

        for (var topX : topTenMap.entrySet()) {
            currentPlace += 1;

            lastPlace = topX;

            if (currentPlace == place) return topX;
        }

        return lastPlace;
    }
}
