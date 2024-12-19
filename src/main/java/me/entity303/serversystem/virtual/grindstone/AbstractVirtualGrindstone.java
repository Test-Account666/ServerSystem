package me.entity303.serversystem.virtual.grindstone;

import me.entity303.serversystem.virtual.AbstractVirtual;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.inventory.ContainerGrindstone;
import org.bukkit.entity.Player;

public abstract class AbstractVirtualGrindstone extends AbstractVirtual {

    public AbstractVirtualGrindstone() {
        super(ContainerGrindstone.class);

        try {
            this._inventoryTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"Grindstone\"}");
        } catch (NoSuchMethodError exception) {
            this._inventoryTitle = Component.literal("Grindstone");
        }
    }

    public abstract void OpenGrindstone(Player player);
}
