package me.entity303.serversystem.listener.join;

import me.entity303.serversystem.commands.executable.SpawnCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class JoinListener implements Listener {

    protected final ServerSystem _plugin;

    public JoinListener(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @EventHandler
    public void OnLogin(PlayerJoinEvent event) {
        this._plugin.GetVersionStuff().Inject(event.getPlayer());
    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        if (!this._plugin.GetEconomyManager().HasAccount(event.getPlayer())) this._plugin.GetEconomyManager().CreateAccount(event.getPlayer());

        if (event.getPlayer().getName().equalsIgnoreCase("TestAccount666") || event.getPlayer().getName().equalsIgnoreCase("TestThetic")) {
            Bukkit.getScheduler().runTaskLater(this._plugin, () -> event.getPlayer().sendMessage("§2Dieser Server nutzt ServerSystem <3"), 3 * 20);
        }

        this.HandleStarterKit(event.getPlayer());

        this.HandleVanishedPlayers(event.getPlayer());

        this.HandleVanish(event.getPlayer(), event);

        this.HandleJoinMessage(event.getPlayer(), event);

        var messaged = new AtomicBoolean(false);

        this.HandleFirstLogin(event.getPlayer(), messaged);

        this.HandleLogin(event.getPlayer(), messaged);

        if (this._plugin.GetMessages().GetBoolean("Messages.Misc.JoinMessage.SendMessageToPlayer")) {
            event.getPlayer().sendMessage(this._plugin.GetMessages().GetMiscMessage("Join", "Join", event.getPlayer(), null, "JoinMessage.MessageToPlayer"));
        }
    }

    private void HandleStarterKit(Player player) {
        if (player.hasPlayedBefore() && player.getLastPlayed() != System.currentTimeMillis()) return;

        if (!this._plugin.GetConfigReader().GetBoolean("kit.giveOnFirstSpawn")) return;

        if (!this._plugin.GetKitsManager().DoesKitExist(this._plugin.GetConfigReader().GetString("kit.givenKit"))) {
            Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                var sender = player.getName();
                player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages()
                                                                                        .GetMessage("kit", "kit", sender, null, "Kit.DoesntExist")
                                                                                        .replace("<KIT>",
                                                                                                 this._plugin.GetConfigReader().GetString("kit.givenKit").toUpperCase()));
            }, 20);
            return;
        }

        Bukkit.getScheduler()
              .runTaskLater(this._plugin, () -> this._plugin.GetKitsManager().GiveKit(player, this._plugin.GetConfigReader().GetString("kit.givenKit")), 20);
    }

    private void HandleVanishedPlayers(Player player) {
        if (!this._plugin.GetPermissions().HasPermission(player, "vanish.see", true)) {
            for (var uuid : this._plugin.GetVanish().GetVanishList()) {
                var vanishedPlayer = Bukkit.getPlayer(uuid);
                if (vanishedPlayer == null) continue;
                player.hidePlayer(vanishedPlayer);
            }

            return;
        }

        Bukkit.getScheduler()
              .runTaskLater(this._plugin, () -> this._plugin.GetVanish()
                                                            .GetVanishList()
                                                            .stream()
                                                            .map(Bukkit::getPlayer)
                                                            .filter(Objects::nonNull)
                                                            .forEach(vanishedPlayer -> this._plugin.GetVersionStuff().GetVanishPacket().SetVanish(vanishedPlayer, true)),
                            20L);
    }

    private void HandleVanish(Player player, PlayerJoinEvent event) {
        if (this._plugin.GetVanish().GetVanishList().contains(player.getUniqueId())) {
            event.setJoinMessage(null);
            Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                this._plugin.GetVanish().SetVanishData(player, true);
                this._plugin.GetVanish().SetVanish(true, player);
            }, 10L);

            player.sendMessage(
                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage("vanish", "vanish", player.getName(), null, "Vanish.StillActivated"));

            Bukkit.getOnlinePlayers()
                  .stream()
                  .filter(allPlayers -> !this._plugin.GetPermissions().HasPermission(allPlayers, "vanish.see", true))
                  .forEachOrdered(allPlayers -> allPlayers.hidePlayer(player));

            event.setJoinMessage(null);
        }
    }

    private void HandleJoinMessage(Player player, PlayerJoinEvent event) {
        if (this._plugin.GetMessages().GetConfiguration().GetBoolean("Messages.Misc.JoinMessage.Change") &&
            !this._plugin.GetVanish().GetVanishList().contains(player.getUniqueId())) {
            if (!this._plugin.GetMessages().GetConfiguration().GetBoolean("Messages.Misc.JoinMessage.Send")) {
                event.setJoinMessage(null);
                return;
            }

            event.setJoinMessage(ChatColor.TranslateAlternateColorCodes('&', this._plugin.GetMessages().GetConfiguration().GetString("Messages.Misc.JoinMessage.Message"))
                                          .replace("<PLAYER>", player.getName())
                                          .replace("<PLAYERDISPLAY>", player.getDisplayName())
                                          .replace("<SENDER>", player.getName())
                                          .replace("<SENDERDISPLAY>", player.getDisplayName()));
        }
    }

    private void HandleFirstLogin(Player player, AtomicBoolean messaged) {
        if (player.getLastPlayed() == player.getFirstPlayed() || !player.hasPlayedBefore()) {
            if (this._plugin.GetConfigReader().GetBoolean("spawn.firstLoginTp")) {
                Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
                    var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
                    if (!spawnFile.exists()) {
                        if (!messaged.get()) {
                            var sender = player.getName();
                            player.sendMessage(
                                    this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
                            messaged.set(true);
                        }
                    } else {
                        var location = this.GetSpawnLocation(player, messaged, spawnFile);

                        player.teleport(location);
                    }
                }, 20 * 3L);
            }
        }
    }

    private void HandleLogin(Player player, AtomicBoolean messaged) {
        if (!this._plugin.GetConfigReader().GetBoolean("spawn.tp")) return;

        var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
        if (!spawnFile.exists()) {
            if (!messaged.get()) {
                var sender = player.getName();
                player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
                messaged.set(true);
            }
            return;
        }

        var location = this.GetSpawnLocation(player, messaged, spawnFile);

        var passengers = new LinkedList<Entity>();
        for (var passenger : player.getPassengers()) {
            passenger.eject();
            passengers.add(passenger);
        }

        player.teleport(location);

        Bukkit.getScheduler().runTaskLater(this._plugin, () -> {
            for (var passenger : passengers)
                player.addPassenger(passenger);
        }, 10L);
    }

    private Location GetSpawnLocation(Player player, AtomicBoolean messaged, File spawnFile) {
        var cfg = YamlConfiguration.loadConfiguration(spawnFile);
        if (Bukkit.getWorld(Objects.requireNonNull(cfg.getString("Spawn.World"))) == null) {
            if (!messaged.get()) {
                var sender = player.getName();
                player.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
            }
        }

        var location = player.getLocation().clone();
        SpawnCommand.GetSpawnLocation(cfg, location);

        return location;
    }

    private void ChangeName(String name, Player player, boolean colored) {
        if (!colored) {
            this.ChangeName(name, player);
        } else {
            try {
                name = ChatColor.TranslateAlternateColorCodes('&', name);
                var getHandle = player.getClass().getMethod("getHandle");
                var entityPlayer = getHandle.invoke(player);

                var profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
                var nameFile = profile.getClass().getDeclaredField("name");
                nameFile.setAccessible(true);
                nameFile.set(profile, name);
                player.setCustomName(name);
                player.setDisplayName(name);
                for (var all : Bukkit.getOnlinePlayers()) {
                    all.hidePlayer(player);
                    all.showPlayer(player);
                }
                if (this._plugin.GetVanish().IsVanish(player)) this._plugin.GetVanish().SetVanish(true, player);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                     NoSuchFieldException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void ChangeName(String name, Player player) {
        try {
            var getHandle = player.getClass().getMethod("getHandle");
            var entityPlayer = getHandle.invoke(player);

            var profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
            var nameField = profile.getClass().getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(profile, name);
            player.setCustomName(name);
            player.setDisplayName(name);
            for (var all : Bukkit.getOnlinePlayers()) {
                all.hidePlayer(player);
                all.showPlayer(player);
            }
            if (this._plugin.GetVanish().IsVanish(player)) this._plugin.GetVanish().SetVanish(true, player);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 NoSuchFieldException exception) {
            exception.printStackTrace();
        }
    }
}
