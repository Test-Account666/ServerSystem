package me.entity303.serversystem.utils.versions;

import me.entity303.serversystem.actionbar.ActionBar;
import me.entity303.serversystem.commands.executable.EditSignCommand;
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
import me.entity303.serversystem.vanish.packets.VanishPacket_Reflection_1_21;
import me.entity303.serversystem.vanish.packets.VanishPacket_Reflection_Till_1_19_2;
import me.entity303.serversystem.vanish.packets.VanishPacket_Reflection_Till_1_20;
import me.entity303.serversystem.virtual.anvil.VirtualAnvil_Latest;
import me.entity303.serversystem.virtual.cartography.VirtualCartography_Latest;
import me.entity303.serversystem.virtual.grindstone.VirtualGrindstone_latest;
import me.entity303.serversystem.virtual.loom.VirtualLoom_Latest;
import me.entity303.serversystem.virtual.smithing.VirtualSmithing_Latest;
import me.entity303.serversystem.virtual.stonecutter.VirtualStoneCutter_Latest;
import org.bukkit.Bukkit;

public class VersionManager {
    private final ServerSystem _serverSystem;
    private String _nmsVersion;
    private String _version;
    private boolean _vanishFullyFunctional = true;

    public VersionManager(ServerSystem serverSystem) {
        this._serverSystem = serverSystem;
    }

    public void RegisterVersionStuff() {
        var splitVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",");

        this._nmsVersion = splitVersion.length > 3? splitVersion[3] + "." : "";
        this.FetchVersion();

        this._serverSystem.Info("ServerSystem is running on " + this._version + "!");

        try {
            this._serverSystem.GetVersionStuff()
                              .SetGetHandleMethod(Class.forName("org.bukkit.craftbukkit." + this._nmsVersion + "entity.CraftPlayer").getDeclaredMethod("getHandle"));
        } catch (NoSuchMethodException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        if (this._version.contains("1.21")) {
            this._vanishFullyFunctional = true;

            if (!this._serverSystem.isRunningPaper()) {
                this._serverSystem.Warn("It seems you're running Spigot! Due to recent changes, vanish requires Paper to be fully functional!");
                this._vanishFullyFunctional = false;
            }

            this.HandleFullySupportedVersion();
        } else if (this._version.contains("1.20")) {
            this._vanishFullyFunctional = this._version.contains("1.20.5") || this._version.contains("1.20.6");
            if (!this._vanishFullyFunctional) this._serverSystem.Warn("Vanish is not fully functional in version 1.20 - 1.20.4!");

            if (this._vanishFullyFunctional && !this._serverSystem.isRunningPaper()) {
                this._serverSystem.Warn("It seems you're running Spigot! Due to recent changes, vanish requires Paper to be fully functional!");
                this._vanishFullyFunctional = false;
            }

            this.HandleFullySupportedVersion();
        } else if (this._version.contains("1.19.R3")) {
            this.HandleFullySupportedPre21Version();
            this.RegisterObsoleteEditSignCommand();
        } else if (this._version.contains("1.19")) {
            this.HandleLegacyVersions();
        } else if (this._version.contains("1.18")) {
            this.HandleLegacyVersions();
        } else if (this._version.contains("1.17")) {
            this._serverSystem.GetVersionStuff().SetActionBar(new ActionBar());
            this._serverSystem.GetVersionStuff().SetVanishPacket(new VanishPacket_Reflection_Till_1_19_2(this._serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener(this._serverSystem), this._serverSystem);
            this._serverSystem.GetVersionStuff().SetSignEdit(new SignEdit_v1_17_R1());
            this.SetLatestVirtualInventories();
            this._serverSystem.GetVersionStuff().SetTeleport(new Teleport_v1_17_R1(this._serverSystem));
            this._serverSystem.GetVersionStuff().SetNbtViewer(new NBTViewer());
            this.RegisterObsoleteEditSignCommand();
        } else {
            this._serverSystem.Warn("Unsupported version detected! Continue with your own risk! Support may not guaranteed!");
            this._serverSystem.Warn("Keep in mind that all versions below 1.17.1 are unsupported since 2.0.0!");
            this._serverSystem.Warn("Using a version older than that, will *NOT* work!");
            this._serverSystem.Warn("Also, only the latest version will always be fully supported!");

            if (!this._serverSystem.isRunningPaper()) {
                this._serverSystem.Warn("It seems you're running Spigot! Due to recent changes, vanish requires Paper to be fully functional!");
            }

            this._vanishFullyFunctional = this._serverSystem.isRunningPaper();
            this.HandleFullySupportedVersion();
        }
    }

    public void FetchVersion() {
        if (this._version != null && !this._version.isEmpty()) return;

        if (this._nmsVersion.isEmpty()) {
            this._version = Bukkit.getVersion();

            var cutoff = this._version.lastIndexOf(':') + 1;

            this._version = this._version.substring(cutoff, this._version.length() - 1).replaceFirst(" ", "");
            return;
        }

        this._version = this._nmsVersion.replace("_", ".");
    }

    public boolean IsVanishFullyFunctional() {
        return this._vanishFullyFunctional;
    }

    private void HandleFullySupportedVersion() {
        var vanishPacket = this._vanishFullyFunctional? new VanishPacket_Reflection_1_21(this._serverSystem) : new VanishPacket_Reflection_Till_1_20(this._serverSystem);
        this._serverSystem.GetVersionStuff().SetActionBar(new ActionBar());
        this._serverSystem.GetVersionStuff().SetVanishPacket(vanishPacket);
        this.HandleGenericVersion();

        Bukkit.getScheduler()
              .runTaskLater(this._serverSystem, () -> this._serverSystem.GetCommandManager().RegisterCommand("skull", new SkullCommand(this._serverSystem), null), 5L);
    }

    private void HandleFullySupportedPre21Version() {
        this._serverSystem.GetVersionStuff().SetActionBar(new ActionBar());
        this._serverSystem.GetVersionStuff().SetVanishPacket(new VanishPacket_Reflection_Till_1_20(this._serverSystem));
        this.HandleGenericVersion();
    }

    private void HandleGenericVersion() {
        Bukkit.getPluginManager().registerEvents(new InteractListener(this._serverSystem), this._serverSystem);
        this._serverSystem.GetVersionStuff().SetSignEdit(new SignEdit_Reflection_Latest());
        this.SetLatestVirtualInventories();
        this._serverSystem.GetVersionStuff().SetTeleport(new Teleport_Latest(this._serverSystem));
        this._serverSystem.GetVersionStuff().SetNbtViewer(new NBTViewer());
    }

    private void RegisterObsoleteEditSignCommand() {
        Bukkit.getScheduler().runTaskLater(this._serverSystem, () -> {
            this._serverSystem.GetCommandManager().RegisterCommand("editsign", new EditSignCommand(this._serverSystem), null);
            this._serverSystem.GetCommandManager().RegisterCommand("skull", new SkullCommand(this._serverSystem), null);
        }, 5L);
    }

    private void HandleLegacyVersions() {
        this._serverSystem.GetVersionStuff().SetActionBar(new ActionBar());
        this._serverSystem.GetVersionStuff().SetVanishPacket(new VanishPacket_Reflection_Till_1_19_2(this._serverSystem));
        this.HandleGenericVersion();
        this.RegisterObsoleteEditSignCommand();
    }

    private void SetLatestVirtualInventories() {
        if (this._serverSystem.isRunningPaper()) {
            this._serverSystem.GetVersionStuff().SetVirtualAnvil(new VirtualAnvil_Latest());
            this._serverSystem.GetVersionStuff().SetVirtualCartography(new VirtualCartography_Latest());
            this._serverSystem.GetVersionStuff().SetVirtualGrindstone(new VirtualGrindstone_latest());
            this._serverSystem.GetVersionStuff().SetVirtualLoom(new VirtualLoom_Latest());
            this._serverSystem.GetVersionStuff().SetVirtualStoneCutter(new VirtualStoneCutter_Latest());
            this._serverSystem.GetVersionStuff().SetVirtualSmithing(new VirtualSmithing_Latest());
        }
        this._serverSystem.GetVersionStuff().SetSaveData(new SaveData_Latest(this._serverSystem));
        this._serverSystem.GetVersionStuff().SetEntityPlayer(new EntityPlayer_Latest(this._serverSystem));
    }

    public String GetNMSVersion() {
        return this._nmsVersion;
    }
}
