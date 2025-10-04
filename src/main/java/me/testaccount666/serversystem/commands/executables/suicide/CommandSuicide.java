package me.testaccount666.serversystem.commands.executables.suicide;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "suicide")
public class CommandSuicide extends AbstractServerSystemCommand {

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Suicide.Use")) return;

        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        var player = commandSender.getPlayer();
        player.damage(Double.MAX_VALUE, DamageSource.builder(DamageType.GENERIC_KILL).build());
    }

    @Override
    public String getSyntaxPath(Command command) {
        throw new UnsupportedOperationException("Suicide command doesn't have an available syntax!");
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Suicide.Use", false);
    }
}
