package me.entity303.serversystem.virtual.loom;

import me.entity303.serversystem.virtual.AbstractVirtual;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerLoom;
import net.minecraft.world.inventory.Containers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;

public class VirtualLoom_Latest extends AbstractVirtualLoom {

    @Override
    public void OpenLoom(Player player) {
        if (AbstractVirtual.GET_INVENTORY_METHOD == null) {
            AbstractVirtual.GET_INVENTORY_METHOD = Arrays.stream(EntityHuman.class.getDeclaredMethods())
                                                         .filter(method -> method.getReturnType().getName().equalsIgnoreCase(PlayerInventory.class.getName()))
                                                         .filter(method -> method.getParameters().length == 0)
                                                         .findFirst()
                                                         .orElse(null);


            if (AbstractVirtual.GET_INVENTORY_METHOD == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'getInventory' in class " + EntityHuman.class.getName());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }

            AbstractVirtual.GET_INVENTORY_METHOD.setAccessible(true);

        }

        if (AbstractVirtual.SEND_PACKET_METHOD == null) {
            AbstractVirtual.SEND_PACKET_METHOD = Arrays.stream(PlayerConnection.class.getMethods())
                                                       .filter(method -> method.getParameters().length == 1)
                                                       .filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Packet.class.getName()))
                                                       .findFirst()
                                                       .orElse(null);

            if (AbstractVirtual.SEND_PACKET_METHOD == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'sendPacket' in class " + PlayerConnection.class.getName());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }

            AbstractVirtual.SEND_PACKET_METHOD.setAccessible(true);
        }

        if (AbstractVirtual.CONTAINER_FIELD == null) {
            AbstractVirtual.CONTAINER_FIELD = Arrays.stream(EntityHuman.class.getDeclaredFields())
                                                    .filter(field -> field.getType().getName().equalsIgnoreCase(Container.class.getName()))
                                                    .findFirst()
                                                    .orElse(null);

            if (AbstractVirtual.CONTAINER_FIELD == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find field 'container' in class " + EntityHuman.class.getName());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }

            AbstractVirtual.CONTAINER_FIELD.setAccessible(true);
        }

        if (AbstractVirtual.INIT_MENU_METHOD == null) {
            AbstractVirtual.INIT_MENU_METHOD = Arrays.stream(EntityPlayer.class.getDeclaredMethods())
                                                     .filter(method -> method.getParameters().length == 1)
                                                     .filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Container.class.getName()))
                                                     .findFirst()
                                                     .orElse(null);

            if (AbstractVirtual.INIT_MENU_METHOD == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'initMenu' in class " + EntityPlayer.class.getName());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }

            AbstractVirtual.INIT_MENU_METHOD.setAccessible(true);
        }

        EntityPlayer human;
        try {
            human = (EntityPlayer) Class.forName("org.bukkit.craftbukkit." + this.GetVersion() + ".entity.CraftPlayer")
                                        .getDeclaredMethod("getHandle")
                                        .invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return;
        }

        var containerId = human.nextContainerCounter();
        ContainerLoom container;
        try {
            container = new ContainerLoom(containerId, (PlayerInventory) AbstractVirtual.GET_INVENTORY_METHOD.invoke(human), (ContainerAccess) this.GetWrapper(player));
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return;
        }

        container.setTitle(IChatBaseComponent.ChatSerializer.a("{\"text\":\"Loom\"}"));

        container.checkReachable = false;

        try {
            if (PLAYER_CONNECTION_FIELD == null) {
                PLAYER_CONNECTION_FIELD = Arrays.stream(human.getClass().getDeclaredFields())
                                                .filter(field -> field.getType().getName().toLowerCase(Locale.ROOT).contains("playerconnection"))
                                                .findFirst()
                                                .orElse(null);

                if (PLAYER_CONNECTION_FIELD == null) {
                    this._plugin.Error("Couldn't find PlayerConnection field! (Modded environment?)");
                    Arrays.stream(human.getClass().getDeclaredFields()).forEach(field -> this._plugin.Info(field.getType() + " -> " + field.getName()));
                    this._plugin.Warn("Please forward this to the developer of ServerSystem!");
                    return;
                }

                PLAYER_CONNECTION_FIELD.setAccessible(true);
            }

            var playerConnection = (PlayerConnection) PLAYER_CONNECTION_FIELD.get(human);

            AbstractVirtual.SEND_PACKET_METHOD.invoke(playerConnection,
                                                      new PacketPlayOutOpenWindow(containerId, Containers.r, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Loom\"}")));
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            return;
        }

        try {
            AbstractVirtual.CONTAINER_FIELD.set(human, container);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
            return;
        }

        try {
            AbstractVirtual.INIT_MENU_METHOD.invoke(human, container);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }

        if (AbstractVirtual.GET_BUKKIT_VIEW_METHOD == null) {
            try {
                AbstractVirtual.GET_BUKKIT_VIEW_METHOD = Container.class.getDeclaredMethod("getBukkitView");
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
                return;
            }
            AbstractVirtual.GET_BUKKIT_VIEW_METHOD.setAccessible(true);
        }

        try {
            player.openInventory(((InventoryView) AbstractVirtual.GET_BUKKIT_VIEW_METHOD.invoke(container)));
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }
}
