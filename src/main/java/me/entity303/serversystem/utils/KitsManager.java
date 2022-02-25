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

public class KitsManager extends MessageUtils {
    private final File file = new File("plugins//ServerSystem", "kits.yml");
    private final FileConfiguration cfg;
    private final File file2 = new File("plugins//ServerSystem", "delays.yml");
    private final FileConfiguration cfg2;

    public KitsManager(ServerSystem plugin) {
        super(plugin);
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
        this.cfg2 = YamlConfiguration.loadConfiguration(this.file2);
    }

    public void setDelay(String uuid, String name, Long current) {
        this.cfg2.set("Players." + uuid + "." + name, current);

        try {
            this.cfg2.save(this.file2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.cfg2.load(this.file2);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public boolean doesKitExist(String name) {
        if (!this.file.exists()) return false;
        name = name.toLowerCase();
        return this.cfg.contains("Kits." + name);
    }

    public Map<Integer, ItemStack> getKit(String name) {
        Map<Integer, ItemStack> kit = new HashMap<>();
        if (!this.file.exists()) return new HashMap<>();
        try {
            for (int i = 0; i < 41; i++) {
                if (!this.cfg.contains("Kits." + name.toLowerCase() + "." + i)) {
                    kit.put(i, null);
                    continue;
                }
                ItemStack itemStack = this.cfg.getItemStack("Kits." + name.toLowerCase() + "." + i);
                kit.put(i, itemStack);
            }
        } catch (Exception ignored) {
        }
        return kit;
    }

    public long getKitDelay(String name) {
        return this.cfg.getLong("Kits." + name.toLowerCase() + ".Delay");
    }

    public List<String> getKitNames() {
        if (!this.file.exists()) return new ArrayList<>();
        return new ArrayList<>(this.cfg.getConfigurationSection("Kits").getKeys(false));
    }

    public List<Map<Integer, ItemStack>> getKits() {
        if (!this.file.exists()) return new ArrayList<>();
        return this.getKitNames().stream().map(this::getKit).collect(Collectors.toList());
    }

    public boolean isKitAllowed(CommandSender cs, String kit, boolean others) {
        try {
            if (!others) return this.isPermAllowed(cs, this.Perm("kit.self").replace("<KIT>", kit.toLowerCase()));
            else
                return this.isPermAllowed(cs, this.Perm("kit.others").replace("<KIT>", kit.toLowerCase()));
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    public boolean isKitAllowed(CommandSender cs, String kit, boolean others, boolean noFuck) {
        try {
            if (!others)
                return this.isPermAllowed(cs, this.Perm("kit.self").replace("<KIT>", kit.toLowerCase()), noFuck);
            else
                return this.isPermAllowed(cs, this.Perm("kit.others").replace("<KIT>", kit.toLowerCase()), noFuck);
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    public KitsManager giveKit(Player player, String kitName) {
        if (!this.file.exists()) return this;
        Map<Integer, ItemStack> kit = this.getKit(kitName);
        if (this.getKits().size() <= 0) return this;
        for (int i = 0; i < 41; i++) {
            if (kit.get(i) == null) continue;

            if (i <= 35) {
                if (player.getInventory().getItem(i) == null) player.getInventory().setItem(i, kit.get(i));
                else player.getInventory().addItem(kit.get(i));
                continue;
            }

            if (i == 36) {
                if (player.getInventory().getHelmet() == null) player.getInventory().setHelmet(kit.get(i));
                else player.getInventory().addItem(kit.get(i));
                continue;
            }

            if (i == 37) {
                if (player.getInventory().getChestplate() == null) player.getInventory().setChestplate(kit.get(i));
                else player.getInventory().addItem(kit.get(i));
                continue;
            }
            if (i == 38) {
                if (player.getInventory().getLeggings() == null) player.getInventory().setLeggings(kit.get(i));
                else player.getInventory().addItem(kit.get(i));
                continue;
            }
            if (i == 39) {
                if (player.getInventory().getBoots() == null) player.getInventory().setBoots(kit.get(i));
                else player.getInventory().addItem(kit.get(i));
                continue;
            }
            if (!this.plugin.getVersionManager().is188()) {
                player.getInventory().getItemInOffHand();
                player.getInventory().addItem(kit.get(i));
            }
            break;
        }
        return this;
    }

    public boolean isKitDelayed(Player player, String name) {
        Long delay = this.getKitDelay(name);
        Long lastEntered = this.getPlayerLastDelay(player.getUniqueId().toString(), name);
        return lastEntered + delay > System.currentTimeMillis();
    }

    public Long getPlayerLastDelay(String uuid, String name) {
        name = name.toLowerCase();
        if (this.cfg2.isSet("Players." + uuid + "." + name)) return this.cfg2.getLong("Players." + uuid + "." + name);
        return 0L;
    }

    public KitsManager addKit(String name, Map<Integer, ItemStack> kit, Long delay) {
        for (int i = 0; i < 41; i++) {
            if (kit.get(i) == null) continue;
            ItemStack item = kit.get(i);
            this.cfg.set("Kits." + name.toLowerCase() + "." + i, item);
        }

        this.cfg.set("Kits." + name.toLowerCase() + ".Delay", delay);

        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.cfg.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return this;
    }

    public KitsManager deleteKit(String name) {
        if (!this.file.exists()) return this;
        this.cfg.set("Kits." + name.toLowerCase(), null);

        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.cfg.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return this;
    }
}
