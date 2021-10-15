package me.Entity303.ServerSystem.Utils.versions;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.Entity303.ServerSystem.ActionBar.ActionBar;
import me.Entity303.ServerSystem.Commands.executable.COMMAND_recipe;
import me.Entity303.ServerSystem.Listener.PlotSquared.PlotListener;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.SignEdit.SignEdit;
import me.Entity303.ServerSystem.Utils.versions.commands.BukkitCommandWrap;
import me.Entity303.ServerSystem.Utils.versions.offlineplayer.data.SaveData;
import me.Entity303.ServerSystem.Utils.versions.offlineplayer.entityplayer.EntityPlayer;
import me.Entity303.ServerSystem.Vanish.Packets.VanishPacket;
import me.Entity303.ServerSystem.Virtual.Anvil.VirtualAnvil;
import me.Entity303.ServerSystem.Virtual.Cartography.VirtualCartography;
import me.Entity303.ServerSystem.Virtual.Grindstone.VirtualGrindstone;
import me.Entity303.ServerSystem.Virtual.Loom.VirtualLoom;
import me.Entity303.ServerSystem.Virtual.Smithing.VirtualSmithing;
import me.Entity303.ServerSystem.Virtual.StoneCutter.VirtualStoneCutter;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VersionStuff {
    private final ss serverSystem;
    private BukkitCommandWrap bukkitCommandWrap = null;
    private VanishPacket vanishPacket;
    private ActionBar actionBar;
    private SignEdit signEdit;
    private VirtualAnvil virtualAnvil;
    private VirtualLoom virtualLoom;
    private VirtualGrindstone virtualGrindstone;
    private VirtualStoneCutter virtualStoneCutter;
    private VirtualCartography virtualCartography;
    private VirtualSmithing virtualSmithing;
    private SaveData saveData;
    private EntityPlayer entityPlayer;
    private Method getHandleMethod;
    private Method aMethod;
    private Field playerConnectionField;
    private Field channelField;
    private Field aField;
    private Constructor packetPlayOutUpdateTimeConstructor;

    public VersionStuff(ss serverSystem) {
        this.serverSystem = serverSystem;
    }

    public BukkitCommandWrap getBukkitCommandWrap() {
        return this.bukkitCommandWrap;
    }

    public void setBukkitCommandWrap(BukkitCommandWrap bukkitCommandWrap) {
        this.bukkitCommandWrap = bukkitCommandWrap;
    }

    public EntityPlayer getEntityPlayer() {
        return this.entityPlayer;
    }

    public void setEntityPlayer(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }

    public VanishPacket getVanishPacket() {
        return this.vanishPacket;
    }

    public void setVanishPacket(VanishPacket vanishPacket) {
        this.vanishPacket = vanishPacket;
    }

    public SaveData getSaveData() {
        return this.saveData;
    }

    public void setSaveData(SaveData saveData) {
        this.saveData = saveData;
    }

    public ActionBar getActionBar() {
        return this.actionBar;
    }

    public void setActionBar(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    public SignEdit getSignEdit() {
        return this.signEdit;
    }

    public void setSignEdit(SignEdit signEdit) {
        this.signEdit = signEdit;
    }

    public VirtualAnvil getVirtualAnvil() {
        return this.virtualAnvil;
    }

    public void setVirtualAnvil(VirtualAnvil virtualAnvil) {
        this.virtualAnvil = virtualAnvil;
    }

    public VirtualLoom getVirtualLoom() {
        return this.virtualLoom;
    }

    public void setVirtualLoom(VirtualLoom virtualLoom) {
        this.virtualLoom = virtualLoom;
    }

    public VirtualGrindstone getVirtualGrindstone() {
        return this.virtualGrindstone;
    }

    public void setVirtualGrindstone(VirtualGrindstone virtualGrindstone) {
        this.virtualGrindstone = virtualGrindstone;
    }

    public VirtualStoneCutter getVirtualStoneCutter() {
        return this.virtualStoneCutter;
    }

    public void setVirtualStoneCutter(VirtualStoneCutter virtualStoneCutter) {
        this.virtualStoneCutter = virtualStoneCutter;
    }

    public VirtualCartography getVirtualCartography() {
        return this.virtualCartography;
    }

    public void setVirtualCartography(VirtualCartography virtualCartography) {
        this.virtualCartography = virtualCartography;
    }

    public VirtualSmithing getVirtualSmithing() {
        return this.virtualSmithing;
    }

    public void setVirtualSmithing(VirtualSmithing virtualSmithing) {
        this.virtualSmithing = virtualSmithing;
    }

    public Method getGetHandleMethod() {
        return this.getHandleMethod;
    }

    public void setGetHandleMethod(Method getHandleMethod) {
        this.getHandleMethod = getHandleMethod;
    }

    public Method getaMethod() {
        return this.aMethod;
    }

    public void setaMethod(Method aMethod) {
        this.aMethod = aMethod;
    }

    public Field getPlayerConnectionField() {
        return this.playerConnectionField;
    }

    public void setPlayerConnectionField(Field playerConnectionField) {
        this.playerConnectionField = playerConnectionField;
    }

    public Field getChannelField() {
        return this.channelField;
    }

    public void setChannelField(Field channelField) {
        this.channelField = channelField;
    }

    public Field getaField() {
        return this.aField;
    }

    public void setaField(Field aField) {
        this.aField = aField;
    }

    public Constructor getPacketPlayOutUpdateTimeConstructor() {
        return this.packetPlayOutUpdateTimeConstructor;
    }

    public void setPacketPlayOutUpdateTimeConstructor(Constructor packetPlayOutUpdateTimeConstructor) {
        this.packetPlayOutUpdateTimeConstructor = packetPlayOutUpdateTimeConstructor;
    }

    public void inject(Player player) {
        try {
            if (this.getHandleMethod == null) {
                this.getHandleMethod = Class.forName("org.bukkit.craftbukkit." + this.serverSystem.getVersionManager().getNMSVersion() + ".entity.CraftPlayer").getMethod("getHandle");
                this.getHandleMethod.setAccessible(true);
            }
            Object entityPlayer = this.getHandleMethod.invoke(player);

            if (this.playerConnectionField == null) {
                this.playerConnectionField = !this.serverSystem.getVersionManager().isV117() ? entityPlayer.getClass().getDeclaredField("playerConnection") : entityPlayer.getClass().getDeclaredField("b");
                this.playerConnectionField.setAccessible(true);
            }

            Object playerConnection = this.playerConnectionField.get(entityPlayer);

            if (this.aMethod == null) {
                this.aMethod = playerConnection.getClass().getDeclaredMethod("a");
                this.aMethod.setAccessible(true);
            }

            Object networkManager = this.aMethod.invoke(playerConnection);

            if (this.channelField == null) {
                this.channelField = !this.serverSystem.getVersionManager().isV117() ? networkManager.getClass().getDeclaredField("channel") : networkManager.getClass().getDeclaredField("k");
                this.channelField.setAccessible(true);
            }

            Channel channel = (Channel) this.channelField.get(networkManager);

            if (this.serverSystem.getVersionManager().isV112())
                channel.pipeline().addAfter("decoder", "recBookChecker", new ChannelDuplexHandler() {

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg.getClass().toString().replaceAll("([@][A-Z0-9]*)", "").equalsIgnoreCase("class net.minecraft.server." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".PacketPlayInAutoRecipe") || msg.getClass().toString().replaceAll("([@][A-Z0-9]*)", "").equalsIgnoreCase("class net.minecraft.network.protocol.game.PacketPlayInAutoRecipe"))
                            if (COMMAND_recipe.getRecipeList().contains(player)) return;
                        super.channelRead(ctx, msg);
                    }
                });

            channel.pipeline().addBefore("packet_handler", "plotTimeFixer", new ChannelDuplexHandler() {

                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    if (PlotListener.TIME_MAP.containsKey(player))
                        if (msg.getClass().toString().replaceAll("([@][A-Z0-9]*)", "").replaceFirst("class ", "").equalsIgnoreCase("net.minecraft.server." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".PacketPlayOutUpdateTime")) {
                            if (VersionStuff.this.packetPlayOutUpdateTimeConstructor == null) {
                                if (!VersionStuff.this.serverSystem.getVersionManager().isV117())
                                    VersionStuff.this.packetPlayOutUpdateTimeConstructor = Class.forName("net.minecraft.server." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".PacketPlayOutUpdateTime").getConstructor(long.class, long.class, boolean.class);
                                else
                                    VersionStuff.this.packetPlayOutUpdateTimeConstructor = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutUpdateTime").getConstructor(long.class, long.class, boolean.class);
                                VersionStuff.this.packetPlayOutUpdateTimeConstructor.setAccessible(true);
                            }

                            if (VersionStuff.this.aField == null) {
                                if (!VersionStuff.this.serverSystem.getVersionManager().isV117())
                                    VersionStuff.this.aField = Class.forName("net.minecraft.server." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".PacketPlayOutUpdateTime").getDeclaredField("a");
                                else
                                    VersionStuff.this.aField = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutUpdateTime").getDeclaredField("a");
                                VersionStuff.this.aField.setAccessible(true);
                            }

                            super.write(ctx, VersionStuff.this.packetPlayOutUpdateTimeConstructor.newInstance(VersionStuff.this.aField.get(msg), PlotListener.TIME_MAP.get(player), false), promise);
                            return;
                        }
                    super.write(ctx, msg, promise);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uninject(Player player) {
        try {
            Object entityPlayer = this.getHandleMethod.invoke(player);

            if (this.playerConnectionField == null) {
                this.playerConnectionField = !this.serverSystem.getVersionManager().isV117() ? entityPlayer.getClass().getDeclaredField("playerConnection") : entityPlayer.getClass().getDeclaredField("b");
                this.playerConnectionField.setAccessible(true);
            }

            Object playerConnection = this.playerConnectionField.get(entityPlayer);

            if (this.aMethod == null) {
                this.aMethod = playerConnection.getClass().getDeclaredMethod("a");
                this.aMethod.setAccessible(true);
            }

            Object networkManager = this.aMethod.invoke(playerConnection);

            if (this.channelField == null) {
                this.channelField = !this.serverSystem.getVersionManager().isV117() ? networkManager.getClass().getDeclaredField("channel") : networkManager.getClass().getDeclaredField("k");
                this.channelField.setAccessible(true);
            }

            Channel channel = (Channel) this.channelField.get(networkManager);

            if (this.serverSystem.getVersionManager().isV112())
                channel.pipeline().remove("recBookChecker");

            channel.pipeline().remove("plotTimeFixer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
