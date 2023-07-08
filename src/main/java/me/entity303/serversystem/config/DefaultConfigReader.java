package me.entity303.serversystem.config;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

public class DefaultConfigReader implements ConfigReader {
    private final File file;
    private final FileConfiguration cfg;
    private FileConfiguration originalCfg = null;
    private DefaultConfigReader newReader = null;

    public DefaultConfigReader(File file, ServerSystem plugin) {
        this.file = file;
        this.cfg = YamlConfiguration.loadConfiguration(file);
        if (plugin.getResource(file.getName()) != null)
            this.originalCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(file.getName())));
        else if (file.getName().equalsIgnoreCase("messages.yml"))
            if (plugin.getResource("messages_" + this.cfg.getString("language") + ".yml") != null)
                this.originalCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("messages_" + this.cfg.getString("language") + ".yml")));
            else {
                this.originalCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("messages_en.yml")));
                plugin.error("Couldn't find default message.yml for language'" + this.cfg.getString("language") + "'!");
                plugin.log("Using english...");
            }

        if (!this.validateConfig()) {
            plugin.warn("One or more errors with your '" + file.getName() + "' file were found and fixed, a backup was made before doing this!");
            try {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
                LocalDateTime now = LocalDateTime.now();
                String date = dtf.format(now);

                FileUtils.copyFile(file, new File("plugins" + File.separator + "ServerSystem", file.getName() + ".backup-" + date));
            } catch (IOException e) {
                e.printStackTrace();
                plugin.error("An error occurred while backing up, changes only saved internally/temporary!");
                return;
            }

            this.save();

            this.reload();
        }
    }

    public static ConfigReader loadConfiguration(File file) {
        return loadConfiguration(file, ServerSystem.getPlugin(ServerSystem.class));
    }

    public static ConfigReader loadConfiguration(File file, ServerSystem serverSystem) {
        return new DefaultConfigReader(file, serverSystem);
    }

    protected boolean validateConfig() {
        boolean fixed = false;
        for (String keys : this.originalCfg.getConfigurationSection("").getKeys(true))
            if (!keys.toLowerCase(Locale.ROOT).contains("example"))
                if (!this.cfg.isSet(keys)) {
                    ServerSystem.getPlugin(ServerSystem.class).warn("Fixing missing config entry '" + keys + "' in file '" + this.file.getName() + "'");
                    this.cfg.set(keys, this.originalCfg.get(keys));
                    fixed = true;
                } else {
                    Object object = this.cfg.get(keys);
                    Object supposedToBeObject = this.originalCfg.get(keys);
                    if (supposedToBeObject instanceof String) if (!(object instanceof String)) {
                        ServerSystem.getPlugin(ServerSystem.class).warn("Fixing invalid config entry '" + keys + "' in file '" + this.file.getName() + "' (Should be a string, but isn't)");
                        this.cfg.set(keys, supposedToBeObject);
                        fixed = true;
                    }

                    if (supposedToBeObject instanceof Integer) if (!(object instanceof Integer)) {
                        ServerSystem.getPlugin(ServerSystem.class).warn("Fixing invalid config entry '" + keys + "' in file '" + this.file.getName() + "' (Should be an integer, but isn't)");
                        this.cfg.set(keys, supposedToBeObject);
                        fixed = true;
                    }

                    if (supposedToBeObject instanceof Long) if (!(object instanceof Long)) {
                        ServerSystem.getPlugin(ServerSystem.class).warn("Fixing invalid config entry '" + keys + "' in file '" + this.file.getName() + "' (Should be a long, but isn't)");
                        this.cfg.set(keys, supposedToBeObject);
                        fixed = true;
                    }

                    if (supposedToBeObject instanceof Boolean) if (!(object instanceof Boolean)) {
                        ServerSystem.getPlugin(ServerSystem.class).warn("Fixing invalid config entry '" + keys + "' in file '" + this.file.getName() + "' (Should be a boolean, but isn't)");
                        this.cfg.set(keys, supposedToBeObject);
                        fixed = true;
                    }

                    if (supposedToBeObject instanceof Double) if (!(object instanceof Double)) {
                        ServerSystem.getPlugin(ServerSystem.class).warn("Fixing invalid config entry '" + keys + "' in file '" + this.file.getName() + "' (Should be a double, but isn't)");
                        this.cfg.set(keys, supposedToBeObject);
                        fixed = true;
                    }

                    if (supposedToBeObject instanceof ItemStack) if (!(object instanceof ItemStack)) {
                        ServerSystem.getPlugin(ServerSystem.class).warn("Fixing invalid config entry '" + keys + "' in file '" + this.file.getName() + "' (Should be an ItemStack, but isn't)");
                        this.cfg.set(keys, supposedToBeObject);
                        fixed = true;
                    }
                }
        return !fixed;
    }

    @Override
    public FileConfiguration getCfg() {
        return this.cfg;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public Object get(String path, Object def) {
        if (this.newReader != null)
            return this.newReader.get(path, def);

        this.setIfNotSet(path);
        return this.cfg.get(path, def);
    }

    @Override
    public Object get(String path) {
        return this.get(path, false);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        if (this.newReader != null)
            return this.newReader.getBoolean(path, def);

        this.setIfNotSet(path);
        return this.cfg.getBoolean(path, def);
    }

    @Override
    public boolean getBoolean(String path) {
        return this.getBoolean(path, false);
    }

    @Override
    public String getString(String path, String def) {
        if (this.newReader != null)
            return this.newReader.getString(path, def);

        this.setIfNotSet(path);
        return this.cfg.getString(path, def);
    }

    @Override
    public String getString(String path) {
        return this.getString(path, null);
    }

    @Override
    public int getInt(String path, int def) {
        if (this.newReader != null)
            return this.newReader.getInt(path, def);

        this.setIfNotSet(path);
        return this.cfg.getInt(path, def);
    }

    @Override
    public int getInt(String path) {
        return this.getInt(path, 0);
    }

    @Override
    public long getLong(String path, long def) {
        if (this.newReader != null)
            return this.newReader.getLong(path, def);

        this.setIfNotSet(path);
        return this.cfg.getLong(path, def);
    }

    @Override
    public long getLong(String path) {
        return this.getLong(path, 0L);
    }

    @Override
    public double getDouble(String path, double def) {
        if (this.newReader != null)
            return this.newReader.getDouble(path, def);

        this.setIfNotSet(path);
        return this.cfg.getDouble(path, def);
    }

    @Override
    public double getDouble(String path) {
        return this.getDouble(path, 0.0D);
    }

    @Override
    public ItemStack getItemStack(String path, ItemStack def) {
        if (this.newReader != null)
            return this.newReader.getItemStack(path, def);

        this.setIfNotSet(path);
        return this.cfg.getItemStack(path, def);
    }

    @Override
    public ItemStack getItemStack(String path) {
        return this.getItemStack(path, null);
    }

    @Override
    public void set(String path, Object object) {
        if (this.newReader != null) {
            this.newReader.set(path, object);
            return;
        }

        this.cfg.set(path, object);
    }

    @Override
    public boolean isConfigurationSection(String path) {
        if (this.newReader != null)
            return this.newReader.isConfigurationSection(path);
        return this.cfg.isConfigurationSection(path);
    }

    @Override
    public ConfigurationSection getConfigurationSection(String path) {
        if (this.newReader != null)
            return this.newReader.getConfigurationSection(path);

        this.setIfNotSet(path);

        return this.cfg.getConfigurationSection(path);
    }

    @Override
    public void save() {
        if (this.newReader != null) {
            this.newReader.save();
            return;
        }

        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        if (this.newReader != null) {
            this.newReader.reload();
            return;
        }

        try {
            this.cfg.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(File file) {
        try {
            this.newReader = new DefaultConfigReader(file, ServerSystem.getPlugin(ServerSystem.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIfNotSet(String path) {
        if (this.originalCfg == null)
            return;

        if (!this.cfg.isSet(path))
            if (this.originalCfg.isSet(path)) {
                String partialPath = "";
                int periods = (int) Arrays.stream(path.split("")).filter(s -> s.equalsIgnoreCase(".")).count();
                for (int i = 0; i <= periods; i++) {
                    String internalPath = path;
                    for (int i1 = 0; i1 < i; i1++)
                        internalPath = internalPath.substring(0, internalPath.lastIndexOf('.'));
                    if (this.cfg.isSet(internalPath)) {
                        partialPath = internalPath;
                        break;
                    }
                }

                if (partialPath.endsWith("."))
                    partialPath = partialPath.substring(0, partialPath.length() - 1);

                if (partialPath.startsWith("."))
                    partialPath = partialPath.substring(1);

                if (partialPath.equalsIgnoreCase("")) {
                    this.cfg.set(path, this.originalCfg.get(path));
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
                } else {
                    ConfigurationSection section = this.cfg.getConfigurationSection(partialPath);

                    if (section == null) {
                        this.cfg.set(path, this.originalCfg.get(path));
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
                        return;
                    }

                    section.set(partialPath, this.originalCfg.get(path));
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
                }
            }
    }
}
