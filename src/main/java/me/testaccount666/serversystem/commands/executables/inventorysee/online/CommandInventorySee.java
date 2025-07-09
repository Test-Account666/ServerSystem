package me.testaccount666.serversystem.commands.executables.inventorysee.online;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.inventorysee.offline.CommandOfflineInventorySee;
import me.testaccount666.serversystem.commands.executables.inventorysee.utils.InventorySeeUtils;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

@ServerSystemCommand(name = "inventorysee", variants = "offlineinventorysee", tabCompleter = TabCompleterInventorySee.class)
public class CommandInventorySee extends AbstractServerSystemCommand {
    public final Map<Player, Inventory> inventoryCache = new HashMap<>();
    public final CommandOfflineInventorySee _offlineInventorySee;

    public CommandInventorySee() {
        _offlineInventorySee = new CommandOfflineInventorySee(this);
    }

    @Override
    public void execute(User sender, Command command, String label, String... arguments) {
        if (command.getName().toLowerCase().startsWith("offline")) {
            _offlineInventorySee.execute(sender, command, label, arguments);
            return;
        }

        if (!command.getName().equalsIgnoreCase("inventorysee")) return;
        processInventorySee(sender, label, arguments);
    }

    public void processInventorySee(User sender, String label, String... arguments) {
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
        if (targetUserOptional.isEmpty()) {
            sendMissingPlayerMessage(sender, label, arguments[0]);
            return;
        }

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
        var newInventory = Bukkit.createInventory(ownerPlayer, 54, "${ownerPlayer.getName()}'s Inventory");
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

    public void addSectionDecorators(Inventory displayInventory) {
        InventorySeeUtils.addSectionDecorators(displayInventory, false);
    }

    public void placeFilledMarkers(Inventory inventory, Material material, String displayName, int startSlot, int endSlot) {
        InventorySeeUtils.placeFilledMarkers(inventory, material, displayName, startSlot, endSlot);
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

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        if (command.getName().toLowerCase().startsWith("offline")) return _offlineInventorySee.hasCommandAccess(player, command);

        return PermissionManager.hasCommandPermission(player, "InventorySee.Use", false);
    }
}
