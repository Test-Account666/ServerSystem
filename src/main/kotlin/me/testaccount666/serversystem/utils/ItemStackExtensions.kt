package me.testaccount666.serversystem.utils

import org.bukkit.inventory.ItemStack

class ItemStackExtensions {
    companion object {
        fun ItemStack?.isAir() = this == null || this.type.isAir
    }
}