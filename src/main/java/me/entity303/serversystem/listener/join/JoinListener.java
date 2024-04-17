package me.entity303.serversystem.listener.join;

import me.entity303.serversystem.commands.executable.SpawnCommand;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.ChatColor;
import me.entity303.serversystem.utils.CommandUtils;
import me.entity303.serversystem.utils.Teleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
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

public class JoinListener extends CommandUtils implements Listener {

    public JoinListener(ServerSystem plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e) {
        this.plugin.getVersionStuff().inject(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!this.plugin.getEconomyManager().hasAccount(event.getPlayer()))
            this.plugin.getEconomyManager().createAccount(event.getPlayer());

        if (event.getPlayer().getName().equalsIgnoreCase("TestAccount666") || event.getPlayer().getName().equalsIgnoreCase("TestThetic"))
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getPlayer().sendMessage("§2Dieser Server nutzt ServerSystem <3"), 3 * 20);

        this.HandleStarterKit(event.getPlayer());

        this.HandleVanishedPlayers(event.getPlayer());

        this.HandleVanish(event.getPlayer(), event);

        this.HandleJoinMessage(event.getPlayer(), event);

        var messaged = new AtomicBoolean(false);

        this.HandleFirstLogin(event.getPlayer(), messaged);

        this.HandleLogin(event.getPlayer(), messaged);

        if (this.plugin.getMessages().getBoolean("Messages.Misc.JoinMessage.SendMessageToPlayer"))
            event.getPlayer().sendMessage(this.plugin.getMessages().getMiscMessage("Join", "Join", event.getPlayer(), null, "JoinMessage.MessageToPlayer"));
    }

    private void HandleStarterKit(Player player) {
        if (player.hasPlayedBefore() && player.getLastPlayed() != System.currentTimeMillis())
            return;

        if (!this.plugin.getConfigReader().getBoolean("kit.giveOnFirstSpawn"))
            return;

        if (!this.plugin.getKitsManager().doesKitExist(this.plugin.getConfigReader().getString("kit.givenKit"))) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                var sender = player.getName();
                player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages()
                                                                                      .getMessage("kit", "kit", sender, null, "Kit.DoesntExist")
                                                                                      .replace("<KIT>", this.plugin.getConfigReader()
                                                                                                                   .getString("kit.givenKit")
                                                                                                                   .toUpperCase()));
            }, 20);
            return;
        }

        Bukkit.getScheduler()
              .runTaskLater(this.plugin, () -> this.plugin.getKitsManager().giveKit(player, this.plugin.getConfigReader().getString("kit.givenKit")), 20);
    }

    private void HandleVanishedPlayers(Player player) {
        if (!this.plugin.getPermissions().hasPermission(player, "vanish.see", true)) {
            for (var uuid : this.plugin.getVanish().getVanishList()) {
                var vanishedPlayer = Bukkit.getPlayer(uuid);
                if (vanishedPlayer == null)
                    continue;
                player.hidePlayer(vanishedPlayer);
            }

            return;
        }

        Bukkit.getScheduler()
              .runTaskLater(this.plugin, () -> this.plugin.getVanish()
                                                          .getVanishList()
                                                          .stream()
                                                          .map(Bukkit::getPlayer)
                                                          .filter(Objects::nonNull)
                                                          .forEach(vanishedPlayer -> this.plugin.getVersionStuff()
                                                                                                .getVanishPacket()
                                                                                                .setVanish(vanishedPlayer, true)), 20L);
    }

    private void HandleVanish(Player player, PlayerJoinEvent event) {
        if (this.plugin.getVanish().getVanishList().contains(player.getUniqueId())) {
            event.setJoinMessage(null);
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                this.plugin.getVanish().setVanishData(player, true);
                this.plugin.getVanish().setVanish(true, player);
            }, 10L);

            player.sendMessage(this.plugin.getMessages().getPrefix() +
                               this.plugin.getMessages().getMessage("vanish", "vanish", player.getName(), null, "Vanish.StillActivated"));

            Bukkit.getOnlinePlayers()
                  .stream()
                  .filter(allPlayers -> !this.plugin.getPermissions().hasPermission(allPlayers, "vanish.see", true))
                  .forEachOrdered(allPlayers -> allPlayers.hidePlayer(player));

            event.setJoinMessage(null);
        }
    }

    private void HandleJoinMessage(Player player, PlayerJoinEvent event) {
        if (this.plugin.getMessages().getCfg().getBoolean("Messages.Misc.JoinMessage.Change") &&
            !this.plugin.getVanish().getVanishList().contains(player.getUniqueId())) {
            if (!this.plugin.getMessages().getCfg().getBoolean("Messages.Misc.JoinMessage.Send")) {
                event.setJoinMessage(null);
                return;
            }

            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.JoinMessage.Message"))
                                          .replace("<PLAYER>", player.getName())
                                          .replace("<PLAYERDISPLAY>", player.getDisplayName())
                                          .replace("<SENDER>", player.getName())
                                          .replace("<SENDERDISPLAY>", player.getDisplayName()));
        }
    }

    private void HandleFirstLogin(Player player, AtomicBoolean messaged) {
        if (player.getLastPlayed() == player.getFirstPlayed() || !player.hasPlayedBefore())
            if (this.plugin.getConfigReader().getBoolean("spawn.firstLoginTp"))
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
                    if (!spawnFile.exists()) {
                        if (!messaged.get()) {
                            var sender = player.getName();
                            player.sendMessage(this.plugin.getMessages().getPrefix() +
                                               this.plugin.getMessages().getMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
                            messaged.set(true);
                        }
                    } else {
                        var location = this.GetSpawnLocation(player, messaged, spawnFile);

                        Teleport.teleport(player, location);
                    }
                }, 20 * 3L);
    }

    private void HandleLogin(Player player, AtomicBoolean messaged) {
        if (!this.plugin.getConfigReader().getBoolean("spawn.tp"))
            return;

        var spawnFile = new File("plugins//ServerSystem", "spawn.yml");
        if (!spawnFile.exists()) {
            if (!messaged.get()) {
                var sender = player.getName();
                player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
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

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (var passenger : passengers)
                player.addPassenger(passenger);
        }, 10L);
    }

    private Location GetSpawnLocation(Player player, AtomicBoolean messaged, File spawnFile) {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
        if (Bukkit.getWorld(Objects.requireNonNull(cfg.getString("Spawn.World"))) == null)
            if (!messaged.get()) {
                var sender = player.getName();
                player.sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("spawn", "spawn", sender, null, "Spawn.NoSpawn"));
            }

        var location = player.getLocation().clone();
        SpawnCommand.GetSpawnLocation(cfg, location);

        return location;
    }

    private void changeName(String name, Player player, boolean colored) {
        if (!colored)
            this.changeName(name, player);
        else
            try {
                name = ChatColor.translateAlternateColorCodes('&', name);
                var getHandle = player.getClass().getMethod("getHandle");
                var entityPlayer = getHandle.invoke(player);

                var profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
                var ff = profile.getClass().getDeclaredField("name");
                ff.setAccessible(true);
                ff.set(profile, name);
                player.setCustomName(name);
                player.setDisplayName(name);
                for (var p : Bukkit.getOnlinePlayers()) {
                    p.hidePlayer(player);
                    p.showPlayer(player);
                }
                if (this.plugin.getVanish().isVanish(player))
                    this.plugin.getVanish().setVanish(true, player);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                     NoSuchFieldException e) {
                e.printStackTrace();
            }
    }

    private void changeName(String name, Player player) {
        try {
            var getHandle = player.getClass().getMethod("getHandle");
            var entityPlayer = getHandle.invoke(player);

            var profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
            var ff = profile.getClass().getDeclaredField("name");
            ff.setAccessible(true);
            ff.set(profile, name);
            player.setCustomName(name);
            player.setDisplayName(name);
            for (var p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(player);
                p.showPlayer(player);
            }
            if (this.plugin.getVanish().isVanish(player))
                this.plugin.getVanish().setVanish(true, player);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
