package me.entity303.serversystem.utils.versions;

import me.entity303.serversystem.actionbar.ActionBar;
import me.entity303.serversystem.commands.executable.EditSignCommand;
import me.entity303.serversystem.commands.executable.EditSignPlotSquaredCommand;
import me.entity303.serversystem.commands.executable.SkullCommand;
import me.entity303.serversystem.listener.vanish.InteractListener;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.signedit.SignEdit_Reflection_Latest;
import me.entity303.serversystem.signedit.SignEdit_v1_17_R1;
import me.entity303.serversystem.utils.versions.nbt.NBTViewer;
import me.entity303.serversystem.utils.versions.offlineplayer.data.SaveData_Latest;
import me.entity303.serversystem.utils.versions.offlineplayer.entityplayer.EntityPlayer_Latest;
import me.entity303.serversystem.utils.versions.offlineplayer.teleport.Teleport_Latest;
import me.entity303.serversystem.utils.versions.offlineplayer.teleport.Teleport_v1_17_R1;
import me.entity303.serversystem.vanish.packets.VanishPacket_Reflection_Latest;
import me.entity303.serversystem.vanish.packets.VanishPacket_Reflection_Till_1_19_2;
import me.entity303.serversystem.virtual.anvil.VirtualAnvil_Latest;
import me.entity303.serversystem.virtual.cartography.VirtualCartography_Latest;
import me.entity303.serversystem.virtual.grindstone.VirtualGrindstone_latest;
import me.entity303.serversystem.virtual.loom.VirtualLoom_Latest;
import me.entity303.serversystem.virtual.smithing.VirtualSmithing_Latest;
import me.entity303.serversystem.virtual.stonecutter.VirtualStoneCutter_Latest;
import org.bukkit.Bukkit;

public class VersionManager {
    private final ServerSystem serverSystem;
    private String nmsVersion;
    private String version;
    private boolean vanishFullyFunctional = true;

    public VersionManager(ServerSystem serverSystem) {
        this.serverSystem = serverSystem;
    }

    public void registerVersionStuff() {
        this.nmsVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        this.fetchVersion();

        this.serverSystem.log("ServerSystem is running on " + this.version + "!");

        try {
            this.serverSystem.getVersionStuff()
                             .setGetHandleMethod(
                                     Class.forName("org.bukkit.craftbukkit." + this.nmsVersion + ".entity.CraftPlayer").getDeclaredMethod("getHandle"));
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (this.version.contains("1.20")) {
            this.serverSystem.warn("Vanish is currently not fully functional in version 1.20+");
            this.vanishFullyFunctional = false;

            this.handleFullySupportedVersion();
            Bukkit.getScheduler()
                  .runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().registerCommand("skull", new SkullCommand(this.serverSystem), null), 5L);
        } else if (this.version.contains("1.19.R3")) {
            this.handleFullySupportedVersion();
            this.registerObsoleteEditSignCommand();
        } else if (this.version.contains("1.19"))
            this.handleLegacyVersions();
        else if (this.version.contains("1.18"))
            this.handleLegacyVersions();
        else if (this.version.contains("1.17")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Till_1_19_2(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_v1_17_R1());
            this.setLatestVirtualInventories();
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_17_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer());
            this.registerObsoleteEditSignCommand();
        } else {
            this.serverSystem.warn("Unsupported version detected! Continue with your own risk! Support may not guaranteed!");
            this.serverSystem.warn("Keep in mind that all versions below 1.17.1 are unsupported since 2.0.0!");
            this.serverSystem.warn("Using a version older than that, will *NOT* work!");
            this.serverSystem.warn("Also, only the latest version will always be fully supported!");

            this.serverSystem.warn("Vanish is currently not fully functional in version 1.20+");
            this.vanishFullyFunctional = false;
            this.handleFullySupportedVersion();
            Bukkit.getScheduler()
                  .runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().registerCommand("skull", new SkullCommand(this.serverSystem), null), 5L);
        }
    }

    public void fetchVersion() {
        if (this.version != null && !this.version.isEmpty())
            return;

        try {
            this.version = this.nmsVersion.replace("_", ".");
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public boolean isVanishFullyFunctional() {
        return this.vanishFullyFunctional;
    }

    private void handleFullySupportedVersion() {
        this.serverSystem.getVersionStuff().setActionBar(new ActionBar());
        this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Latest(this.serverSystem));
        this.handleGenericVersion();
    }

    private void handleGenericVersion() {
        Bukkit.getPluginManager().registerEvents(new InteractListener(this.serverSystem), this.serverSystem);
        this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Latest());
        this.setLatestVirtualInventories();
        this.serverSystem.getVersionStuff().setTeleport(new Teleport_Latest(this.serverSystem));
        this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer());
    }

    private void registerObsoleteEditSignCommand() {
        Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
            this.serverSystem.getCommandManager()
                             .registerCommand("editsign", Bukkit.getPluginManager().getPlugin("PlotSquared") != null?
                                                          new EditSignPlotSquaredCommand(this.serverSystem) :
                                                          new EditSignCommand(this.serverSystem), null);
            this.serverSystem.getCommandManager().registerCommand("skull", new SkullCommand(this.serverSystem), null);
        }, 5L);
    }

    private void handleLegacyVersions() {
        this.serverSystem.getVersionStuff().setActionBar(new ActionBar());
        this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Till_1_19_2(this.serverSystem));
        this.handleGenericVersion();
        this.registerObsoleteEditSignCommand();
    }

    private void setLatestVirtualInventories() {
        this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_Latest());
        this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_Latest());
        this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_latest());
        this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_Latest());
        this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_Latest());
        this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_Latest());
        this.serverSystem.getVersionStuff().setSaveData(new SaveData_Latest(this.serverSystem));
        this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Latest(this.serverSystem));
    }

    public String getNMSVersion() {
        return this.nmsVersion;
    }
}
