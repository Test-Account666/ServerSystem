package me.testaccount666.serversystem.commands.executables.kit.manager;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KitManager {
    private final Map<String, Kit> _kits = new HashMap<>();

    public List<String> getAllKitNames() {
        return new ArrayList<>(_kits.keySet());
    }

    public KitManager() {
        var kitDirectory = Path.of(ServerSystem.Instance.getDataFolder().getPath(), "Kits").toFile();
        if (!kitDirectory.exists()) kitDirectory.mkdirs();
        else if (!kitDirectory.isDirectory()) throw new IllegalStateException("The Kit directory is not a directory!");

        var kitFiles = kitDirectory.listFiles();
        if (kitFiles == null) return;

        for (var kitFile : kitFiles) {
            var kitConfig = YamlConfiguration.loadConfiguration(kitFile);
            var name = kitConfig.getString("Name");
            if (name == null) continue;

            var coolDown = kitConfig.getLong("Cooldown", -1);

            var offHandItem = kitConfig.getItemStack("Items.OffHand", null);
            var armorContents = new LinkedList<ItemStack>();
            var inventoryContents = new LinkedList<ItemStack>();

            var armorSection = kitConfig.getConfigurationSection("Items.Armor");
            if (armorSection != null) for (var key = 0; key < 4; key++) {
                var item = armorSection.getItemStack(String.valueOf(key), null);
                armorContents.add(item);
            }

            var inventorySection = kitConfig.getConfigurationSection("Items.Inventory");
            if (inventorySection != null) for (var key = 0; key < 36; key++) {
                var item = inventorySection.getItemStack(String.valueOf(key), null);
                inventoryContents.add(item);
            }

            _kits.put(name, new Kit(name, coolDown, offHandItem,
                    armorContents.toArray(new ItemStack[0]),
                    inventoryContents.toArray(new ItemStack[0])));
        }
    }

    public Optional<Kit> getKit(String name) {
        return Optional.ofNullable(_kits.get(name));
    }

    public boolean kitExists(String name) {
        return _kits.containsKey(name);
    }

    public void addKit(Kit kit) {
        _kits.put(kit.getName(), kit);
    }

    public void removeKit(String name) {
        _kits.remove(name);
    }
}
