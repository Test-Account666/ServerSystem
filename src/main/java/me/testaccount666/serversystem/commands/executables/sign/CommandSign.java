package me.testaccount666.serversystem.commands.executables.sign;

import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

import static me.testaccount666.serversystem.utils.DurationParser.parseDate;
import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "sign", variants = "unsign")
public class CommandSign extends AbstractServerSystemCommand {
    protected final NamespacedKey signKey = new NamespacedKey(ServerSystem.Instance, "sign");

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, getCommandPermission(command))) return;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var player = commandSender.getPlayer();
        var itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.isEmpty()) {
            command("Sign.NoItemInHand", commandSender).build();
            return;
        }
        var meta = itemInHand.getItemMeta();
        if (meta == null) {
            command("Sign.NoItemMeta", commandSender).build();
            return;
        }

        if (command.getName().equalsIgnoreCase("sign")) executeSign(itemInHand, meta, commandSender, command, label, arguments);
        else executeUnsign(itemInHand, meta, commandSender);
    }

    private void executeUnsign(ItemStack itemInHand, ItemMeta itemMeta, User commandSender) {
        var dataContainer = itemMeta.getPersistentDataContainer();
        if (!dataContainer.has(signKey)) {
            command("Unsign.NotSigned", commandSender).build();
            return;
        }

        var lore = itemMeta.lore();
        if (lore == null) lore = new ArrayList<>();

        lore.removeIf(loreComponent -> {
            var strippedLine = ChatColor.stripColor(ComponentColor.componentToString(loreComponent));
            var strippedLore = ChatColor.stripColor(dataContainer.get(signKey, PersistentDataType.STRING));

            return Arrays.stream(strippedLore.split("\n")).anyMatch(strippedLine::equalsIgnoreCase);
        });
        itemMeta.lore(lore);

        dataContainer.remove(signKey);

        itemInHand.setItemMeta(itemMeta);
        command("Unsign.Success", commandSender).build();
    }

    private void executeSign(ItemStack itemInHand, ItemMeta itemMeta, User commandSender, Command command, String label, String... arguments) {
        var dataContainer = itemMeta.getPersistentDataContainer();
        if (dataContainer.has(signKey)) {
            command("Sign.AlreadySigned", commandSender).build();
            return;
        }

        var lore = itemMeta.lore();
        if (lore == null) lore = new ArrayList<>();

        var message = String.join(" ", arguments).trim();
        if (message.isEmpty()) {
            general("InvalidArguments", commandSender).syntax(getSyntaxPath(command)).label(label).build();
            return;
        }

        var parsedDate = parseDate(System.currentTimeMillis(), commandSender);

        var loreMessage = command("Sign.Format", commandSender)
                .prefix(false).send(false)
                .postModifier(msg -> msg.replace("<MESSAGE>", message)
                        .replace("<DATE>", parsedDate)).build();

        if (loreMessage.isEmpty()) {
            general("ErrorOccurred", commandSender).label(label).build();
            return;
        }

        dataContainer.set(signKey, PersistentDataType.STRING, loreMessage.get());

        for (var line : loreMessage.get().split("\n")) {
            var lineComponent = ComponentColor.translateToComponent(line);
            lore.add(lineComponent);
        }

        itemMeta.lore(lore);
        itemInHand.setItemMeta(itemMeta);

        command("Sign.Success", commandSender).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return command.getName().equalsIgnoreCase("sign")? "Sign" : "Unsign";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, getCommandPermission(command), false);
    }

    private String getCommandPermission(Command command) {
        return command.getName().equalsIgnoreCase("sign")? "Sign.Use" : "Unsign.Use";
    }
}
