package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KitsManager {
    protected final ServerSystem _plugin;
    private final File _kitsFile = new File("plugins//ServerSystem", "kits.yml");
    private final FileConfiguration _kitsConfiguration;
    private final File _delaysFile = new File("plugins//ServerSystem", "delays.yml");
    private final FileConfiguration _delaysConfiguration;

    public KitsManager(ServerSystem plugin) {
        this._plugin = plugin;
        this._kitsConfiguration = YamlConfiguration.loadConfiguration(this._kitsFile);
        this._delaysConfiguration = YamlConfiguration.loadConfiguration(this._delaysFile);
    }

    public void SetDelay(String uuid, String name, Long current) {
        this._delaysConfiguration.set("Players." + uuid + "." + name, current);

        try {
            this._delaysConfiguration.save(this._delaysFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try {
            this._delaysConfiguration.load(this._delaysFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public boolean DoesKitExist(String name) {
        if (!this._kitsFile.exists()) return false;
        name = name.toLowerCase();
        return this._kitsConfiguration.contains("Kits." + name);
    }

    public boolean IsKitAllowed(CommandSender commandSender, String kit, boolean others) {
        try {
            if (!others) {
                var permission = this._plugin.GetPermissions().GetPermission("kit.self").replace("<KIT>", kit.toLowerCase());

                var hasPermission = this._plugin.GetPermissions().HasPermissionString(commandSender, permission);

                commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));
                return hasPermission;
            } else {
                var permission = this._plugin.GetPermissions().GetPermission("kit.others").replace("<KIT>", kit.toLowerCase());

                var hasPermission = this._plugin.GetPermissions().HasPermissionString(commandSender, permission);

                if (!hasPermission) commandSender.sendMessage(this._plugin.GetMessages().GetPrefix() + this._plugin.GetMessages().GetNoPermission(permission));

                return hasPermission;
            }
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    public boolean IsKitAllowed(CommandSender commandSender, String kit, boolean others, boolean disableNoPermissionMessage) {
        try {
            if (!others) {
                return this._plugin.GetPermissions()
                                   .HasPermissionString(commandSender, this._plugin.GetPermissions().GetPermission("kit.self").replace("<KIT>", kit.toLowerCase()),
                                                        disableNoPermissionMessage);
            } else {
                return this._plugin.GetPermissions()
                                   .HasPermissionString(commandSender, this._plugin.GetPermissions().GetPermission("kit.others").replace("<KIT>", kit.toLowerCase()),
                                                        disableNoPermissionMessage);
            }
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    public KitsManager GiveKit(Player player, String kitName) {
        if (!this._kitsFile.exists()) return this;
        var kit = this.GetKit(kitName);
        if (this.GetKits().isEmpty()) return this;
        for (var index = 0; index < 41; index++) {
            if (kit.get(index) == null) continue;

            if (index <= 35) {
                if (player.getInventory().getItem(index) == null) {
                    player.getInventory().setItem(index, kit.get(index));
                } else {
                    player.getInventory().addItem(kit.get(index));
                }
                continue;
            }

            if (index == 36) {
                if (player.getInventory().getHelmet() == null) {
                    player.getInventory().setHelmet(kit.get(index));
                } else {
                    player.getInventory().addItem(kit.get(index));
                }
                continue;
            }

            if (index == 37) {
                if (player.getInventory().getChestplate() == null) {
                    player.getInventory().setChestplate(kit.get(index));
                } else {
                    player.getInventory().addItem(kit.get(index));
                }
                continue;
            }
            if (index == 38) {
                if (player.getInventory().getLeggings() == null) {
                    player.getInventory().setLeggings(kit.get(index));
                } else {
                    player.getInventory().addItem(kit.get(index));
                }
                continue;
            }
            if (index == 39) {
                if (player.getInventory().getBoots() == null) {
                    player.getInventory().setBoots(kit.get(index));
                } else {
                    player.getInventory().addItem(kit.get(index));
                }
                continue;
            }
            player.getInventory().getItemInOffHand();
            player.getInventory().addItem(kit.get(index));
            break;
        }
        return this;
    }

    public Map<Integer, ItemStack> GetKit(String name) {
        Map<Integer, ItemStack> kit = new HashMap<>();
        if (!this._kitsFile.exists()) return new HashMap<>();
        try {
            for (var index = 0; index < 41; index++) {
                if (!this._kitsConfiguration.contains("Kits." + name.toLowerCase() + "." + index)) {
                    kit.put(index, null);
                    continue;
                }
                var itemStack = this._kitsConfiguration.getItemStack("Kits." + name.toLowerCase() + "." + index);
                kit.put(index, itemStack);
            }
        } catch (Exception ignored) {
        }
        return kit;
    }

    public List<Map<Integer, ItemStack>> GetKits() {
        if (!this._kitsFile.exists()) return new ArrayList<>();
        return this.GetKitNames().stream().map(this::GetKit).collect(Collectors.toList());
    }

    public List<String> GetKitNames() {
        if (!this._kitsFile.exists()) return new ArrayList<>();
        return new ArrayList<>(this._kitsConfiguration.getConfigurationSection("Kits").getKeys(false));
    }

    public boolean IsKitDelayed(Player player, String name) {
        Long delay = this.GetKitDelay(name);
        var lastEntered = this.GetPlayerLastDelay(player.getUniqueId().toString(), name);
        return lastEntered + delay > System.currentTimeMillis();
    }

    public long GetKitDelay(String name) {
        return this._kitsConfiguration.getLong("Kits." + name.toLowerCase() + ".Delay");
    }

    public Long GetPlayerLastDelay(String uuid, String name) {
        name = name.toLowerCase();
        if (this._delaysConfiguration.isSet("Players." + uuid + "." + name)) return this._delaysConfiguration.getLong("Players." + uuid + "." + name);
        return 0L;
    }

    public KitsManager AddKit(String name, Map<Integer, ItemStack> kit, Long delay) {
        for (var index = 0; index < 41; index++) {
            if (kit.get(index) == null) continue;
            var item = kit.get(index);
            this._kitsConfiguration.set("Kits." + name.toLowerCase() + "." + index, item);
        }

        this._kitsConfiguration.set("Kits." + name.toLowerCase() + ".Delay", delay);

        try {
            this._kitsConfiguration.save(this._kitsFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try {
            this._kitsConfiguration.load(this._kitsFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
        return this;
    }

    public KitsManager DeleteKit(String name) {
        if (!this._kitsFile.exists()) return this;
        this._kitsConfiguration.set("Kits." + name.toLowerCase(), null);

        try {
            this._kitsConfiguration.save(this._kitsFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try {
            this._kitsConfiguration.load(this._kitsFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
        return this;
    }
}
