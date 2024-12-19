package me.entity303.serversystem.virtual.cartography;

import me.entity303.serversystem.virtual.AbstractVirtual;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.inventory.ContainerCartography;
import org.bukkit.entity.Player;

public abstract class AbstractVirtualCartography extends AbstractVirtual {

    public AbstractVirtualCartography() {
        super(ContainerCartography.class);

        try {
            this._inventoryTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"Cartography\"}");
        } catch (NoSuchMethodError exception) {
            this._inventoryTitle = Component.literal("Cartography");
        }
    }

    public abstract void OpenCartography(Player player);
}
