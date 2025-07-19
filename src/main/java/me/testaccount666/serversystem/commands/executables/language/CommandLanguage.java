package me.testaccount666.serversystem.commands.executables.language;

import me.testaccount666.serversystem.commands.ServerSystemCommand;
import me.testaccount666.serversystem.commands.executables.AbstractServerSystemCommand;
import me.testaccount666.serversystem.managers.PermissionManager;
import me.testaccount666.serversystem.userdata.ConsoleUser;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Path;

import static me.testaccount666.serversystem.utils.MessageBuilder.command;
import static me.testaccount666.serversystem.utils.MessageBuilder.general;

@ServerSystemCommand(name = "language", tabCompleter = TabCompleterLanguages.class)
public class CommandLanguage extends AbstractServerSystemCommand {
    private final File _messagesDirectory = Path.of("plugins", "ServerSystem", "messages").toFile();

    @Override
    public void execute(User commandSender, Command command, String label, String... arguments) {
        if (!checkBasePermission(commandSender, "Language.Use")) return;
        if (commandSender instanceof ConsoleUser) {
            general("NotPlayer", commandSender).build();
            return;
        }

        if (arguments.length == 0) {
            general("InvalidArguments", commandSender).syntaxPath(getSyntaxPath(command)).label(label).build();
            return;
        }

        var language = arguments[0];
        var languages = _messagesDirectory.listFiles();
        if (languages == null) languages = new File[0];
        var selectedLanguage = "English";

        for (var file : languages) {
            var fileName = file.getName();
            if (!fileName.toLowerCase().startsWith(language.toLowerCase())) continue;

            selectedLanguage = fileName;
        }

        commandSender.setPlayerLanguage(selectedLanguage.toLowerCase());
        commandSender.setUsesDefaultLanguage(false);
        commandSender.save();
        var chars = selectedLanguage.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        selectedLanguage = new String(chars);

        var finalSelectedLanguage = selectedLanguage;
        command("Language.Changed", commandSender)
                .postModifier(message -> message.replace("<LANGUAGE>", finalSelectedLanguage)).build();
    }

    @Override
    public String getSyntaxPath(Command command) {
        return "Language";
    }

    @Override
    public boolean hasCommandAccess(Player player, Command command) {
        return PermissionManager.hasCommandPermission(player, "Language.Use", false);
    }
}
