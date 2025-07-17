package me.testaccount666.serversystem.clickablesigns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SignManager {
    private final Map<Location, SignType> _signTypes = new HashMap<>();
    private final File _signDirectory = Path.of("plugins", "ServerSystem", "data", "signs").toFile();

    public void addSignType(Location location, SignType signType) {
        _signTypes.put(location, signType);
    }

    public void removeSignType(Location location) {
        _signTypes.remove(location);
    }

    public Optional<SignType> getSignType(Location location) {
        return Optional.ofNullable(_signTypes.get(location));
    }

    public void loadSignTypes() {
        if (!_signDirectory.exists()) return;
        var files = _signDirectory.listFiles();
        if (files == null) return;

        for (var file : files) {
            var fileConfig = YamlConfiguration.loadConfiguration(file);
            var key = fileConfig.getString("Key");
            if (key == null) continue;
            var signTypeOptional = SignType.getSignTypeByKey(key);
            if (signTypeOptional.isEmpty()) continue;
            var signType = signTypeOptional.get();
            var locationString = file.getName().substring(0, file.getName().indexOf("."));
            var locationSplit = locationString.split("_");
            if (locationSplit.length != 4) continue;

            var worldName = locationSplit[0];
            var xString = locationSplit[1];
            var yString = locationSplit[2];
            var zString = locationSplit[3];

            var world = Bukkit.getWorld(worldName);
            if (world == null) continue;
            var x = Integer.parseInt(xString);
            var y = Integer.parseInt(yString);
            var z = Integer.parseInt(zString);
            var location = new Location(world, x, y, z);
            addSignType(location, signType);
        }
    }
}
