package me.entity303.serversystem.utils.versions;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.entity303.serversystem.actionbar.ActionBar;
import me.entity303.serversystem.commands.executable.RecipeCommand;
import me.entity303.serversystem.listener.plotsquared.PlotListener;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.signedit.SignEdit;
import me.entity303.serversystem.utils.versions.nbt.NBTViewer;
import me.entity303.serversystem.utils.versions.offlineplayer.data.SaveData;
import me.entity303.serversystem.utils.versions.offlineplayer.entityplayer.EntityPlayer;
import me.entity303.serversystem.utils.versions.offlineplayer.teleport.Teleport;
import me.entity303.serversystem.vanish.packets.VanishPacket;
import me.entity303.serversystem.virtual.anvil.VirtualAnvil;
import me.entity303.serversystem.virtual.cartography.VirtualCartography;
import me.entity303.serversystem.virtual.grindstone.VirtualGrindstone;
import me.entity303.serversystem.virtual.loom.VirtualLoom;
import me.entity303.serversystem.virtual.smithing.VirtualSmithing;
import me.entity303.serversystem.virtual.stonecutter.VirtualStoneCutter;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

public class VersionStuff {
    private final ServerSystem serverSystem;
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
    private Teleport teleport;
    private EntityPlayer entityPlayer;
    private NBTViewer nbtViewer;
    private Method getHandleMethod;
    private Method aMethod;
    private Field playerConnectionField;
    private Field channelField;
    private Field aField;
    private Constructor packetPlayOutUpdateTimeConstructor;
    private Field aFieldStatus;
    private Field bFieldStatus;
    private Constructor packetPlayOutEntityStatusConstructor;
    private Method getHandleWorldMethod;
    private Method getEntityMethod;
    private Method sendPacketMethod;

    public VersionStuff(ServerSystem serverSystem) {
        this.serverSystem = serverSystem;
    }

    public EntityPlayer getEntityPlayer() {
        return this.entityPlayer;
    }

    public void setEntityPlayer(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }

    public NBTViewer getNbtViewer() {
        return this.nbtViewer;
    }

    public void setNbtViewer(NBTViewer nbtViewer) {
        this.nbtViewer = nbtViewer;
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

    public Teleport getTeleport() {
        return this.teleport;
    }

    public void setTeleport(Teleport teleport) {
        this.teleport = teleport;
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
                this.playerConnectionField = Arrays.stream(entityPlayer.getClass().getDeclaredFields()).filter(field -> field.getType().getName().toLowerCase(Locale.ROOT).contains("playerconnection")).findFirst().orElse(null);
                this.playerConnectionField.setAccessible(true);
            }

            Object playerConnection = this.playerConnectionField.get(entityPlayer);

            if (this.aMethod == null) {
                this.aMethod = Arrays.stream(playerConnection.getClass().getDeclaredMethods()).filter(field -> field.getReturnType().getName().toLowerCase(Locale.ROOT).contains("networkmanager")).findFirst().orElse(null);
                this.aMethod.setAccessible(true);
            }

            Object networkManager = this.aMethod.invoke(playerConnection);

            if (this.channelField == null) {
                this.channelField = Arrays.stream(networkManager.getClass().getDeclaredFields()).filter(field -> field.getType().getName().toLowerCase(Locale.ROOT).contains("channel")).findFirst().orElse(null);
                this.channelField.setAccessible(true);
            }

            Channel channel = (Channel) this.channelField.get(networkManager);

            if (this.serverSystem.getVersionManager().isV112())
                channel.pipeline().addAfter("decoder", "recBookChecker", new ChannelDuplexHandler() {

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg.getClass().toString().replaceAll("([@][A-Z0-9]*)", "").equalsIgnoreCase("class net.minecraft.server." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".PacketPlayInAutoRecipe") || msg.getClass().toString().replaceAll("([@][A-Z0-9]*)", "").equalsIgnoreCase("class net.minecraft.network.protocol.game.PacketPlayInAutoRecipe"))
                            if (RecipeCommand.getRecipeList().contains(player)) return;
                        super.channelRead(ctx, msg);
                    }
                });

            channel.pipeline().addBefore("packet_handler", "serverSystemInject", new ChannelDuplexHandler() {

                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    if (VersionStuff.this.serverSystem.isClientsideOp())
                        if (msg.getClass().getName().contains("PacketPlayOutEntityStatus")) {
                            if (VersionStuff.this.aFieldStatus == null) {
                                VersionStuff.this.aFieldStatus = Arrays.stream(msg.getClass().getDeclaredFields()).filter(field -> field.getType().getName().toLowerCase(Locale.ROOT).contains("int")).findFirst().orElse(null);
                                if (VersionStuff.this.aFieldStatus == null) {
                                    VersionStuff.this.serverSystem.error("Could not find field 'a' in class 'PacketPlayOutEntityStatus'!");
                                    super.write(ctx, msg, promise);
                                    return;
                                }

                                VersionStuff.this.aFieldStatus.setAccessible(true);
                            }

                            if (VersionStuff.this.bFieldStatus == null) {
                                VersionStuff.this.bFieldStatus = Arrays.stream(msg.getClass().getDeclaredFields()).filter(field -> field.getType().getName().toLowerCase(Locale.ROOT).contains("byte")).findFirst().orElse(null);
                                if (VersionStuff.this.bFieldStatus == null) {
                                    VersionStuff.this.serverSystem.error("Could not find field 'b' in class 'PacketPlayOutEntityStatus'!");
                                    super.write(ctx, msg, promise);
                                    return;
                                }

                                VersionStuff.this.bFieldStatus.setAccessible(true);
                            }

                            byte b = VersionStuff.this.bFieldStatus.getByte(msg);
                            if (b < 24) {
                                super.write(ctx, msg, promise);
                                return;
                            }

                            if (b > 28) {
                                super.write(ctx, msg, promise);
                                return;
                            }

                            if (VersionStuff.this.packetPlayOutEntityStatusConstructor == null) {
                                VersionStuff.this.packetPlayOutEntityStatusConstructor = Arrays.stream(msg.getClass().getDeclaredConstructors()).filter(constructor -> constructor.getParameterCount() == 2).filter(constructor -> constructor.getParameterTypes()[0].getName().toLowerCase(Locale.ROOT).contains("entity")).filter(constructor -> constructor.getParameterTypes()[1].getName().toLowerCase(Locale.ROOT).contains("byte")).findFirst().orElse(null);
                                if (VersionStuff.this.packetPlayOutEntityStatusConstructor == null) {
                                    VersionStuff.this.serverSystem.error("Could not find constructor for class 'PacketPlayOutEntityStatus'!");
                                    super.write(ctx, msg, promise);
                                    return;
                                }

                                VersionStuff.this.packetPlayOutEntityStatusConstructor.setAccessible(true);
                            }

                            if (VersionStuff.this.getHandleWorldMethod == null) try {
                                VersionStuff.this.getHandleWorldMethod = Class.forName("org.bukkit.craftbukkit." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".CraftWorld").getDeclaredMethod("getHandle");
                            } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodException | NoSuchMethodError ignored) {
                                VersionStuff.this.serverSystem.error("Could not find method 'getHandle' in class 'CraftWorld'!");
                                super.write(ctx, msg, promise);
                                return;
                            }

                            Object world = VersionStuff.this.getHandleWorldMethod.invoke(player.getWorld());

                            if (VersionStuff.this.getEntityMethod == null) try {
                                VersionStuff.this.getEntityMethod = Class.forName("net.minecraft.server." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".World").getDeclaredMethod("getEntity", int.class);
                            } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodException | NoSuchMethodError e) {
                                if (e instanceof NoSuchMethodException || e instanceof NoSuchMethodError)
                                    VersionStuff.this.getEntityMethod = Arrays.stream(Class.forName("net.minecraft.server." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".World").getDeclaredMethods()).
                                            filter(method -> method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("entity")).
                                            filter(method -> method.getParameterCount() == 1).
                                            filter(method -> method.getParameters()[0].getType().getName().toLowerCase(Locale.ROOT).contains("int")).
                                            findFirst().orElse(null);
                                if (VersionStuff.this.getEntityMethod == null)
                                    try {
                                        VersionStuff.this.getEntityMethod = Arrays.stream(Class.forName("net.minecraft.world.level.World").getDeclaredMethods()).
                                                filter(method -> method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("entity")).
                                                filter(method -> method.getParameterCount() == 1).
                                                filter(method -> method.getParameters()[0].getType().getName().toLowerCase(Locale.ROOT).contains("int")).
                                                findFirst().orElse(null);
                                        if (VersionStuff.this.getEntityMethod == null) {
                                            VersionStuff.this.serverSystem.error("Could not find method 'getEntity' in class 'World'!");
                                            super.write(ctx, msg, promise);
                                            return;
                                        }
                                    } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodError ignored1) {
                                        VersionStuff.this.serverSystem.error("Could not find method 'getEntity' in class 'World'!");
                                        super.write(ctx, msg, promise);
                                        return;
                                    }
                            }

                            Object newStatus = VersionStuff.this.packetPlayOutEntityStatusConstructor.newInstance(entityPlayer, (byte) 28);
                            super.write(ctx, newStatus, promise);
                            return;
                        }


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

            if (VersionStuff.this.serverSystem.isClientsideOp()) {

                Class msg = null;

                try {
                    msg = Class.forName("net.minecraft.server." + this.serverSystem.getVersionManager().getNMSVersion() + ".PacketPlayOutEntityStatus");
                } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                    msg = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityStatus");
                }

                if (VersionStuff.this.packetPlayOutEntityStatusConstructor == null) {
                    VersionStuff.this.packetPlayOutEntityStatusConstructor = Arrays.stream(msg.getDeclaredConstructors()).filter(constructor -> constructor.getParameterCount() == 2).filter(constructor -> constructor.getParameterTypes()[0].getName().toLowerCase(Locale.ROOT).contains("entity")).filter(constructor -> constructor.getParameterTypes()[1].getName().toLowerCase(Locale.ROOT).contains("byte")).findFirst().orElse(null);
                    if (VersionStuff.this.packetPlayOutEntityStatusConstructor == null) {
                        VersionStuff.this.serverSystem.error("Could not find constructor for class 'PacketPlayOutEntityStatus'!");
                        return;
                    }

                    VersionStuff.this.packetPlayOutEntityStatusConstructor.setAccessible(true);
                }

                if (VersionStuff.this.getHandleWorldMethod == null) try {
                    VersionStuff.this.getHandleWorldMethod = Class.forName("org.bukkit.craftbukkit." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".CraftWorld").getDeclaredMethod("getHandle");
                } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodException | NoSuchMethodError ignored) {
                    VersionStuff.this.serverSystem.error("Could not find method 'getHandle' in class 'CraftWorld'!");
                    return;
                }

                if (VersionStuff.this.getEntityMethod == null) try {
                    VersionStuff.this.getEntityMethod = Class.forName("net.minecraft.server." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".World").getDeclaredMethod("getEntity", int.class);
                } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodException | NoSuchMethodError e) {
                    if (e instanceof NoSuchMethodException || e instanceof NoSuchMethodError)
                        VersionStuff.this.getEntityMethod = Arrays.stream(Class.forName("net.minecraft.server." + VersionStuff.this.serverSystem.getVersionManager().getNMSVersion() + ".World").getDeclaredMethods()).
                                filter(method -> method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("entity")).
                                filter(method -> method.getParameterCount() == 1).
                                filter(method -> method.getParameters()[0].getType().getName().toLowerCase(Locale.ROOT).contains("int")).
                                findFirst().orElse(null);

                    if (VersionStuff.this.getEntityMethod == null)
                        try {
                            VersionStuff.this.getEntityMethod = Arrays.stream(Class.forName("net.minecraft.world.level.World").getDeclaredMethods()).
                                    filter(method -> method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("entity")).
                                    filter(method -> method.getParameterCount() == 1).
                                    filter(method -> method.getParameters()[0].getType().getName().toLowerCase(Locale.ROOT).contains("int")).
                                    findFirst().orElse(null);
                            if (VersionStuff.this.getEntityMethod == null) {
                                VersionStuff.this.serverSystem.error("Could not find method 'getEntity' in class 'World'!");
                                return;
                            }
                        } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodError ignored1) {
                            VersionStuff.this.serverSystem.error("Could not find method 'getEntity' in class 'World'!");
                            return;
                        }
                }

                if (this.sendPacketMethod == null)
                    this.sendPacketMethod = Arrays.stream(networkManager.getClass().getMethods()).
                            filter(method -> method.getParameters().length == 1).
                            filter(method -> {
                                try {
                                    return method.getParameters()[0].getType().getName().equalsIgnoreCase(Class.forName("net.minecraft.network.protocol.Packet").getName());
                                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                                    try {
                                        return method.getParameters()[0].getType().getName().equalsIgnoreCase(Class.forName("net.minecraft.server." + this.serverSystem.getVersionManager().getNMSVersion() + ".Packet").getName());
                                    } catch (ClassNotFoundException ex) {
                                        ex.printStackTrace();
                                        return false;
                                    }

                                }
                            }).
                            findFirst().orElse(null);

                Object world = this.getHandleWorldMethod.invoke(player.getWorld());

                Object entity = (VersionStuff.this.getEntityMethod.invoke(world, player.getEntityId()));

                Object newStatus = VersionStuff.this.packetPlayOutEntityStatusConstructor.newInstance(entity, (byte) 28);

                this.sendPacketMethod.invoke(networkManager, newStatus);
            }
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

            channel.pipeline().remove("serverSystemInject");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
