package me.Entity303.ServerSystem.Commands.util;

import me.Entity303.ServerSystem.Commands.executable.*;
import me.Entity303.ServerSystem.Listener.PlotSquared.*;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.TabCompleter.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class CommandManager {
    private final ss serverSystem;
    private final List<String> serverSystemCommands = new ArrayList<>();
    private final Map<String, PluginCommand> knownCommands = new HashMap<>();
    private boolean dropActive = true;
    private boolean pickupActive = true;
    private boolean interactActive = true;
    private boolean chatActive = true;

    public CommandManager(ss serverSystem) {
        this.serverSystem = serverSystem;
    }

    public boolean isDropActive() {
        return this.dropActive;
    }

    public boolean isPickupActive() {
        return this.pickupActive;
    }

    public boolean isInteractActive() {
        return this.interactActive;
    }

    public boolean isChatActive() {
        return this.chatActive;
    }

    public void rtc(String command, TabCompleter completer) {
        if (this.serverSystem.getCommand(command) != null)
            this.serverSystem.getCommand(command).setTabCompleter(completer);
    }

    public Command getCommand(String command) {
        Object result = null;
        try {
            result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        SimpleCommandMap commandMap = (SimpleCommandMap) result;
        return commandMap.getCommand(command.toLowerCase(Locale.ROOT));
    }

    public void rc(String command, CommandExecutor executor, TabCompleter tabCompleter) {
        if (executor == null) System.out.println("Executor!");
        if (command == null) System.out.println("Command?!");

        if (tabCompleter == null)
            tabCompleter = new DefaultTabCompleter(this.serverSystem);


        File commandsFiles = new File("plugins//ServerSystem", "commands.yml");
        FileConfiguration commandsConfig = YamlConfiguration.loadConfiguration(commandsFiles);

        if (commandsConfig.getBoolean(command.toLowerCase())) {
            this.registerCommand(executor, tabCompleter, this.serverSystem, command);

            this.serverSystemCommands.add(command.toLowerCase());

            File aliasFiles = new File("plugins//ServerSystem", "aliases.yml");
            FileConfiguration aliasConfig = YamlConfiguration.loadConfiguration(aliasFiles);

            String aliasString = aliasConfig.getString("Aliases." + command.toLowerCase() + ".aliases");

            if (aliasString != null) {
                if (!aliasString.equalsIgnoreCase("No Aliases")) {
                    String[] aliases = aliasConfig.getString("Aliases." + command.toLowerCase() + ".aliases").replace(" ", "").toLowerCase().split(",");
                    this.addAlias(command, executor, aliases);
                    this.serverSystemCommands.addAll(Arrays.asList(aliases));
                }
            } else this.serverSystem.warn("Null alias for: " + command);
        } else if (command.equalsIgnoreCase("drop")) this.dropActive = false;
        else if (command.equalsIgnoreCase("chat")) this.chatActive = false;
        else if (command.equalsIgnoreCase("pickup")) this.pickupActive = false;
        else if (command.equalsIgnoreCase("interact")) this.interactActive = false;
    }

    private Object getPrivateField(Object object, String field) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();

        String version = this.serverSystem.getVersionManager().getVersion();

        Field objectField = field.equals("commandMap") ? clazz.getDeclaredField(field) : field.equals("knownCommands") ? this.serverSystem.getVersionManager().isV113() ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field) : null;
        objectField.setAccessible(true);
        return objectField.get(object);
    }

    public void registerCommand(CommandExecutor executor, TabCompleter tabCompleter, Plugin plugin, String... aliases) {
        if (this.serverSystem.getServer().getPluginCommand(aliases[0]) != null)
            if (!this.serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName().equalsIgnoreCase("ServerSystem"))
                this.deactivateBukkitCommand(aliases[0], this.serverSystem.getServer().getPluginCommand(aliases[0]).getPlugin().getName());

        Object map = null;
        try {
            map = this.getPrivateField(this.getCommandMap(), "knownCommands");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;

        aliases = Arrays.stream(aliases).map(String::toLowerCase).toArray(String[]::new);
        PluginCommand command = this.getNewCommand(aliases[0], plugin);

        command.setExecutor(executor);
        if (tabCompleter != null)
            command.setTabCompleter(tabCompleter);

        for (String alias : aliases) {
            knownCommands.remove(alias.toLowerCase());
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                this.serverSystem.getVersionStuff().getBukkitCommandWrap().unwrap(alias.toLowerCase());
        }

        for (String alias : aliases) {
            alias = alias.toLowerCase();
            knownCommands.put("serversystem:" + alias.toLowerCase(), command);
            knownCommands.put(alias.toLowerCase(), command);
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null) {
                this.serverSystem.getVersionStuff().getBukkitCommandWrap().wrap(command, alias);
                this.serverSystem.getVersionStuff().getBukkitCommandWrap().wrap(command, "serversystem:" + alias);
            }
        }

        command.register(getCommandMap());
    }

    private PluginCommand getNewCommand(String name, Plugin plugin) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return command;
    }

    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(this.serverSystem.getServer().getPluginManager());
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return commandMap;
    }

    private void addAlias(String cmd, CommandExecutor executor, String[] aliases) {
        cmd = cmd.toLowerCase();
        try {
            Object result = null;
            try {
                result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = this.getPrivateField(commandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;

            for (String alias : aliases) {
                knownCommands.remove(alias.toLowerCase());
                if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                    this.serverSystem.getVersionStuff().getBukkitCommandWrap().unwrap(alias.toLowerCase(Locale.ROOT));
                knownCommands.remove("serversystem:" + alias.toLowerCase());
            }

            PluginCommand command = this.getNewCommand(cmd.toLowerCase(), this.serverSystem);

            command.setExecutor(executor);

            command.setAliases(Arrays.asList(aliases.clone()));

            for (String alias : aliases) {
                alias = alias.toLowerCase();
                knownCommands.put(alias.toLowerCase(), command);
                knownCommands.put("serversystem:" + alias.toLowerCase(), command);
                if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null) {
                    this.serverSystem.getVersionStuff().getBukkitCommandWrap().wrap(command, alias);
                    this.serverSystem.getVersionStuff().getBukkitCommandWrap().wrap(command, "serversystem:" + alias);
                }
            }

            command.register(commandMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deactivateOwnCommand(String cmd) {
        try {
            String plugin = "serversystem";
            Object result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = this.getPrivateField(commandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            if (cmd == null) return;
            if (this.serverSystem.getServer().getPluginCommand(plugin + ":" + cmd) == this.serverSystem.getServer().getPluginCommand(cmd)) {
                knownCommands.remove(cmd);
                if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                    this.serverSystem.getVersionStuff().getBukkitCommandWrap().unwrap(cmd);
            }
            knownCommands.remove(plugin + ":" + cmd);
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                this.serverSystem.getVersionStuff().getBukkitCommandWrap().unwrap(plugin + ":" + cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deactivateBukkitCommand(String cmd, String plugin) {
        try {
            Object result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = this.getPrivateField(commandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            if (cmd == null) return;
            if (plugin == null) return;
            this.knownCommands.put(plugin + ":" + cmd, this.serverSystem.getServer().getPluginCommand(plugin + ":" + cmd));
            if (this.serverSystem.getServer().getPluginCommand(plugin + ":" + cmd) == this.serverSystem.getServer().getPluginCommand(cmd)) {
                knownCommands.remove(cmd);
                if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                    this.serverSystem.getVersionStuff().getBukkitCommandWrap().unwrap(cmd);
            }
            knownCommands.remove(plugin + ":" + cmd);
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                this.serverSystem.getVersionStuff().getBukkitCommandWrap().unwrap(plugin + ":" + cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void activateBukkitCommand(PluginCommand cmd) {
        try {
            Object result = this.getPrivateField(this.serverSystem.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = this.getPrivateField(commandMap, "knownCommands");
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            if (cmd == null) return;
            knownCommands.put(cmd.getName().toLowerCase(), cmd);
            if (Bukkit.getServer().getPluginCommand(cmd.getName().toLowerCase()) == null)
                if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                    this.serverSystem.getVersionStuff().getBukkitCommandWrap().wrap(cmd, cmd.getName().toLowerCase());
            knownCommands.put(cmd.getPlugin().getName().toLowerCase() + ":" + cmd.getName().toLowerCase(), cmd);
            if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                this.serverSystem.getVersionStuff().getBukkitCommandWrap().wrap(cmd, cmd.getPlugin().getName().toLowerCase() + ":" + cmd.getName().toLowerCase());
            if (cmd.getAliases().size() > 0) for (String alias : cmd.getAliases()) {
                knownCommands.put(alias.toLowerCase(), cmd);
                if (Bukkit.getServer().getPluginCommand(alias.toLowerCase().toLowerCase()) == null)
                    if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                        this.serverSystem.getVersionStuff().getBukkitCommandWrap().wrap(cmd, alias.toLowerCase());
                knownCommands.put(cmd.getPlugin().getName().toLowerCase() + ":" + alias.toLowerCase(), cmd);
                if (this.serverSystem.getVersionStuff().getBukkitCommandWrap() != null)
                    this.serverSystem.getVersionStuff().getBukkitCommandWrap().wrap(cmd, cmd.getPlugin().getName().toLowerCase() + ":" + alias.toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterCommands() {
        if (this.knownCommands.size() > 0) {
            for (String cmd : this.knownCommands.keySet()) {
                String plugin = cmd.split(":")[0];
                String command = cmd.split(":")[1];
                this.serverSystem.log("Reactivating command " + command + " from " + plugin + "!");
                this.activateBukkitCommand(this.knownCommands.get(cmd));
            }

            if (this.serverSystem.getEssentialsCommandListener() != null)
                for (String cmd : this.serverSystem.getEssentialsCommandListener().getNewEssentialsCommands()) {
                    this.serverSystem.log("Reactivating command " + cmd + "!");
                    this.serverSystem.getEssentialsCommandListener().removeCommand(cmd);
                }
        }

        try {
            for (String command : this.serverSystemCommands) {
                command = command.toLowerCase();
                this.deactivateOwnCommand(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerCommands() {
        this.rc("clearchat", new COMMAND_clearchat(this.serverSystem), null);
        this.rc("back", new COMMAND_back(this.serverSystem), null);
        this.rc("broadcast", new COMMAND_broadcast(this.serverSystem), null);
        this.rc("sethome", new COMMAND_sethome(this.serverSystem), null);
        this.rc("delhome", new COMMAND_delhome(this.serverSystem), new TABCOMPLETER_delhome(this.serverSystem));
        this.rc("home", new COMMAND_home(this.serverSystem), new TABCOMPLETER_home(this.serverSystem));
        this.rc("workbench", new COMMAND_workbench(this.serverSystem), null);
        this.rc("tpa", new COMMAND_tpa(this.serverSystem), null);
        this.rc("tpahere", new COMMAND_tpahere(this.serverSystem), null);
        this.rc("tpaccept", new COMMAND_tpaccept(this.serverSystem), null);
        this.rc("tpdeny", new COMMAND_tpdeny(this.serverSystem), null);
        this.rc("vanish", new COMMAND_vanish(this.serverSystem), null);
        this.rc("chat", new COMMAND_chat(this.serverSystem), null);
        this.rc("interact", new COMMAND_interact(this.serverSystem), null);
        this.rc("pickup", new COMMAND_pickup(this.serverSystem), null);
        this.rc("drop", new COMMAND_drop(this.serverSystem), null);
        this.rc("tptoggle", new COMMAND_tptoggle(this.serverSystem), null);
        this.rc("clearenderchest", new COMMAND_clearenderchest(this.serverSystem), null);
        this.rc("clearinventory", new COMMAND_clearinventory(this.serverSystem), null);
        this.rc("checkgamemode", new COMMAND_checkgamemode(this.serverSystem), null);
        this.rc("speed", new COMMAND_speed(this.serverSystem), new TABCOMPLETER_speed(this.serverSystem));
        this.rc("serversystem", new COMMAND_serversystem(this.serverSystem), new TABCOMPLETER_serversystem(this.serverSystem));
        this.rc("gamemode", new COMMAND_gamemode(this.serverSystem), new GameModeTabCompleter());
        this.rc("gmc", new COMMAND_gmc(this.serverSystem), null);
        this.rc("gms", new COMMAND_gms(this.serverSystem), null);
        this.rc("gma", new COMMAND_gma(this.serverSystem), null);
        this.rc("gmsp", new COMMAND_gmsp(this.serverSystem), null);
        this.rc("feed", new COMMAND_feed(this.serverSystem), null);
        this.rc("time", new COMMAND_time(this.serverSystem), new WorldTabCompleter());
        this.rc("heal", this.serverSystem.getVersionManager().is188() ? new COMMAND_heal(this.serverSystem) : new COMMAND_heal_newer(this.serverSystem), null);
        this.rc("hat", new COMMAND_hat(this.serverSystem), null);
        this.rc("weather", new COMMAND_weather(this.serverSystem), new WorldTabCompleter());
        this.rc("day", new COMMAND_day(this.serverSystem), new WorldTabCompleter());
        this.rc("night", new COMMAND_night(this.serverSystem), new WorldTabCompleter());
        this.rc("restart", new COMMAND_restart(this.serverSystem), null);
        this.rc("noon", new COMMAND_noon(this.serverSystem), new WorldTabCompleter());
        this.rc("ping", new COMMAND_ping(this.serverSystem), null);
        this.rc("burn", new COMMAND_burn(this.serverSystem), null);
        this.rc("ip", new COMMAND_ip(this.serverSystem), null);
        this.rc("repair", new COMMAND_repair(this.serverSystem), null);
        this.rc("disenchant", new COMMAND_disenchant(this.serverSystem), null);
        this.rc("suicide", new COMMAND_suicide(this.serverSystem), null);
        this.rc("extinguish", new COMMAND_extinguish(this.serverSystem), null);
        this.rc("lag", new COMMAND_lag(this.serverSystem), null);
        this.rc("sudo", new COMMAND_sudo(this.serverSystem), null);
        this.rc("smelt", new COMMAND_smelt(this.serverSystem), null);
        this.rc("stack", new COMMAND_stack(this.serverSystem), null);
        this.rc("sun", new COMMAND_sun(this.serverSystem), new WorldTabCompleter());
        this.rc("rain", new COMMAND_rain(this.serverSystem), new WorldTabCompleter());
        this.rc("disposal", new COMMAND_disposal(this.serverSystem), null);
        this.rc("editsign", Bukkit.getPluginManager().getPlugin("PlotSquared") != null ? new COMMAND_editsignpapi(this.serverSystem) : new COMMAND_editsign(this.serverSystem), null);
        this.rc("teamchat", new COMMAND_teamchat(this.serverSystem), null);
        this.rc("sign", new COMMAND_sign(this.serverSystem), null);
        this.rc("unsign", new COMMAND_unsign(this.serverSystem), null);
        this.rc("tppos", new COMMAND_tppos(this.serverSystem), null);
        if (this.serverSystem.getConfig().getBoolean("bansystem.enabled")) {
            this.rc("ban", new COMMAND_ban(this.serverSystem), new TABCOMPLETER_ban(this.serverSystem));
            this.rc("unban", new COMMAND_unban(this.serverSystem), new TABCOMPLETER_unban(this.serverSystem));
            this.rc("mute", new COMMAND_mute(this.serverSystem), new TABCOMPLETER_mute(this.serverSystem));
            this.rc("unmute", new COMMAND_unmute(this.serverSystem), new TABCOMPLETER_unmute(this.serverSystem));
            this.rc("kick", new COMMAND_kick(this.serverSystem), null);
            this.rc("kickall", new COMMAND_kickall(this.serverSystem), null);
        }
        if (this.serverSystem.getConfig().getBoolean("economy.enabled")) {
            this.rc("money", new COMMAND_money(this.serverSystem), null);
            this.rc("pay", new COMMAND_pay(this.serverSystem), null);
            this.rc("economy", new COMMAND_economy(this.serverSystem), new EconomyTabCompleter(this.serverSystem));
            this.rc("baltop", new COMMAND_baltop(this.serverSystem), null);
        }
        this.rc("fly", new COMMAND_fly(this.serverSystem), null);
        this.rc("commandspy", new COMMAND_commandspy(this.serverSystem), null);
        this.rc("warp", new COMMAND_warp(this.serverSystem), new TABCOMPLETER_warp(this.serverSystem));
        this.rc("delwarp", new COMMAND_delwarp(this.serverSystem), new TABCOMPLETER_warp(this.serverSystem));
        this.rc("setwarp", new COMMAND_setwarp(this.serverSystem), null);
        this.rc("setspawn", new COMMAND_setspawn(this.serverSystem), null);
        this.rc("spawn", new COMMAND_spawn(this.serverSystem), null);
        this.rc("createkit", new COMMAND_createkit(this.serverSystem), null);
        this.rc("delkit", new COMMAND_delkit(this.serverSystem), new TABCOMPLETER_deletekit(this.serverSystem));
        this.rc("kit", new COMMAND_kit(this.serverSystem), new TABCOMPLETER_kit(this.serverSystem));
        this.rc("invsee", new COMMAND_invsee(this.serverSystem), null);
        this.rc("enderchest", new COMMAND_enderchest(this.serverSystem), null);
        this.rc("tp", new COMMAND_tp(this.serverSystem), null);
        this.rc("tpo", new COMMAND_tpo(this.serverSystem), null);
        this.rc("tphere", new COMMAND_tphere(this.serverSystem), null);
        this.rc("tpohere", new COMMAND_tpohere(this.serverSystem), null);
        this.rc("tpall", new COMMAND_tpall(this.serverSystem), null);
        this.rc("msg", new COMMAND_msg(this.serverSystem), null);
        this.rc("msgtoggle", new COMMAND_msgtoggle(this.serverSystem), null);
        this.rc("socialspy", new COMMAND_socialspy(this.serverSystem), null);
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null)
            this.rc("convertfromessentials", new COMMAND_essentialsconvertion(this.serverSystem), null);
        this.rc("god", new COMMAND_god(this.serverSystem), null);
        this.rc("reply", new COMMAND_reply(this.serverSystem), null);
        this.rc("maintenance", new COMMAND_maintenance(this.serverSystem), null);
        this.rc("rules", new COMMAND_rules(this.serverSystem), null);
        this.rc("rename", new COMMAND_rename(this.serverSystem), null);
        this.rc("recipe", new COMMAND_recipe(this.serverSystem), new TABCOMPLETER_recipe(this.serverSystem));
        this.rc("warps", new COMMAND_warps(this.serverSystem), null);
        this.rc("checkhealth", new COMMAND_checkhealth(this.serverSystem), null);
        this.rc("lightning", new COMMAND_lightning(this.serverSystem), null);
        this.rc("getpos", new COMMAND_getpos(this.serverSystem), null);
        this.rc("anvil", new COMMAND_anvil(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualCartography() != null)
            this.rc("cartographytable", new COMMAND_cartography(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualGrindstone() != null)
            this.rc("grindstone", new COMMAND_grindstone(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualLoom() != null)
            this.rc("loom", new COMMAND_loom(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualStoneCutter() != null)
            this.rc("stonecutter", new COMMAND_stonecutter(this.serverSystem), null);
        if (this.serverSystem.getVersionStuff().getVirtualSmithing() != null)
            this.rc("smithingtable", new COMMAND_smithingtable(this.serverSystem), null);
        this.rc("break", new COMMAND_break(this.serverSystem), null);
        this.rc("offlineteleport", new COMMAND_offlineteleport(this.serverSystem), null);
        this.rc("offlineteleporthere", new COMMAND_offlineteleporthere(this.serverSystem), null);
        this.rc("offlineinvsee", new COMMAND_offlineinvsee(this.serverSystem), null);
        this.rc("offlineenderchest", new COMMAND_offlineenderchest(this.serverSystem), null);

        boolean plotSquaredAlreadyRegistered = false;

        try {
            Class.forName("com.plotsquared.core.PlotAPI");
            new PlotListener4();
            new PlotListener(this.serverSystem);
            plotSquaredAlreadyRegistered = true;
        } catch (Exception ignored) {
        }

        if (!plotSquaredAlreadyRegistered)
            try {
                Class.forName("com.plotsquared.core.events.PlayerEnterPlotEvent");
                new PlotListener3();
                new PlotListener(this.serverSystem);
                plotSquaredAlreadyRegistered = true;
            } catch (Exception ignored) {

            }

        if (!plotSquaredAlreadyRegistered)
            try {
                Class.forName("com.github.intellectualsites.plotsquared.bukkit.events.PlayerEnterPlotEvent");
                Bukkit.getPluginManager().registerEvents(new PlotListener2(this.serverSystem), this.serverSystem);
                new PlotListener(this.serverSystem);
                plotSquaredAlreadyRegistered = true;
            } catch (Exception ignored) {

            }

        if (!plotSquaredAlreadyRegistered)
            try {
                Class.forName("com.plotsquared.bukkit.events.PlayerEnterPlotEvent");
                Bukkit.getPluginManager().registerEvents(new PlotListener1(this.serverSystem), this.serverSystem);
                new PlotListener(this.serverSystem);
            } catch (Exception ignored) {

            }
    }
}
