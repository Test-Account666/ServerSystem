package me.testaccount666.serversystem.commands.executables.serversystem;

import me.testaccount666.migration.plugins.MigratorRegistry;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.updates.UpdateManager;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "serversystem", tabCompleter = TabCompleterServerSystem.class)
public class CommandServerSystem extends AbstractServerSystemCommand {
    @Override
    public String getSyntaxPath(Command command) {
        return "ServerSystem";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "ServerSystem.Use", false);
    }

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "ServerSystem.Use")) return;
        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        var newArguments = new String[arguments.length - 1];
        System.arraycopy(arguments, 1, newArguments, 0, arguments.length - 1);

        var subCommand = arguments[0].toLowerCase();
        switch (subCommand) {
            case "version" -> version(commandSender, label);
            case "reload" -> reload(commandSender);
            case "migrate" -> migrate(commandSender, label, newArguments);
            default -> general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
        }
    }

    public void version(User commandSender, String label) {
        if (!checkBasePermission(commandSender, "ServerSystem.Version")) return;

        command("ServerSystem.Version.Checking", commandSender).build();

        var updateManager = ServerSystem.Instance.getRegistry().getService(UpdateManager.class);
        updateManager.getUpdateChecker().getLatestVersion().thenAccept(latestVersion -> {
            command("ServerSystem.Version.Success", commandSender)
                    .postModifier(message -> applyVersion(message, latestVersion.getVersion()))
                    .prefix(false).build();
        }).exceptionally(throwable -> {
            general("ErrorOccurred", commandSender).label(label).build();
            command("ServerSystem.Version.Success", commandSender)
                    .postModifier(message -> applyVersion(message, null))
                    .prefix(false).build();
            return null;
        });
    }

    private String applyVersion(String message, String latestVersion) {
        if (latestVersion == null) latestVersion = "?";

        var currentVersion = ServerSystem.Instance.getDescription().getVersion();
        var serverVersion = ServerSystem.getServerVersion().getVersion();

        return message.replace("<LATEST_VERSION>", latestVersion)
                .replace("<CURRENT_VERSION>", currentVersion)
                .replace("<SERVER_VERSION>", serverVersion);
    }

    public void reload(User commandSender) {
        if (!checkBasePermission(commandSender, "ServerSystem.Reload")) return;

        var serverSystem = ServerSystem.Instance;
        Bukkit.getScheduler().cancelTasks(serverSystem);
        serverSystem.onDisable();
        serverSystem.onEnable();

        if (!ServerSystem.Instance.isEnabled()) {
            general("ErrorOccurred", commandSender).build();
            return;
        }

        command("ServerSystem.Reload.Success", commandSender).build();
    }

    public void migrate(User commandSender, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "ServerSystem.Migrate")) return;

        if (arguments.length <= 1) {
            general("InvalidArguments", commandSender).label(label)
                    .syntaxPath(getSyntaxPath(null)).build();
            return;
        }

        var migratorRegistry = ServerSystem.Instance.getRegistry().getService(MigratorRegistry.class);
        var migratorName = arguments[1];

        var migrator = migratorRegistry.getMigrator(migratorName);
        if (migrator.isEmpty()) {
            command("ServerSystem.Migrate.NotFound", commandSender)
                    .postModifier(message -> message.replace("<MIGRATOR>", migratorName)).build();
            return;
        }

        var migrationType = arguments[0].toLowerCase();
        switch (migrationType) {
            case "to" -> {
                migrator.get().migrateTo();
                migrationType = "To";
            }
            case "from" -> {
                migrator.get().migrateFrom();
                migrationType = "From";
            }
            default -> {
                general("InvalidArguments", commandSender).label(label)
                        .syntaxPath(getSyntaxPath(null)).build();
                return;
            }
        }

        command("ServerSystem.Migrate.Success.${migrationType}", commandSender)
                .postModifier(message -> message.replace("<MIGRATOR>", migratorName)).build();
    }
}
