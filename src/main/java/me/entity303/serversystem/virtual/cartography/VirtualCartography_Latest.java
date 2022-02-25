package me.entity303.serversystem.virtual.cartography;

import me.entity303.serversystem.virtual.Virtual;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerCartography;
import net.minecraft.world.inventory.Containers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class VirtualCartography_Latest extends VirtualCartography {

    @Override
    public void openCartography(Player player) {
        if (Virtual.getInventoryMethod == null) {
            Virtual.getInventoryMethod = Arrays.stream(EntityHuman.class.getDeclaredMethods()).
                    filter(method -> method.getReturnType().getName().equalsIgnoreCase(PlayerInventory.class.getName())).
                    filter(method -> method.getParameters().length <= 0)
                    .findFirst().orElse(null);


            if (Virtual.getInventoryMethod == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'getInventory' in class " + EntityHuman.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            Virtual.getInventoryMethod.setAccessible(true);

        }

        if (Virtual.sendPacketMethod == null) {
            Virtual.sendPacketMethod = Arrays.stream(PlayerConnection.class.getMethods()).
                    filter(method -> method.getParameters().length == 1).
                    filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Packet.class.getName())).
                    findFirst().orElse(null);

            if (Virtual.sendPacketMethod == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'sendPacket' in class " + PlayerConnection.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            Virtual.sendPacketMethod.setAccessible(true);
        }

        if (Virtual.containerField == null) {
            Virtual.containerField = Arrays.stream(EntityHuman.class.getDeclaredFields())
                    .filter(field -> field.getType().getName().equalsIgnoreCase(Container.class.getName()))
                    .findFirst().orElse(null);

            if (Virtual.containerField == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find field 'container' in class " + EntityHuman.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            Virtual.containerField.setAccessible(true);
        }

        if (Virtual.initMenuMethod == null) {
            Virtual.initMenuMethod = Arrays.stream(EntityPlayer.class.getDeclaredMethods()).
                    filter(method -> method.getParameters().length == 1).
                    filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Container.class.getName())).
                    findFirst().orElse(null);

            if (Virtual.initMenuMethod == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'initMenu' in class " + EntityPlayer.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            Virtual.initMenuMethod.setAccessible(true);
        }

        EntityPlayer human = null;
        try {
            human = (EntityPlayer) Class.forName("org.bukkit.craftbukkit." + this.getVersion() + ".entity.CraftPlayer").getDeclaredMethod("getHandle").invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        int id = human.nextContainerCounter();
        ContainerCartography container = null;
        try {
            container = new ContainerCartography(id, (PlayerInventory) Virtual.getInventoryMethod.invoke(human), (ContainerAccess) this.getWrapper(player));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        container.setTitle(IChatBaseComponent.ChatSerializer.a("{\"text\":\"Cartography\"}"));

        container.checkReachable = false;

        try {
            Virtual.sendPacketMethod.invoke(human.b, new PacketPlayOutOpenWindow(id, Containers.r, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Cartography\"}")));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        try {
            Virtual.containerField.set(human, container);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        try {
            Virtual.initMenuMethod.invoke(human, container);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (Virtual.getBukkitViewMethod == null) {
            try {
                Virtual.getBukkitViewMethod = Container.class.getDeclaredMethod("getBukkitView");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }
            Virtual.getBukkitViewMethod.setAccessible(true);
        }

        try {
            player.openInventory(((InventoryView) Virtual.getBukkitViewMethod.invoke(container)));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
