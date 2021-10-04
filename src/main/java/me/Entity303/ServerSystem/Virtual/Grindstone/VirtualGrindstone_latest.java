package me.Entity303.ServerSystem.Virtual.Grindstone;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerGrindstone;
import net.minecraft.world.inventory.Containers;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class VirtualGrindstone_latest extends VirtualGrindstone {

    @Override
    public void openGrind(Player player) {
        EntityPlayer human = null;
        try {
            human = (EntityPlayer) Class.forName("org.bukkit.craftbukkit." + this.getVersion() + ".entity.CraftPlayer").getDeclaredMethod("getHandle").invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        int id = human.nextContainerCounter();
        ContainerGrindstone container = new ContainerGrindstone(id, human.getInventory(), (ContainerAccess) this.getWrapper(player));
        container.checkReachable = false;

        human.b.sendPacket(new PacketPlayOutOpenWindow(id, Containers.o, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Grindstone\"}")));
        human.bV = container;
        human.initMenu(container);
    }
}
