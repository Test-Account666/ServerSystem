package me.testaccount666.migration.essentials;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.executables.warp.manager.WarpManager;

import java.util.logging.Level;

public class WarpMigrator {

    public int migrateFrom() {
        var warpManager = ServerSystem.Instance.getRegistry().getService(WarpManager.class);
        var essentials = Essentials.getPlugin(Essentials.class);

        var count = 0;
        for (var warpName : essentials.getWarps().getList())
            try {
                var location = essentials.getWarps().getWarp(warpName);
                warpManager.addWarp(warpName, location);

                count += 1;
            } catch (IllegalArgumentException | WarpNotFoundException exception) {
                ServerSystem.getLog().log(Level.WARNING, "Couldn't migrate warp '${warpName}'", exception);
            }

        return count;
    }

    public int migrateTo() {
        var warpManager = ServerSystem.Instance.getRegistry().getService(WarpManager.class);
        var essentials = Essentials.getPlugin(Essentials.class);

        var count = 0;
        for (var warp : warpManager.getWarps())
            try {
                var warpName = warp.getDisplayName();
                var location = warp.getLocation();

                essentials.getWarps().setWarp(warpName, location);

                count += 1;
            } catch (Exception exception) {
                ServerSystem.getLog().log(Level.WARNING, "Couldn't migrate warp '${warp.getDisplayName()}'", exception);
            }

        return count;
    }
}
