package me.entity303.serversystem.utils.versions;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.entity303.serversystem.actionbar.IActionBar;
import me.entity303.serversystem.commands.executable.RecipeCommand;
import me.entity303.serversystem.listener.plotsquared.PlotListener;
import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.signedit.ISignEdit;
import me.entity303.serversystem.utils.versions.nbt.INBTViewer;
import me.entity303.serversystem.utils.versions.offlineplayer.data.ISaveData;
import me.entity303.serversystem.utils.versions.offlineplayer.entityplayer.IEntityPlayer;
import me.entity303.serversystem.utils.versions.offlineplayer.teleport.ITeleport;
import me.entity303.serversystem.vanish.packets.AbstractVanishPacket;
import me.entity303.serversystem.virtual.anvil.AbstractVirtualAnvil;
import me.entity303.serversystem.virtual.cartography.AbstractVirtualCartography;
import me.entity303.serversystem.virtual.grindstone.AbstractVirtualGrindstone;
import me.entity303.serversystem.virtual.loom.AbstractVirtualLoom;
import me.entity303.serversystem.virtual.smithing.AbstractVirtualSmithingTable;
import me.entity303.serversystem.virtual.stonecutter.AbstractVirtualStoneCutter;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class VersionStuff {
    private static final Pattern PATTERN = Pattern.compile("(@[A-Z0-9]*)");
    private static final Pattern CLASS_PATTERN = Pattern.compile("class ");
    private final ServerSystem _serverSystem;
    private AbstractVanishPacket _vanishPacket;
    private IActionBar _actionBar;
    private ISignEdit _signEdit;
    private AbstractVirtualAnvil _virtualAnvil;
    private AbstractVirtualLoom _virtualLoom;
    private AbstractVirtualGrindstone _virtualGrindstone;
    private AbstractVirtualStoneCutter _virtualStoneCutter;
    private AbstractVirtualCartography _virtualCartography;
    private AbstractVirtualSmithingTable _virtualSmithing;
    private ISaveData _saveData;
    private ITeleport _teleport;
    private IEntityPlayer _entityPlayer;
    private INBTViewer _nbtViewer;
    private Method _getHandleMethod;
    private MethodOrField _aMethod;
    private Field _playerConnectionField;
    private Field _channelField;
    private Field _aField;
    private Constructor _packetPlayOutUpdateTimeConstructor;
    private Field _aFieldStatus;
    private Field _bFieldStatus;
    private Constructor _packetPlayOutEntityStatusConstructor;
    private Method _getHandleWorldMethod;
    private Method _getEntityMethod;
    private Method _sendPacketMethod;
    private boolean _notified = false;

    public VersionStuff(ServerSystem serverSystem) {
        this._serverSystem = serverSystem;
    }

    public IEntityPlayer GetEntityPlayer() {
        return this._entityPlayer;
    }

    public void SetEntityPlayer(IEntityPlayer entityPlayer) {
        this._entityPlayer = entityPlayer;
    }

    public INBTViewer GetNbtViewer() {
        return this._nbtViewer;
    }

    public void SetNbtViewer(INBTViewer nbtViewer) {
        this._nbtViewer = nbtViewer;
    }

    public AbstractVanishPacket GetVanishPacket() {
        return this._vanishPacket;
    }

    public void SetVanishPacket(AbstractVanishPacket vanishPacket) {
        this._vanishPacket = vanishPacket;
    }

    public ISaveData GetSaveData() {
        return this._saveData;
    }

    public void SetSaveData(ISaveData saveData) {
        this._saveData = saveData;
    }

    public ITeleport GetTeleport() {
        return this._teleport;
    }

    public void SetTeleport(ITeleport teleport) {
        this._teleport = teleport;
    }

    public IActionBar GetActionBar() {
        return this._actionBar;
    }

    public void SetActionBar(IActionBar actionBar) {
        this._actionBar = actionBar;
    }

    public ISignEdit GetSignEdit() {
        return this._signEdit;
    }

    public void SetSignEdit(ISignEdit signEdit) {
        this._signEdit = signEdit;
    }

    public AbstractVirtualAnvil GetVirtualAnvil() {
        return this._virtualAnvil;
    }

    public void SetVirtualAnvil(AbstractVirtualAnvil virtualAnvil) {
        this._virtualAnvil = virtualAnvil;
    }

    public AbstractVirtualLoom GetVirtualLoom() {
        return this._virtualLoom;
    }

    public void SetVirtualLoom(AbstractVirtualLoom virtualLoom) {
        this._virtualLoom = virtualLoom;
    }

    public AbstractVirtualGrindstone GetVirtualGrindstone() {
        return this._virtualGrindstone;
    }

    public void SetVirtualGrindstone(AbstractVirtualGrindstone virtualGrindstone) {
        this._virtualGrindstone = virtualGrindstone;
    }

    public AbstractVirtualStoneCutter GetVirtualStoneCutter() {
        return this._virtualStoneCutter;
    }

    public void SetVirtualStoneCutter(AbstractVirtualStoneCutter virtualStoneCutter) {
        this._virtualStoneCutter = virtualStoneCutter;
    }

    public AbstractVirtualCartography GetVirtualCartography() {
        return this._virtualCartography;
    }

    public void SetVirtualCartography(AbstractVirtualCartography virtualCartography) {
        this._virtualCartography = virtualCartography;
    }

    public AbstractVirtualSmithingTable GetVirtualSmithing() {
        return this._virtualSmithing;
    }

    public void SetVirtualSmithing(AbstractVirtualSmithingTable virtualSmithing) {
        this._virtualSmithing = virtualSmithing;
    }

    public Method GetGetHandleMethod() {
        return this._getHandleMethod;
    }

    public void SetGetHandleMethod(Method getHandleMethod) {
        this._getHandleMethod = getHandleMethod;
    }

    public MethodOrField GetaMethod() {
        return this._aMethod;
    }

    public void SetaMethod(MethodOrField aMethod) {
        this._aMethod = aMethod;
    }

    public Field GetPlayerConnectionField() {
        return this._playerConnectionField;
    }

    public void SetPlayerConnectionField(Field playerConnectionField) {
        this._playerConnectionField = playerConnectionField;
    }

    public Field GetChannelField() {
        return this._channelField;
    }

    public void SetChannelField(Field channelField) {
        this._channelField = channelField;
    }

    public Field GetaField() {
        return this._aField;
    }

    public void SetaField(Field aField) {
        this._aField = aField;
    }

    public Constructor GetPacketPlayOutUpdateTimeConstructor() {
        return this._packetPlayOutUpdateTimeConstructor;
    }

    public void SetPacketPlayOutUpdateTimeConstructor(Constructor packetPlayOutUpdateTimeConstructor) {
        this._packetPlayOutUpdateTimeConstructor = packetPlayOutUpdateTimeConstructor;
    }

    public void Inject(Player player) {
        try {
            if (this._getHandleMethod == null)
                this.FetchGetHandleMethod();
            var entityPlayer = this._getHandleMethod.invoke(player);

            if (this._playerConnectionField == null) {
                this._playerConnectionField = Arrays.stream(entityPlayer.getClass().getDeclaredFields())
                                                    .filter(field -> field.getType().getName().toLowerCase(Locale.ROOT).contains("playerconnection"))
                                                    .findFirst()
                                                    .orElse(null);

                if (this._playerConnectionField == null) {
                    this._serverSystem.Error("Couldn't find PlayerConnection field! (Modded environment?)");
                    Arrays.stream(entityPlayer.getClass().getDeclaredFields())
                          .forEach(field -> this._serverSystem.Info(field.getType() + " -> " + field.getName()));
                    this._serverSystem.Warn("Please forward this to the developer of ServerSystem!");
                    return;
                }

                this._playerConnectionField.setAccessible(true);
            }

            var playerConnection = this._playerConnectionField.get(entityPlayer);

            if (this._aMethod == null) {
                var aMethod = Arrays.stream(playerConnection.getClass().getDeclaredMethods())
                                    .filter(field -> field.getReturnType().getName().toLowerCase(Locale.ROOT).contains("networkmanager"))
                                    .findFirst()
                                    .orElse(null);

                if (aMethod == null) {
                    var field = Arrays.stream(playerConnection.getClass().getDeclaredFields())
                                      .filter(field1 -> field1.getType().getName().toLowerCase(Locale.ROOT).contains("networkdispatcher") ||
                                                        field1.getType().getName().toLowerCase(Locale.ROOT).contains("networkmanager"))
                                      .findFirst()
                                      .orElse(null);

                    if (field == null) //Since 1.20.2 the field is located in SuperClass
                        field = Arrays.stream(playerConnection.getClass().getSuperclass().getDeclaredFields())
                                      .filter(field1 -> field1.getType().getName().toLowerCase(Locale.ROOT).contains("networkdispatcher") ||
                                                        field1.getType().getName().toLowerCase(Locale.ROOT).contains("networkmanager"))
                                      .findFirst()
                                      .orElse(null);

                    if (field == null) {
                        this._serverSystem.Error("Couldn't find NetworkManager field!");
                        return;
                    }

                    this._aMethod = new MethodOrField(field);
                } else
                    this._aMethod = new MethodOrField(aMethod);
            }

            var networkManager = this._aMethod.Invoke(playerConnection);

            if (this._channelField == null) {
                this._channelField = Arrays.stream(networkManager.getClass().getDeclaredFields())
                                           .filter(field -> field.getType().getName().toLowerCase(Locale.ROOT).contains("channel"))
                                           .findFirst()
                                           .orElse(null);
                this._channelField.setAccessible(true);
            }

            var channel = (Channel) this._channelField.get(networkManager);

            try {
                channel.pipeline().addBefore("packet_handler", "recBookChecker", new RecBookPacketHandler(player));

                channel.pipeline().addBefore("packet_handler", "serverSystemInject", new ServerSystemInjectPacketHandler(player, entityPlayer));

                if (VersionStuff.this._serverSystem.IsClientsideOp()) {

                    Class msg;

                    try {
                        msg = Class.forName("net.minecraft.server." + this._serverSystem.GetVersionManager().GetNMSVersion() + ".PacketPlayOutEntityStatus");
                    } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                        msg = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityStatus");
                    }

                    if (VersionStuff.this._packetPlayOutEntityStatusConstructor == null) {
                        VersionStuff.this._packetPlayOutEntityStatusConstructor = Arrays.stream(msg.getDeclaredConstructors())
                                                                                        .filter(constructor -> constructor.getParameterCount() == 2)
                                                                                        .filter(constructor -> constructor.getParameterTypes()[0].getName()
                                                                                                                                                .toLowerCase(
                                                                                                                                                        Locale.ROOT)
                                                                                                                                                .contains(
                                                                                                                                                        "entity"))
                                                                                        .filter(constructor -> constructor.getParameterTypes()[1].getName()
                                                                                                                                                .toLowerCase(
                                                                                                                                                        Locale.ROOT)
                                                                                                                                                .contains("byte"))
                                                                                        .findFirst()
                                                                                        .orElse(null);
                        if (VersionStuff.this._packetPlayOutEntityStatusConstructor == null) {
                            VersionStuff.this._serverSystem.Error("Could not find constructor for class 'PacketPlayOutEntityStatus'!");
                            return;
                        }

                        VersionStuff.this._packetPlayOutEntityStatusConstructor.setAccessible(true);
                    }

                    if (VersionStuff.this._getHandleWorldMethod == null)
                        try {
                            VersionStuff.this._getHandleWorldMethod =
                                    Class.forName("org.bukkit.craftbukkit." + VersionStuff.this._serverSystem.GetVersionManager().GetNMSVersion() + ".CraftWorld")
                                         .getDeclaredMethod("getHandle");
                        } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodException | NoSuchMethodError ignored) {
                            VersionStuff.this._serverSystem.Error("Could not find method 'getHandle' in class 'CraftWorld'!");
                            return;
                        }

                    if (VersionStuff.this._getEntityMethod == null)
                        try {
                            VersionStuff.this._getEntityMethod =
                                    Class.forName("net.minecraft.server." + VersionStuff.this._serverSystem.GetVersionManager().GetNMSVersion() + ".World")
                                         .getDeclaredMethod("getEntity", int.class);
                        } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodException | NoSuchMethodError exception) {
                            if (exception instanceof NoSuchMethodException || exception instanceof NoSuchMethodError)
                                VersionStuff.this._getEntityMethod = Arrays.stream(
                                                                                  Class.forName("net.minecraft.server." + VersionStuff.this._serverSystem.GetVersionManager().GetNMSVersion() + ".World")
                                                                                       .getDeclaredMethods())
                                                                           .filter(method -> method.getReturnType()
                                                                                                  .getName()
                                                                                                  .toLowerCase(Locale.ROOT)
                                                                                                  .contains("entity"))
                                                                           .filter(method -> method.getParameterCount() == 1)
                                                                           .filter(method -> method.getParameters()[0].getType()
                                                                                                                     .getName()
                                                                                                                     .toLowerCase(Locale.ROOT)
                                                                                                                     .contains("int"))
                                                                           .findFirst()
                                                                           .orElse(null);

                            if (VersionStuff.this._getEntityMethod == null)
                                try {
                                    VersionStuff.this._getEntityMethod = Arrays.stream(Class.forName("net.minecraft.world.level.World").getDeclaredMethods())
                                                                               .filter(method -> method.getReturnType()
                                                                                                      .getName()
                                                                                                      .toLowerCase(Locale.ROOT)
                                                                                                      .contains("entity"))
                                                                               .filter(method -> method.getParameterCount() == 1)
                                                                               .filter(method -> method.getParameters()[0].getType()
                                                                                                                         .getName()
                                                                                                                         .toLowerCase(Locale.ROOT)
                                                                                                                         .contains("int"))
                                                                               .findFirst()
                                                                               .orElse(null);
                                    if (VersionStuff.this._getEntityMethod == null) {
                                        VersionStuff.this._serverSystem.Error("Could not find method 'getEntity' in class 'World'!");
                                        return;
                                    }
                                } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodError ignored1) {
                                    VersionStuff.this._serverSystem.Error("Could not find method 'getEntity' in class 'World'!");
                                    return;
                                }
                        }

                    if (this._sendPacketMethod == null)
                        this._sendPacketMethod =
                                Arrays.stream(networkManager.getClass().getMethods()).filter(method -> method.getParameters().length == 1).filter(method -> {
                                    try {
                                        return method.getParameters()[0].getType()
                                                                        .getName()
                                                                        .equalsIgnoreCase(Class.forName("net.minecraft.network.protocol.Packet").getName());
                                    } catch (ClassNotFoundException | NoClassDefFoundError exception) {
                                        try {
                                            return method.getParameters()[0].getType()
                                                                            .getName()
                                                                            .equalsIgnoreCase(Class.forName("net.minecraft.server." +
                                                                                                            this._serverSystem.GetVersionManager()
                                                                                                                              .GetNMSVersion() + ".Packet")
                                                                                                   .getName());
                                        } catch (ClassNotFoundException exception1) {
                                            exception1.printStackTrace();
                                            return false;
                                        }

                                    }
                                }).findFirst().orElse(null);

                    var world = this._getHandleWorldMethod.invoke(player.getWorld());

                    var newStatus = VersionStuff.this._packetPlayOutEntityStatusConstructor.newInstance(entityPlayer, (byte) 28);

                    this._sendPacketMethod.invoke(networkManager, newStatus);
                }
            } catch (NoSuchElementException exception) {
                if (!this._notified)
                    this._serverSystem.Warn("Couldn't find packet_handler field, are you using Mohist, CatServer or Magma?");

                this._notified = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void FetchGetHandleMethod() throws ClassNotFoundException, NoSuchMethodException {
        var craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + this._serverSystem.GetVersionManager().GetNMSVersion() + ".entity.CraftPlayer");

        this._getHandleMethod = craftPlayerClass.getMethod("getHandle");

        this._getHandleMethod.setAccessible(true);
    }

    public void Uninject(Player player) {
        try {
            var entityPlayer = this._getHandleMethod.invoke(player);

            if (this._playerConnectionField == null) {
                this._playerConnectionField = entityPlayer.getClass().getDeclaredField("b");
                this._playerConnectionField.setAccessible(true);
            }

            var playerConnection = this._playerConnectionField.get(entityPlayer);

            if (this._aMethod == null)
                this._aMethod = new MethodOrField(playerConnection.getClass().getDeclaredMethod("a"));

            var networkManager = this._aMethod.Invoke(playerConnection);

            if (this._channelField == null) {
                this._channelField = networkManager.getClass().getDeclaredField("k");
                this._channelField.setAccessible(true);
            }

            var channel = (io.netty.channel.Channel) this._channelField.get(networkManager);

            channel.pipeline().remove("recBookChecker");

            channel.pipeline().remove("serverSystemInject");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static class MethodOrField {
        private Method _method = null;
        private Field _field = null;

        public MethodOrField(Method method) {
            this._method = method;
            this._method.setAccessible(true);
        }

        public MethodOrField(Field field) {
            this._field = field;
            this._field.setAccessible(true);
        }

        public Object Invoke(Object object) {
            if (this._method != null)
                try {
                    return this._method.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    exception.printStackTrace();
                    return null;
                }

            try {
                return this._field.get(object);
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    private class RecBookPacketHandler extends ChannelDuplexHandler {

        private final Player _player;

        public RecBookPacketHandler(Player player) {
            this._player = player;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (PATTERN.matcher(msg.getClass().toString()).replaceAll("")
                       .equalsIgnoreCase("class net.minecraft.server." + VersionStuff.this._serverSystem.GetVersionManager().GetNMSVersion() +
                                         ".PacketPlayInAutoRecipe") || PATTERN.matcher(msg.getClass().toString()).replaceAll("")
                                                                        .equalsIgnoreCase(
                                                                              "class net.minecraft.network.protocol.game.PacketPlayInAutoRecipe"))
                if (RecipeCommand.GetRecipeList().contains(_player))
                    return;
            super.channelRead(ctx, msg);
        }
    }

    private class ServerSystemInjectPacketHandler extends ChannelDuplexHandler {

        private final Player _player;
        private final Object _entityPlayer;

        public ServerSystemInjectPacketHandler(Player player, Object entityPlayer) {
            this._player = player;
            this._entityPlayer = entityPlayer;
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (VersionStuff.this._serverSystem.IsClientsideOp())
                if (msg.getClass().getName().contains("PacketPlayOutEntityStatus")) {
                    if (VersionStuff.this._aFieldStatus == null) {
                        VersionStuff.this._aFieldStatus = Arrays.stream(msg.getClass().getDeclaredFields())
                                                                .filter(field -> field.getType().getName().toLowerCase(Locale.ROOT).contains("int"))
                                                                .findFirst()
                                                                .orElse(null);
                        if (VersionStuff.this._aFieldStatus == null) {
                            VersionStuff.this._serverSystem.Error("Could not find field 'a' in class 'PacketPlayOutEntityStatus'!");
                            super.write(ctx, msg, promise);
                            return;
                        }

                        VersionStuff.this._aFieldStatus.setAccessible(true);
                    }

                    if (VersionStuff.this._bFieldStatus == null) {
                        VersionStuff.this._bFieldStatus = Arrays.stream(msg.getClass().getDeclaredFields())
                                                                .filter(field -> field.getType().getName().toLowerCase(Locale.ROOT).contains("byte"))
                                                                .findFirst()
                                                                .orElse(null);
                        if (VersionStuff.this._bFieldStatus == null) {
                            VersionStuff.this._serverSystem.Error("Could not find field 'b' in class 'PacketPlayOutEntityStatus'!");
                            super.write(ctx, msg, promise);
                            return;
                        }

                        VersionStuff.this._bFieldStatus.setAccessible(true);
                    }

                    var statusByte = VersionStuff.this._bFieldStatus.getByte(msg);
                    if (statusByte < 24) {
                        super.write(ctx, msg, promise);
                        return;
                    }

                    if (statusByte > 28) {
                        super.write(ctx, msg, promise);
                        return;
                    }

                    if (VersionStuff.this._packetPlayOutEntityStatusConstructor == null) {
                        VersionStuff.this._packetPlayOutEntityStatusConstructor = Arrays.stream(msg.getClass().getDeclaredConstructors())
                                                                                        .filter(constructor -> constructor.getParameterCount() == 2)
                                                                                        .filter(constructor -> constructor.getParameterTypes()[0].getName()
                                                                                                                                                .toLowerCase(
                                                                                                                                                        Locale.ROOT)
                                                                                                                                                .contains(
                                                                                                                                                        "entity"))
                                                                                        .filter(constructor -> constructor.getParameterTypes()[1].getName()
                                                                                                                                                .toLowerCase(
                                                                                                                                                        Locale.ROOT)
                                                                                                                                                .contains(
                                                                                                                                                        "byte"))
                                                                                        .findFirst()
                                                                                        .orElse(null);
                        if (VersionStuff.this._packetPlayOutEntityStatusConstructor == null) {
                            VersionStuff.this._serverSystem.Error("Could not find constructor for class 'PacketPlayOutEntityStatus'!");
                            super.write(ctx, msg, promise);
                            return;
                        }

                        VersionStuff.this._packetPlayOutEntityStatusConstructor.setAccessible(true);
                    }

                    if (VersionStuff.this._getHandleWorldMethod == null)
                        try {
                            VersionStuff.this._getHandleWorldMethod = Class.forName(
                                                                                  "org.bukkit.craftbukkit." + VersionStuff.this._serverSystem.GetVersionManager().GetNMSVersion() + ".CraftWorld")
                                                                           .getDeclaredMethod("getHandle");
                        } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodException | NoSuchMethodError ignored) {
                            VersionStuff.this._serverSystem.Error("Could not find method 'getHandle' in class 'CraftWorld'!");
                            super.write(ctx, msg, promise);
                            return;
                        }

                    var world = VersionStuff.this._getHandleWorldMethod.invoke(_player.getWorld());

                    if (VersionStuff.this._getEntityMethod == null)
                        try {
                            VersionStuff.this._getEntityMethod = Class.forName(
                                                                             "net.minecraft.server." + VersionStuff.this._serverSystem.GetVersionManager().GetNMSVersion() + ".World")
                                                                      .getDeclaredMethod("getEntity", int.class);
                        } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodException | NoSuchMethodError exception) {
                            if (exception instanceof NoSuchMethodException || exception instanceof NoSuchMethodError)
                                VersionStuff.this._getEntityMethod = Arrays.stream(Class.forName(
                                                                                               "net.minecraft.server." + VersionStuff.this._serverSystem.GetVersionManager().GetNMSVersion() + ".World")
                                                                                        .getDeclaredMethods())
                                                                           .filter(method -> method.getReturnType()
                                                                                                  .getName()
                                                                                                  .toLowerCase(Locale.ROOT)
                                                                                                  .contains("entity"))
                                                                           .filter(method -> method.getParameterCount() == 1)
                                                                           .filter(method -> method.getParameters()[0].getType()
                                                                                                                     .getName()
                                                                                                                     .toLowerCase(Locale.ROOT)
                                                                                                                     .contains("int"))
                                                                           .findFirst()
                                                                           .orElse(null);
                            if (VersionStuff.this._getEntityMethod == null)
                                try {
                                    VersionStuff.this._getEntityMethod =
                                            Arrays.stream(Class.forName("net.minecraft.world.level.World").getDeclaredMethods())
                                                  .filter(method -> method.getReturnType().getName().toLowerCase(Locale.ROOT).contains("entity"))
                                                  .filter(method -> method.getParameterCount() == 1)
                                                  .filter(method -> method.getParameters()[0].getType()
                                                                                             .getName()
                                                                                             .toLowerCase(Locale.ROOT)
                                                                                             .contains("int"))
                                                  .findFirst()
                                                  .orElse(null);
                                    if (VersionStuff.this._getEntityMethod == null) {
                                        VersionStuff.this._serverSystem.Error("Could not find method 'getEntity' in class 'World'!");
                                        super.write(ctx, msg, promise);
                                        return;
                                    }
                                } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodError ignored1) {
                                    VersionStuff.this._serverSystem.Error("Could not find method 'getEntity' in class 'World'!");
                                    super.write(ctx, msg, promise);
                                    return;
                                }
                        }

                    var newStatus = VersionStuff.this._packetPlayOutEntityStatusConstructor.newInstance(_entityPlayer, (byte) 28);
                    super.write(ctx, newStatus, promise);
                    return;
                }


            if (PlotListener.TIME_MAP.containsKey(_player))
                if (CLASS_PATTERN.matcher(PATTERN.matcher(msg.getClass().toString()).replaceAll("")).replaceFirst("")
                                 .equalsIgnoreCase("net.minecraft.server." + VersionStuff.this._serverSystem.GetVersionManager().GetNMSVersion() +
                                           ".PacketPlayOutUpdateTime")) {
                    if (VersionStuff.this._packetPlayOutUpdateTimeConstructor == null) {
                        VersionStuff.this._packetPlayOutUpdateTimeConstructor =
                                Class.forName("net.minecraft.network.protocol.game.PacketPlayOutUpdateTime")
                                     .getConstructor(long.class, long.class, boolean.class);
                        VersionStuff.this._packetPlayOutUpdateTimeConstructor.setAccessible(true);
                    }

                    if (VersionStuff.this._aField == null) {
                        VersionStuff.this._aField = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutUpdateTime").getDeclaredField("a");
                        VersionStuff.this._aField.setAccessible(true);
                    }

                    super.write(ctx, VersionStuff.this._packetPlayOutUpdateTimeConstructor.newInstance(VersionStuff.this._aField.get(msg),
                                                                                                       PlotListener.TIME_MAP.get(_player), false),
                                promise);
                    return;
                }
            super.write(ctx, msg, promise);
        }
    }
}
