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
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class DefaultConfigReader implements IConfigReader {
    private static final Pattern SPLIT_PATTERN = Pattern.compile("");
    private final ServerSystem _plugin;
    private final File _file;
    private final FileConfiguration _configuration;
    private FileConfiguration _originalCfg = null;
    private DefaultConfigReader _newReader = null;

    public DefaultConfigReader(File file, ServerSystem plugin) {
        this._plugin = plugin;
        this._file = file;
        this._configuration = YamlConfiguration.loadConfiguration(file);
        this.FetchInternalConfig();

        if (this.ValidateConfig()) return;

        this.CreateBackupAndSave();
    }

    private void FetchInternalConfig() {
        if (this._plugin.getResource(this._file.getName()) != null) {
            this._originalCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(this._plugin.getResource(this._file.getName())));
        } else if (this._file.getName().equalsIgnoreCase("messages.yml")) {
            if (this._plugin.getResource("messages_" + this._configuration.getString("language") + ".yml") != null) {
                this._originalCfg = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(this._plugin.getResource("messages_" + this._configuration.getString("language") + ".yml")));
            } else {
                this._originalCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(this._plugin.getResource("messages_en.yml")));
                this._plugin.Error("Couldn't find default message.yml for language'" + this._configuration.getString("language") + "'!");
                this._plugin.Info("Using english...");
            }
        }
    }

    protected boolean ValidateConfig() {
        var fixed = false;

        var typeWarnings = new HashMap<Class<?>, String>();
        typeWarnings.put(String.class, "Should be a string, but isn't");
        typeWarnings.put(Integer.class, "Should be an integer, but isn't");
        typeWarnings.put(Long.class, "Should be a long, but isn't");
        typeWarnings.put(Boolean.class, "Should be a boolean, but isn't");
        typeWarnings.put(Double.class, "Should be a double, but isn't");
        typeWarnings.put(ItemStack.class, "Should be an ItemStack, but isn't");

        for (var key : this._originalCfg.getConfigurationSection("").getKeys(true)) {
            if (key.toLowerCase(Locale.ROOT).contains("example")) continue;

            if (!this._configuration.isSet(key)) {
                this._plugin.Warn("Fixing missing config entry '" + key + "' in file '" + this._file.getName() + "'");
                this._configuration.set(key, this._originalCfg.get(key));
                fixed = true;
                continue;
            }

            var object = this._configuration.get(key);
            var supposedToBeObject = this._originalCfg.get(key);
            var objectType = object.getClass();
            var supposedType = supposedToBeObject.getClass();

            if (objectType.isAssignableFrom(supposedType)) continue;

            var warningMessage = typeWarnings.get(supposedType);
            if (warningMessage != null) {
                this._plugin.Warn("Fixing invalid config entry '" + key + "' in file '" + this._file.getName() + "' (" + warningMessage + ")");
                this._configuration.set(key, supposedToBeObject);
                fixed = true;
            }
        }
        return !fixed;
    }

    private void CreateBackupAndSave() {
        this._plugin.Warn("One or more errors with your '" + this._file.getName() + "' file were found and fixed, a backup was made before doing this!");
        try {
            var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH-mm-ss");
            var now = LocalDateTime.now();
            var date = dtf.format(now);

            FileUtils.CopyFile(this._file, new File("plugins" + File.separator + "ServerSystem", this._file.getName() + ".backup-" + date));
        } catch (IOException exception) {
            exception.printStackTrace();
            this._plugin.Error("An error occurred while backing up, changes only saved internally/temporary!");
            return;
        }

        this.Save();

        this.Reload();
    }

    public static IConfigReader LoadConfiguration(File file) {
        return LoadConfiguration(file, ServerSystem.getPlugin(ServerSystem.class));
    }

    public static IConfigReader LoadConfiguration(File file, ServerSystem serverSystem) {
        return new DefaultConfigReader(file, serverSystem);
    }

    @Override
    public FileConfiguration GetConfiguration() {
        return this._configuration;
    }

    @Override
    public File GetFile() {
        return this._file;
    }

    @Override
    public Object GetObject(String path, Object def) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            other.SetIfNotSet(path);
            return other._configuration.get(path, def);
        }
    }

    @Override
    public Object GetObject(String path) {
        return this.GetObject(path, false);
    }

    @Override
    public boolean GetBoolean(String path, boolean def) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            other.SetIfNotSet(path);
            return other._configuration.getBoolean(path, def);
        }
    }

    @Override
    public boolean GetBoolean(String path) {
        return this.GetBoolean(path, false);
    }

    @Override
    public String GetString(String path, String def) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            other.SetIfNotSet(path);
            return other._configuration.getString(path, def);
        }
    }

    @Override
    public String GetString(String path) {
        return this.GetString(path, null);
    }

    @Override
    public int GetInt(String path, int def) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            other.SetIfNotSet(path);
            return other._configuration.getInt(path, def);
        }
    }

    @Override
    public int GetInt(String path) {
        return this.GetInt(path, 0);
    }

    @Override
    public long GetLong(String path, long def) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            other.SetIfNotSet(path);
            return other._configuration.getLong(path, def);
        }
    }

    @Override
    public long GetLong(String path) {
        return this.GetLong(path, 0L);
    }

    @Override
    public double GetDouble(String path, double def) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            other.SetIfNotSet(path);
            return other._configuration.getDouble(path, def);
        }
    }

    @Override
    public double GetDouble(String path) {
        return this.GetDouble(path, 0.0D);
    }

    @Override
    public ItemStack GetItemStack(String path, ItemStack def) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            other.SetIfNotSet(path);
            return other._configuration.getItemStack(path, def);
        }
    }

    @Override
    public ItemStack GetItemStack(String path) {
        return this.GetItemStack(path, null);
    }

    @Override
    public void Set(String path, Object object) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            other._configuration.set(path, object);
            return;
        }
    }

    @Override
    public void Save() {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            try {
                other._configuration.save(other._file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return;
        }
    }

    @Override
    public void Reload() {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            try {
                other._configuration.load(other._file);
            } catch (IOException | InvalidConfigurationException exception) {
                exception.printStackTrace();
            }
            return;
        }
    }

    @Override
    public void Load(File file) {
        try {
            this._newReader = new DefaultConfigReader(file, ServerSystem.getPlugin(ServerSystem.class));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public ConfigurationSection GetConfigurationSection(String path) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }

            other.SetIfNotSet(path);

            return other._configuration.getConfigurationSection(path);
        }
    }

    @Override
    public boolean IsConfigurationSection(String path) {
        var other = this;
        while (true) {
            if (other._newReader != null) {
                other = other._newReader;
                continue;
            }
            return other._configuration.isConfigurationSection(path);
        }
    }

    private void SetIfNotSet(String path) {
        if (this._originalCfg == null) return;

        if (this._configuration.isSet(path)) return;

        if (!this._originalCfg.isSet(path)) return;

        var partialPath = "";
        var periods = (int) Arrays.stream(SPLIT_PATTERN.split(path)).filter(period -> period.equalsIgnoreCase(".")).count();
        for (var index = 0; index <= periods; index++) {
            var internalPath = path;
            for (var index1 = 0; index1 < index; index1++)
                internalPath = internalPath.substring(0, internalPath.lastIndexOf('.'));
            if (this._configuration.isSet(internalPath)) {
                partialPath = internalPath;
                break;
            }
        }

        if (partialPath.endsWith(".")) partialPath = partialPath.substring(0, partialPath.length() - 1);

        if (partialPath.startsWith(".")) partialPath = partialPath.substring(1);

        if (partialPath.equalsIgnoreCase("")) {
            this._configuration.set(path, this._originalCfg.get(path));
            try {
                this._configuration.save(this._file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            try {
                this._configuration.load(this._file);
            } catch (IOException | InvalidConfigurationException exception) {
                exception.printStackTrace();
            }
            return;
        }

        var section = this._configuration.getConfigurationSection(partialPath);

        if (section == null) {
            this._configuration.set(path, this._originalCfg.get(path));
            try {
                this._configuration.save(this._file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            try {
                this._configuration.load(this._file);
            } catch (IOException | InvalidConfigurationException exception) {
                exception.printStackTrace();
            }
            return;
        }

        section.set(partialPath, this._originalCfg.get(path));
        try {
            this._configuration.save(this._file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            this._configuration.load(this._file);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }
}
