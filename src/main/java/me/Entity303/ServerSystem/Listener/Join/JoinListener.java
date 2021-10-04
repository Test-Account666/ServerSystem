package me.Entity303.ServerSystem.Listener.Join;

import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.ChatColor;
import me.Entity303.ServerSystem.Utils.ServerSystemCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class JoinListener extends ServerSystemCommand implements Listener {

    public JoinListener(ss plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!this.plugin.getEconomyManager().hasAccount(e.getPlayer()))
            this.plugin.getEconomyManager().createAccount(e.getPlayer());
        this.plugin.getVersionStuff().inject(e.getPlayer());

        if (e.getPlayer().getName().equalsIgnoreCase("TestAccount666") || e.getPlayer().getName().equalsIgnoreCase("TestThetic"))
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> e.getPlayer().sendMessage("§2Dieser Server nutzt ServerSystem <3"), 3 * 20);

        if (!e.getPlayer().hasPlayedBefore() || e.getPlayer().getLastPlayed() == System.currentTimeMillis())
            if (this.plugin.getConfig().getBoolean("kit.giveonfirstspawn"))
                if (this.plugin.getKitsManager().doesKitExist(this.plugin.getConfig().getString("kit.givenkit")))
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.plugin.getKitsManager().giveKit(e.getPlayer(), this.plugin.getConfig().getString("kit.givenkit")), 20);
                else
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        e.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Kit.DoesntExist", "kit", "kit", e.getPlayer().getName(), null).replace("<KIT>", this.plugin.getConfig().getString("kit.givenkit").toUpperCase()));
                    }, 20);

        if (!this.plugin.getPermissions().hasPerm(e.getPlayer(), "vanish.see", true))
            for (UUID uuid : this.plugin.getVanish().getVanishList()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                e.getPlayer().hidePlayer(player);
            }
        else
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.plugin.getVanish().getVanishList().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> this.plugin.getVersionStuff().getVanishPacket().setVanish(player, true)), 20L);

        if (this.plugin.getVanish().getVanishList().contains(e.getPlayer().getUniqueId())) {
            e.setJoinMessage(null);
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                this.plugin.getVanish().setVanishData(e.getPlayer(), true);
                this.plugin.getVanish().setVanish(true, e.getPlayer());
            }, 10L);
            e.getPlayer().sendMessage(this.plugin.getMessages().getPrefix() + this.plugin.getMessages().getMessage("vanish", "vanish", e.getPlayer().getName(), null, "Vanish.StillActivated"));
            Bukkit.getOnlinePlayers().stream().filter(player -> !this.plugin.getPermissions().hasPerm(player, "vanish.see", true)).forEachOrdered(player -> player.hidePlayer(e.getPlayer()));
            e.setJoinMessage(null);
        }
        if (this.plugin.getMessages().getCfg().getBoolean("Messages.Misc.JoinMessage.Change") && !this.plugin.getVanish().getVanishList().contains(e.getPlayer().getUniqueId()))
            if (this.plugin.getMessages().getCfg().getBoolean("Messages.Misc.JoinMessage.Send"))
                e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getMessages().getCfg().getString("Messages.Misc.JoinMessage.Message")).replace("<PLAYER>", e.getPlayer().getName()).replace("<PLAYERDISPLAY>", e.getPlayer().getDisplayName()).replace("<SENDER>", e.getPlayer().getName()).replace("<SENDERDISPLAY>", e.getPlayer().getDisplayName()));
            else
                e.setJoinMessage(null);

        AtomicBoolean messaged = new AtomicBoolean(false);

        if (e.getPlayer().getLastPlayed() == e.getPlayer().getFirstPlayed() || !e.getPlayer().hasPlayedBefore()) {
            if (this.plugin.getConfig().getBoolean("spawn.firstlogintp"))
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    File spawnFile = new File("plugins//ServerSystem", "spawn.yml");
                    if (!spawnFile.exists()) {
                        if (!messaged.get()) {
                            e.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Spawn.NoSpawn", "spawn", "spawn", e.getPlayer().getName(), null));
                            messaged.set(true);
                        }
                    } else {
                        FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
                        if (Bukkit.getWorld(cfg.getString("Spawn.World")) == null) if (!messaged.get())
                            e.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Spawn.NoSpawn", "spawn", "spawn", e.getPlayer().getName(), null));
                        Location location = e.getPlayer().getLocation().clone();
                        location.setX(cfg.getDouble("Spawn.X"));
                        location.setY(cfg.getDouble("Spawn.Y"));
                        location.setZ(cfg.getDouble("Spawn.Z"));
                        location.setYaw((float) cfg.getDouble("Spawn.Yaw"));
                        location.setPitch((float) cfg.getDouble("Spawn.Pitch"));
                        location.setWorld(Bukkit.getWorld(cfg.getString("Spawn.World")));
                        e.getPlayer().teleport(location);
                    }
                }, 20 * 3L);
        } else if (this.plugin.getConfig().getBoolean("spawn.tp")) {
            File spawnFile = new File("plugins//ServerSystem", "spawn.yml");
            if (!spawnFile.exists()) {
                if (!messaged.get()) {
                    e.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Spawn.NoSpawn", "spawn", "spawn", e.getPlayer().getName(), null));
                    messaged.set(true);
                }
            } else {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(spawnFile);
                if (Bukkit.getWorld(cfg.getString("Spawn.World")) == null) if (!messaged.get())
                    e.getPlayer().sendMessage(this.getPrefix() + this.getMessage("Spawn.NoSpawn", "spawn", "spawn", e.getPlayer().getName(), null));
                Location location = e.getPlayer().getLocation().clone();
                location.setX(cfg.getDouble("Spawn.X"));
                location.setY(cfg.getDouble("Spawn.Y"));
                location.setZ(cfg.getDouble("Spawn.Z"));
                location.setYaw((float) cfg.getDouble("Spawn.Yaw"));
                location.setPitch((float) cfg.getDouble("Spawn.Pitch"));
                location.setWorld(Bukkit.getWorld(cfg.getString("Spawn.World")));
                e.getPlayer().teleport(location);
            }
        }

        if (this.plugin.getMessages().getBoolean("Messages.Misc.JoinMessage.SendMessageToPlayer"))
            e.getPlayer().sendMessage(this.getMiscMessage("JoinMessage.MessageToPlayer", "Join", "Join", e.getPlayer(), null));
    }

    private void changeName(String name, Player player) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);

            Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
            Field ff = profile.getClass().getDeclaredField("name");
            ff.setAccessible(true);
            ff.set(profile, name);
            player.setCustomName(name);
            player.setDisplayName(name);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(player);
                p.showPlayer(player);
            }
            if (this.plugin.getVanish().isVanish(player)) this.plugin.getVanish().setVanish(true, player);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void changeName(String name, Player player, boolean colored) {
        if (!colored) this.changeName(name, player);
        else try {
            name = ChatColor.translateAlternateColorCodes('&', name);
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);

            Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
            Field ff = profile.getClass().getDeclaredField("name");
            ff.setAccessible(true);
            ff.set(profile, name);
            player.setCustomName(name);
            player.setDisplayName(name);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(player);
                p.showPlayer(player);
            }
            if (this.plugin.getVanish().isVanish(player)) this.plugin.getVanish().setVanish(true, player);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
