package me.entity303.serversystem.listener.command;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EssentialsCommandListener implements Listener {
    private static final Pattern SLASH_PATTERN = Pattern.compile("/");
    private final Essentials _essentials;
    private final ServerSystem _plugin;
    private final Map<String, String> _essentialsCommandMap = new HashMap<>();

    public EssentialsCommandListener(Essentials essentials, ServerSystem plugin) {
        this._essentials = essentials;
        this._plugin = plugin;
    }

    @EventHandler
    public void OnNewEssentialsCommand(PlayerCommandPreprocessEvent event) {
        var com = SLASH_PATTERN.matcher(event.getMessage().split(" ")[0]).replaceFirst("").toLowerCase();
        if (this._essentialsCommandMap.containsKey(com)) {
            event.setCancelled(true);
            List<String> arguments = new ArrayList<>();

            if (event.getMessage().split(" ").length >= 2)
                for (var index = 1; index < event.getMessage().split(" ").length; index++)
                    arguments.add(index - 1, event.getMessage().split(" ")[index]);

            var commandName = this._essentialsCommandMap.get(com);
            var essentialsCommand = this._plugin.getServer().getPluginCommand("essentials:" + commandName);

            IEssentialsCommand command;

            try {
                command = (IEssentialsCommand) Essentials.class.getClassLoader()
                                                           .loadClass("com.earth2me.essentials.commands.Command" + essentialsCommand.getName())
                                                           .newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException instantiationException) {
                if (!instantiationException.getMessage().isEmpty())
                    event.getPlayer().sendMessage(instantiationException.getMessage());
                instantiationException.printStackTrace();
                return;
            }

            command.setEssentials(this._essentials);
            command.setEssentialsModule(null);

            try {
                command.run(this._essentials.getServer(), this._essentials.getUser(event.getPlayer().getUniqueId()), essentialsCommand.getName(), essentialsCommand,
                        arguments.toArray(new String[0]));
            } catch (NotEnoughArgumentsException exception) {
                event.getPlayer().sendMessage(essentialsCommand.getDescription());
                event.getPlayer().sendMessage(essentialsCommand.getUsage().replace("<command>", com));
                if (!exception.getMessage().isEmpty())
                    event.getPlayer().sendMessage(exception.getMessage());
            } catch (Exception exception) {
                if (!exception.getMessage().isEmpty())
                    event.getPlayer().sendMessage(exception.getMessage());
                else
                    exception.printStackTrace();
            }
        }
    }

    public void AddCommand(String command, String essentialsCommand) {
        this._essentialsCommandMap.put(command.toLowerCase(), essentialsCommand.toLowerCase());
    }

    public void RemoveCommand(String command) {
        this._essentialsCommandMap.remove(command.toLowerCase());
    }

    public List<String> GetNewEssentialsCommands() {
        return new ArrayList<>(this._essentialsCommandMap.keySet());
    }
}
