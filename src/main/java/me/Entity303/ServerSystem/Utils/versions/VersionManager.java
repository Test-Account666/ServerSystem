package me.Entity303.ServerSystem.Utils.versions;

import me.Entity303.ServerSystem.ActionBar.ActionBar_1_16;
import me.Entity303.ServerSystem.ActionBar.ActionBar_Latest;
import me.Entity303.ServerSystem.ActionBar.ActionBar_v1_12_R1_to_v1_15_R1;
import me.Entity303.ServerSystem.ActionBar.ActionBar_v1_8_R3_to_v1_11_R1;
import me.Entity303.ServerSystem.Commands.executable.COMMAND_skull;
import me.Entity303.ServerSystem.Commands.executable.COMMAND_skull_newer;
import me.Entity303.ServerSystem.Listener.Vanish.*;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.SignEdit.SignEdit_Reflection_Latest;
import me.Entity303.ServerSystem.SignEdit.SignEdit_Reflection_Old;
import me.Entity303.ServerSystem.Utils.versions.commands.BukkitCommandWrap_Reflection_Latest;
import me.Entity303.ServerSystem.Utils.versions.commands.BukkitCommandWrap_Reflection_Old;
import me.Entity303.ServerSystem.Utils.versions.offlineplayer.data.SaveData_Latest;
import me.Entity303.ServerSystem.Utils.versions.offlineplayer.data.SaveData_Old;
import me.Entity303.ServerSystem.Utils.versions.offlineplayer.entityplayer.EntityPlayer_Latest;
import me.Entity303.ServerSystem.Utils.versions.offlineplayer.entityplayer.EntityPlayer_Old;
import me.Entity303.ServerSystem.Vanish.Packets.VanishPacket_Reflection_Latest;
import me.Entity303.ServerSystem.Vanish.Packets.VanishPacket_Reflection_Old;
import me.Entity303.ServerSystem.Vanish.Packets.VanishPacket_Reflection_To_1_16;
import me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_Latest;
import me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_v1_14_R1_To_v1_16_R3;
import me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2;
import me.Entity303.ServerSystem.Virtual.Cartography.VirtualCartography_Latest;
import me.Entity303.ServerSystem.Virtual.Cartography.VirtualCartography_v1_14_R1_To_v1_16_R3;
import me.Entity303.ServerSystem.Virtual.Grindstone.VirtualGrindstone_latest;
import me.Entity303.ServerSystem.Virtual.Grindstone.VirtualGrindstone_v1_14_R1_To_v1_16_R3;
import me.Entity303.ServerSystem.Virtual.Loom.VirtualLoom_Latest;
import me.Entity303.ServerSystem.Virtual.Loom.VirtualLoom_v1_14_R1_To_v1_16_R3;
import me.Entity303.ServerSystem.Virtual.Smithing.VirtualSmithing_Latest;
import me.Entity303.ServerSystem.Virtual.Smithing.VirtualSmithing_v1_16_R1_To_v1_16_R3;
import me.Entity303.ServerSystem.Virtual.StoneCutter.VirtualStoneCutter_Latest;
import me.Entity303.ServerSystem.Virtual.StoneCutter.VirtualStoneCutter_v1_14_R1_To_v1_16_R3;
import org.bukkit.Bukkit;

public class VersionManager {
    private final ss serverSystem;
    private String nmsVersion;
    private boolean v117 = false;
    private boolean v116 = false;
    private boolean v114 = false;
    private boolean v113 = false;
    private boolean v112 = false;
    private boolean v119 = false;
    private boolean v188 = false;
    private boolean terracotta = false;
    private boolean checkedOnce = false;
    private String version;

    public VersionManager(ss serverSystem) {
        this.serverSystem = serverSystem;
    }

    public boolean is188() {
        if (!this.checkedOnce) {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            this.v188 = version.equals("v1_8_R3");
            this.checkedOnce = true;
        }
        return this.v188;
    }

    public boolean isV119() {
        return this.v119;
    }

    public boolean isV112() {
        return this.v112;
    }

    public boolean isV113() {
        return this.v113;
    }

    public boolean isV114() {
        return this.v114;
    }

    public boolean isV116() {
        return this.v116;
    }

    public boolean isV117() {
        return this.v117;
    }

    public String getVersion() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].replace("_", ".");
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
        return version;
    }

    public boolean isTerracotta() {
        return this.terracotta;
    }

    public void registerVersionStuff() {
        this.nmsVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        String version = this.getVersion();

        this.serverSystem.log("ServerSystem is running on " + version + "!");

        if (version.contains("1.18")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_Latest(this.nmsVersion));
            this.v117 = true;
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Latest());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Latest(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_Newer(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Latest());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_Latest());
            this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_Latest());
            this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_latest());
            this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_Latest());
            this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_Latest());
            this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_Latest());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Latest(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Latest(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.17")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_Latest(this.nmsVersion));
            this.v117 = true;
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Latest());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Latest(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_Newer(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Latest());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_Latest());
            this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_Latest());
            this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_latest());
            this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_Latest());
            this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_Latest());
            this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_Latest());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Latest(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Latest(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.16.R3")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_1_16(this.nmsVersion));
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Old());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_Newer(this.serverSystem), this.serverSystem);

            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_v1_16_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.16.R2")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_1_16(this.nmsVersion));
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Old());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_Newer(this.serverSystem), this.serverSystem);

            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_v1_16_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.16")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_1_16(this.nmsVersion));
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Old());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_Newer(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_v1_16_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.15")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Old());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_Newer(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_v1_16_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.14")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Old());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_Newer(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_v1_14_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_v1_16_R1_To_v1_16_R3());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.13.R2")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Old());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_13_R2(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.13")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Old());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_13_R1(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.12")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_12_R1(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.11")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.v119 = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_11_R1(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.10")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.v119 = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_10_R1(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.9.R2")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.v119 = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Old(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_9_R1(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.9")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.v119 = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Old(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_9_R1(this.serverSystem), this.serverSystem);

            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull(this.serverSystem), null);
            }, 5L);
        } else if (version.contains("1.8")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Old(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_8_R3(this.serverSystem), this.serverSystem);

            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull(this.serverSystem), null);
            }, 5L);
        } else {
            this.serverSystem.warn("Unsupported version detected! Continue with your own risk! Support may not guaranteed!");
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_Latest(this.nmsVersion));
            this.v117 = true;
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() == null)
                this.serverSystem.getVersionStuff().setBukkitCommandWrap(new BukkitCommandWrap_Reflection_Latest());
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Latest(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_Newer(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Latest());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_Latest());
            this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_Latest());
            this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_latest());
            this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_Latest());
            this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_Latest());
            this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_Latest());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Latest(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Latest(this.serverSystem));
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> {
                this.serverSystem.getCommandManager().rc("skull", new COMMAND_skull_newer(this.serverSystem), null);
            }, 5L);
        }
    }

    public String getNMSVersion() {
        return this.nmsVersion;
    }
}
