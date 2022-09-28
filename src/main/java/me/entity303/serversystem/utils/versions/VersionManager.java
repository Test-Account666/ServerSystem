package me.entity303.serversystem.utils.versions;

import me.entity303.serversystem.actionbar.ActionBar_1_16;
import me.entity303.serversystem.actionbar.ActionBar_Latest;
import me.entity303.serversystem.actionbar.ActionBar_v1_12_R1_to_v1_15_R1;
import me.entity303.serversystem.actionbar.ActionBar_v1_8_R3_to_v1_11_R1;
import me.entity303.serversystem.commands.executable.SkullCommand;
import me.entity303.serversystem.commands.executable.SkullNewerCommand;
import me.entity303.serversystem.listener.vanish.*;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.signedit.SignEdit_Reflection_Latest;
import me.entity303.serversystem.signedit.SignEdit_Reflection_Old;
import me.entity303.serversystem.signedit.SignEdit_Reflection_to_v1_17_R1;
import me.entity303.serversystem.utils.versions.nbt.*;
import me.entity303.serversystem.utils.versions.offlineplayer.data.SaveData_Latest;
import me.entity303.serversystem.utils.versions.offlineplayer.data.SaveData_Old;
import me.entity303.serversystem.utils.versions.offlineplayer.entityplayer.EntityPlayer_Latest;
import me.entity303.serversystem.utils.versions.offlineplayer.entityplayer.EntityPlayer_Old;
import me.entity303.serversystem.utils.versions.offlineplayer.teleport.Teleport_Latest;
import me.entity303.serversystem.utils.versions.offlineplayer.teleport.Teleport_v1_16_R1_to_v1_17_R1;
import me.entity303.serversystem.utils.versions.offlineplayer.teleport.Teleport_v1_8_R3;
import me.entity303.serversystem.utils.versions.offlineplayer.teleport.Teleport_v1_9_R1_to_v1_15_R1;
import me.entity303.serversystem.vanish.packets.VanishPacket_Reflection_Latest;
import me.entity303.serversystem.vanish.packets.VanishPacket_Reflection_Old;
import me.entity303.serversystem.vanish.packets.VanishPacket_Reflection_To_1_16;
import me.entity303.serversystem.virtual.anvil.VirtualAnvil_Latest;
import me.entity303.serversystem.virtual.anvil.VirtualAnvil_v1_14_R1_To_v1_16_R3;
import me.entity303.serversystem.virtual.anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2;
import me.entity303.serversystem.virtual.cartography.VirtualCartography_Latest;
import me.entity303.serversystem.virtual.cartography.VirtualCartography_v1_14_R1_To_v1_16_R3;
import me.entity303.serversystem.virtual.grindstone.VirtualGrindstone_latest;
import me.entity303.serversystem.virtual.grindstone.VirtualGrindstone_v1_14_R1_To_v1_16_R3;
import me.entity303.serversystem.virtual.loom.VirtualLoom_Latest;
import me.entity303.serversystem.virtual.loom.VirtualLoom_v1_14_R1_To_v1_16_R3;
import me.entity303.serversystem.virtual.smithing.VirtualSmithing_Latest;
import me.entity303.serversystem.virtual.smithing.VirtualSmithing_v1_16_R1_To_v1_16_R3;
import me.entity303.serversystem.virtual.stonecutter.VirtualStoneCutter_Latest;
import me.entity303.serversystem.virtual.stonecutter.VirtualStoneCutter_v1_14_R1_To_v1_16_R3;
import org.bukkit.Bukkit;

public class VersionManager {
    private final ServerSystem serverSystem;
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

    public VersionManager(ServerSystem serverSystem) {
        this.serverSystem = serverSystem;
    }

    public void registerVersionStuff() {
        this.nmsVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        String version = this.getVersion();

        this.serverSystem.log("ServerSystem is running on " + version + "!");

        try {
            this.serverSystem.getVersionStuff().setGetHandleMethod(Class.forName("org.bukkit.craftbukkit." + this.nmsVersion + ".entity.CraftPlayer").getDeclaredMethod("getHandle"));
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (version.contains("1.19")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_Latest(this.nmsVersion));
            this.v117 = true;
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
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
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_Latest(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_Latest());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.18")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_Latest(this.nmsVersion));
            this.v117 = true;
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
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
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_Latest(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_Latest());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.17")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_Latest(this.nmsVersion));
            this.v117 = true;
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Latest(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_Newer(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_to_v1_17_R1());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_Latest());
            this.serverSystem.getVersionStuff().setVirtualCartography(new VirtualCartography_Latest());
            this.serverSystem.getVersionStuff().setVirtualGrindstone(new VirtualGrindstone_latest());
            this.serverSystem.getVersionStuff().setVirtualLoom(new VirtualLoom_Latest());
            this.serverSystem.getVersionStuff().setVirtualStoneCutter(new VirtualStoneCutter_Latest());
            this.serverSystem.getVersionStuff().setVirtualSmithing(new VirtualSmithing_Latest());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Latest(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Latest(this.serverSystem));
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_16_R1_to_v1_17_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_Latest());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.16.R3")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_1_16(this.nmsVersion));
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
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
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_16_R1_to_v1_17_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_Latest());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.16.R2")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_1_16(this.nmsVersion));
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
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
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_16_R1_to_v1_17_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_Latest());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.16")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_1_16(this.nmsVersion));
            this.v116 = true;
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
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
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_16_R1_to_v1_17_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_Latest());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.15")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
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
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_9_R1_to_v1_15_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_Latest());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.14")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v114 = true;
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
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
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_9_R1_to_v1_15_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_Latest());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.13.R2")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_13_R2(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.entity303.serversystem.virtual.anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_9_R1_to_v1_15_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_v1_13_R1());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.13")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v113 = true;
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_13_R1(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_9_R1_to_v1_15_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_v1_13_R1());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.12")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_12_R1_to_v1_15_R1(this.nmsVersion));
            this.v112 = true;
            this.v119 = true;
            this.terracotta = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_12_R1(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.entity303.serversystem.virtual.anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_9_R1_to_v1_15_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_v1_12_R1());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.11")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.v119 = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_11_R1(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.entity303.serversystem.virtual.anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_9_R1_to_v1_15_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_v1_11_R1());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.10")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.v119 = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_To_1_16(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_10_R1(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.entity303.serversystem.virtual.anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_9_R1_to_v1_15_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_v1_10_R1());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.9.R2")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.v119 = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Old(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_9_R2(this.serverSystem), this.serverSystem);
            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.entity303.serversystem.virtual.anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_9_R1_to_v1_15_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_v1_9_R2());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.9")) {
            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.v119 = true;
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Old(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_9_R1(this.serverSystem), this.serverSystem);

            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.entity303.serversystem.virtual.anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_9_R1_to_v1_15_R1(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_v1_9_R1());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullCommand(this.serverSystem), null), 5L);
        } else if (version.contains("1.8")) {

            if (!this.is188())
                this.serverSystem.error("You are using an outdated version of 1.8! Please use at least 1.8.4 (better 1.8.8), ServerSystem will not work (correctly) otherwise!");

            try {
                Class.forName("org.github.paperspigot.event.ServerExceptionEvent");
                this.serverSystem.error("Running on Paper 1.8! ServerSystem will NOT work, please use Spigot or a newer version of Paper!");
            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                this.serverSystem.warn("You're using a very old version of minecraft, I suggest you to update!");
            }

            this.serverSystem.getVersionStuff().setActionBar(new ActionBar_v1_8_R3_to_v1_11_R1(this.nmsVersion));
            this.serverSystem.getVersionStuff().setVanishPacket(new VanishPacket_Reflection_Old(this.serverSystem));
            Bukkit.getPluginManager().registerEvents(new InteractListener_v1_8_R3(this.serverSystem), this.serverSystem);

            this.serverSystem.getVersionStuff().setSignEdit(new SignEdit_Reflection_Old());
            this.serverSystem.getVersionStuff().setVirtualAnvil(new me.entity303.serversystem.virtual.anvil.VirtualAnvil_v1_8_R3_To_v1_13_R2());
            this.serverSystem.getVersionStuff().setSaveData(new SaveData_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setEntityPlayer(new EntityPlayer_Old(this.serverSystem));
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_v1_8_R3());
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_v1_8_R3());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullCommand(this.serverSystem), null), 5L);
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
            this.serverSystem.getVersionStuff().setTeleport(new Teleport_Latest(this.serverSystem));
            this.serverSystem.getVersionStuff().setNbtViewer(new NBTViewer_Latest());
            Bukkit.getScheduler().runTaskLater(this.serverSystem, () -> this.serverSystem.getCommandManager().rc("skull", new SkullNewerCommand(this.serverSystem), null), 5L);
        }
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

    public String getNMSVersion() {
        return this.nmsVersion;
    }
}
