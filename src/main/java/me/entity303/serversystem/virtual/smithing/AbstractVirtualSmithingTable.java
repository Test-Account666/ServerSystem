package me.entity303.serversystem.virtual.smithing;

import me.entity303.serversystem.virtual.AbstractVirtual;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.inventory.ContainerSmithing;
import org.bukkit.entity.Player;

public abstract class AbstractVirtualSmithingTable extends AbstractVirtual {

    public AbstractVirtualSmithingTable() {
        super(ContainerSmithing.class);

        try {
            this._inventoryTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"Smithing\"}");
        } catch (NoSuchMethodError exception) {
            this._inventoryTitle = Component.literal("Smithing");
        }
    }

    public abstract void OpenSmithingTable(Player player);
}
