package me.testaccount666.serversystem.commands.executables.inventorysee;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@ServerSystemCommand(name = "inventorysee")
public class CommandInventorySee extends AbstractServerSystemCommand {
    protected final Map<Player, Inventory> inventoryCache = new HashMap<>();

    @Override
    public void execute(User sender, Command command, String label, String... arguments) {
        if (!command.getName().equalsIgnoreCase("inventorysee")) return;

        processInventorySee(sender, label, arguments);
    }

    private void processInventorySee(User sender, String label, String... arguments) {
        if (!checkBasePermission(sender, "InventorySee.Use", label)) return;

        if (sender instanceof ConsoleUser) {
            sendGeneralMessage(sender, "NotPlayer", null, label, null);
            return;
        }

        if (arguments.length < 1) {
            sendGeneralMessage(sender, "InvalidArguments", null, label, null);
            return;
        }

        var targetUserOptional = getTargetUser(sender, arguments[0]);
        if (targetUserOptional.isEmpty()) return;

        var targetUser = targetUserOptional.get();
        if (targetUser == sender) {
            sendCommandMessage(sender, "InventorySee.CannotSeeSelf", targetUser.getPlayer().getName(), label, null);
            return;
        }

        var ownerPlayer = targetUser.getPlayer();
        var viewerPlayer = sender.getPlayer();

        var customInventory = inventoryCache.computeIfAbsent(ownerPlayer, this::createAndInitializeInventory);
        viewerPlayer.openInventory(customInventory);
    }

    private Inventory createAndInitializeInventory(Player ownerPlayer) {
        var newInventory = Bukkit.createInventory(ownerPlayer, 54);
        refreshInventoryContents(ownerPlayer, newInventory);
        return newInventory;
    }

    protected void refreshInventoryContents(Player owner, Inventory displayInventory) {
        copyPlayerInventory(owner, displayInventory);
        addSectionDecorators(displayInventory);
    }

    private void copyPlayerInventory(Player owner, Inventory displayInventory) {
        // main inventory and hotbar (slots 0-39)
        for (var slot = 0; slot < 40; slot++) displayInventory.setItem(slot, owner.getInventory().getItem(slot));

        // off-hand and cursor
        displayInventory.setItem(40, owner.getInventory().getItemInOffHand());
        displayInventory.setItem(41, owner.getOpenInventory().getCursor());
    }

    private void addSectionDecorators(Inventory displayInventory) {
        placeFilledMarkers(displayInventory, Material.ARMOR_STAND, "Armor", 45, 49);
        placeFilledMarkers(displayInventory, Material.APPLE, "Off-Hand", 49, 50);
        placeFilledMarkers(displayInventory, Material.WHITE_WOOL, "Cursor", 50, 51);
        placeFilledMarkers(displayInventory, Material.DROPPER, "Drop item", 51, 54);
    }

    private void placeFilledMarkers(Inventory inventory, Material material, String displayName, int startSlot, int endSlot) {
        var markerItem = new ItemStack(material);
        var itemMeta = markerItem.getItemMeta();
        itemMeta.displayName(Component.text(displayName)
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(TextColor.color(255, 0, 0))
                .asComponent());
        markerItem.setItemMeta(itemMeta);

        for (var slot = startSlot; slot < endSlot; slot++) inventory.setItem(slot, markerItem);
    }

    protected void applyChangesToOwner(Player owner, Inventory displayInventory) {
        for (var slot = 0; slot < 40; slot++) owner.getInventory().setItem(slot, displayInventory.getItem(slot));

        owner.getInventory().setItemInOffHand(displayInventory.getItem(40));
        owner.setItemOnCursor(displayInventory.getItem(41));
        owner.updateInventory();
        dropModifiedItems(owner, displayInventory, 42, 45);
    }

    private void dropModifiedItems(Player owner, Inventory displayInventory, int fromSlot, int toSlot) {
        for (var slot = fromSlot; slot < toSlot; slot++) {
            var item = displayInventory.getItem(slot);
            if (item == null) continue;

            owner.getWorld()
                    .dropItem(owner.getEyeLocation().add(0, -0.33, 0), item.clone())
                    .setVelocity(owner.getLocation().getDirection().multiply(0.35));
            item.setAmount(0);
        }
    }
}