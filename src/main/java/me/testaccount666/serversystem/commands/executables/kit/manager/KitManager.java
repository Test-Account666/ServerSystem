package me.testaccount666.serversystem.commands.executables.kit.manager;

import me.testaccount666.serversystem.ServerSystem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

public class KitManager {
    private final Map<String, Kit> _kits = new HashMap<>();

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

    public List<String> getAllKitNames() {
        return new ArrayList<>(_kits.keySet());
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

        var kitFile = getKitFile(name);
        if (kitFile.exists()) if (!kitFile.delete()) ServerSystem.Instance.getLogger().warning("Failed to delete kit file: ${kitFile.getPath()}");
    }

    /**
     * Saves a kit to a file in the Kits directory.
     *
     * @param kit The kit to save
     * @return true if the kit was saved successfully, false otherwise
     */
    public boolean saveKit(Kit kit) {
        var kitFile = getKitFile(kit.getName());
        var kitConfig = new YamlConfiguration();

        kitConfig.set("Name", kit.getName());
        kitConfig.set("Cooldown", kit.getCoolDown());

        kitConfig.set("Items.OffHand", kit.getOffHandItem());

        var armorContents = kit.getArmorContents();
        if (armorContents != null)
            for (var index = 0; index < armorContents.length; index++) kitConfig.set("Items.Armor.${index}", armorContents[index]);

        var inventoryContents = kit.getInventoryContents();
        if (inventoryContents != null) for (var index = 0; index < inventoryContents.length; index++)
            kitConfig.set("Items.Inventory.${index}", inventoryContents[index]);

        try {
            kitConfig.save(kitFile);
            return true;
        } catch (IOException exception) {
            ServerSystem.Instance.getLogger().log(Level.SEVERE, "Failed to save kit '${kit.getName()}'", exception);
            return false;
        }
    }

    /**
     * Saves all kits to files in the Kits directory.
     *
     * @return The number of kits that were saved successfully
     */
    public int saveAllKits() {
        var successCount = 0;

        for (var kit : _kits.values()) if (saveKit(kit)) successCount++;

        return successCount;
    }

    /**
     * Gets the file for a kit with the given name.
     *
     * @param name The name of the kit
     * @return The file for the kit
     */
    private File getKitFile(String name) {
        var kitDirectory = Path.of(ServerSystem.Instance.getDataFolder().getPath(), "Kits").toFile();
        if (!kitDirectory.exists()) kitDirectory.mkdirs();

        return new File(kitDirectory, "${name}.yml");
    }
}
