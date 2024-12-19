package me.entity303.serversystem.virtual.stonecutter;

import me.entity303.serversystem.virtual.AbstractVirtual;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.inventory.ContainerStonecutter;
import org.bukkit.entity.Player;

public abstract class AbstractVirtualStoneCutter extends AbstractVirtual {

    public AbstractVirtualStoneCutter() {
        super(ContainerStonecutter.class);

        try {
            this._inventoryTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"Stonecutter\"}");
        } catch (NoSuchMethodError exception) {
            this._inventoryTitle = Component.literal("Stonecutter");
        }
    }

    public abstract void OpenStoneCutter(Player player);
}
