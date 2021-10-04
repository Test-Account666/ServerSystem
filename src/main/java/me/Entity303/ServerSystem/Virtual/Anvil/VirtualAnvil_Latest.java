package me.Entity303.ServerSystem.Virtual.Anvil;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.Containers;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class VirtualAnvil_Latest extends VirtualAnvil {

    @Override
    public void openAnvil(Player player) {
        EntityPlayer human = null;
        try {
            human = (EntityPlayer) Class.forName("org.bukkit.craftbukkit." + this.getVersion() + ".entity.CraftPlayer").getDeclaredMethod("getHandle").invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        int id = human.nextContainerCounter();
        ContainerAnvil container = new ContainerAnvil(id, human.getInventory(), (ContainerAccess) this.getWrapper(player));
        container.checkReachable = false;

        human.b.sendPacket(new PacketPlayOutOpenWindow(id, Containers.h, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Repairing\"}")));
        human.bV = container;
        human.initMenu(container);
    }
}
