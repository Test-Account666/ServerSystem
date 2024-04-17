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

public class EssentialsCommandListener implements Listener {
    private final Essentials essentials;
    private final ServerSystem plugin;
    private final Map<String, String> essentialsCommandMap = new HashMap<>();

    public EssentialsCommandListener(Essentials essentials, ServerSystem plugin) {
        this.essentials = essentials;
        this.plugin = plugin;
    }

    @EventHandler
    public void onNewEssentialsCommand(PlayerCommandPreprocessEvent e) {
        var com = e.getMessage().split(" ")[0].replaceFirst("/", "").toLowerCase();
        if (this.essentialsCommandMap.containsKey(com)) {
            e.setCancelled(true);
            List<String> args = new ArrayList<>();

            if (e.getMessage().split(" ").length >= 2)
                for (var i = 1; i < e.getMessage().split(" ").length; i++)
                    args.add(i - 1, e.getMessage().split(" ")[i]);

            var command = this.essentialsCommandMap.get(com);
            var essentialsCommand = this.plugin.getServer().getPluginCommand("essentials:" + command);

            IEssentialsCommand cmd;

            try {
                cmd = (IEssentialsCommand) Essentials.class.getClassLoader()
                                                           .loadClass("com.earth2me.essentials.commands.Command" + essentialsCommand.getName())
                                                           .newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException instantiationException) {
                if (!instantiationException.getMessage().isEmpty())
                    e.getPlayer().sendMessage(instantiationException.getMessage());
                instantiationException.printStackTrace();
                return;
            }

            cmd.setEssentials(this.essentials);
            cmd.setEssentialsModule(null);

            try {
                cmd.run(this.essentials.getServer(), this.essentials.getUser(e.getPlayer().getUniqueId()), essentialsCommand.getName(), essentialsCommand,
                        args.toArray(new String[0]));
            } catch (NotEnoughArgumentsException exception) {
                e.getPlayer().sendMessage(essentialsCommand.getDescription());
                e.getPlayer().sendMessage(essentialsCommand.getUsage().replaceAll("<command>", com));
                if (!exception.getMessage().isEmpty())
                    e.getPlayer().sendMessage(exception.getMessage());
            } catch (Exception exception) {
                if (!exception.getMessage().isEmpty())
                    e.getPlayer().sendMessage(exception.getMessage());
                else
                    exception.printStackTrace();
            }
        }
    }

    public void addCommand(String command, String essentialsCommand) {
        this.essentialsCommandMap.put(command.toLowerCase(), essentialsCommand.toLowerCase());
    }

    public void removeCommand(String command) {
        this.essentialsCommandMap.remove(command.toLowerCase());
    }

    public List<String> getNewEssentialsCommands() {
        return new ArrayList<>(this.essentialsCommandMap.keySet());
    }
}
