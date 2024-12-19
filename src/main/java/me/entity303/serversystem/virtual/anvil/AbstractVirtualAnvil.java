package me.entity303.serversystem.virtual.anvil;

import me.entity303.serversystem.virtual.AbstractVirtual;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.inventory.ContainerAnvil;
import org.bukkit.entity.Player;

public abstract class AbstractVirtualAnvil extends AbstractVirtual {

    public AbstractVirtualAnvil() {
        super(ContainerAnvil.class);

        try {
            this._inventoryTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"Repairing\"}");
        } catch (NoSuchMethodError exception) {
            this._inventoryTitle = Component.literal("Repairing");
        }
    }

    public abstract void OpenAnvil(Player player);
}
