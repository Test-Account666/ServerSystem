package me.entity303.serversystem.virtual.loom;

import me.entity303.serversystem.virtual.AbstractVirtual;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.inventory.ContainerLoom;
import org.bukkit.entity.Player;

public abstract class AbstractVirtualLoom extends AbstractVirtual {

    public AbstractVirtualLoom() {
        super(ContainerLoom.class);

        try {
            this._inventoryTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"Loom\"}");
        } catch (NoSuchMethodError exception) {
            this._inventoryTitle = Component.literal("Loom");
        }
    }

    public abstract void OpenLoom(Player player);
}
