package me.Entity303.ServerSystem.TabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class WorldTabCompleter implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("time")) {
            List<String> newList = new ArrayList<>();
            List<String> zeitenList = new ArrayList<>();
            if (args.length == 1) {
                List<String> zeiten = new ArrayList<>();
                zeitenList.add("Day");
                zeitenList.add("Noon");
                zeitenList.add("Night");
                zeitenList.add("Tag");
                zeitenList.add("Mittag");
                zeitenList.add("Nacht");
                for (String zeit : zeitenList)
                    if (zeit.toLowerCase().startsWith(args[0].toLowerCase())) zeiten.add(zeit);
                if (zeiten.size() > 0) return zeiten;
                else return zeitenList;
            } else if (args.length == 2) {
                List<World> welten = new ArrayList<>(Bukkit.getWorlds());
                List<String> weltenName = new ArrayList<>();
                List<String> welt = new ArrayList<>();
                for (World w : welten) weltenName.add(w.getName());
                for (World world : Bukkit.getWorlds())
                    if (world.getName().toLowerCase().startsWith(args[1].toLowerCase())) welt.add(world.getName());
                if (welt.size() > 0) return welt;
                else return weltenName;
            }
        } else if (command.getName().equalsIgnoreCase("day")) {
            if (args.length == 1) {
                List<World> welten = new ArrayList<>(Bukkit.getWorlds());
                List<String> weltenName = new ArrayList<>();
                List<String> welt = new ArrayList<>();
                for (World w : welten) weltenName.add(w.getName());
                for (World world : Bukkit.getWorlds())
                    if (world.getName().toLowerCase().startsWith(args[0].toLowerCase())) welt.add(world.getName());
                if (welt.size() > 0) return welt;
                else return weltenName;
            }
        } else if (command.getName().equalsIgnoreCase("noon")) {
            if (args.length == 1) {
                List<World> welten = new ArrayList<>(Bukkit.getWorlds());
                List<String> weltenName = new ArrayList<>();
                List<String> welt = new ArrayList<>();
                for (World w : welten) weltenName.add(w.getName());
                for (World world : Bukkit.getWorlds())
                    if (world.getName().toLowerCase().startsWith(args[0].toLowerCase())) welt.add(world.getName());
                if (welt.size() > 0) return welt;
                else return weltenName;
            }
        } else if (command.getName().equalsIgnoreCase("night")) {
            if (args.length == 1) {
                List<World> welten = new ArrayList<>(Bukkit.getWorlds());
                List<String> weltenName = new ArrayList<>();
                List<String> welt = new ArrayList<>();
                for (World w : welten) weltenName.add(w.getName());
                for (World world : Bukkit.getWorlds())
                    if (world.getName().toLowerCase().startsWith(args[0].toLowerCase())) welt.add(world.getName());
                if (welt.size() > 0) return welt;
                else return weltenName;
            }
        } else if (command.getName().equalsIgnoreCase("sun")) {
            if (args.length == 1) {
                List<World> welten = new ArrayList<>(Bukkit.getWorlds());
                List<String> weltenName = new ArrayList<>();
                List<String> welt = new ArrayList<>();
                for (World w : welten) weltenName.add(w.getName());
                for (World world : Bukkit.getWorlds())
                    if (world.getName().toLowerCase().startsWith(args[0].toLowerCase())) welt.add(world.getName());
                if (welt.size() > 0) return welt;
                else return weltenName;
            }
        } else if (command.getName().equalsIgnoreCase("rain")) {
            if (args.length == 1) {
                List<World> welten = new ArrayList<>(Bukkit.getWorlds());
                List<String> weltenName = new ArrayList<>();
                List<String> welt = new ArrayList<>();
                for (World w : welten) weltenName.add(w.getName());
                for (World world : Bukkit.getWorlds())
                    if (world.getName().toLowerCase().startsWith(args[0].toLowerCase())) welt.add(world.getName());
                if (welt.size() > 0) return welt;
                else return weltenName;
            }
        } else if (command.getName().equalsIgnoreCase("weather")) if (args.length == 1) {
            List<String> wetterArten = new ArrayList<>();
            wetterArten.add("Sonne");
            wetterArten.add("Sun");
            wetterArten.add("Sturm");
            wetterArten.add("Storm");
            wetterArten.add("Rain");
            List<String> w = new ArrayList<>();
            for (String wetter : wetterArten)
                if (wetter.toLowerCase().startsWith(args[0].toLowerCase())) w.add(wetter);
            if (w.size() > 0) return w;
            else return wetterArten;
        } else if (args.length == 2) {
            List<World> welten = new ArrayList<>(Bukkit.getWorlds());
            List<String> weltenName = new ArrayList<>();
            List<String> welt = new ArrayList<>();
            for (World w : welten) weltenName.add(w.getName());
            for (World world : Bukkit.getWorlds())
                if (world.getName().toLowerCase().startsWith(args[0].toLowerCase())) welt.add(world.getName());
            if (welt.size() > 0) return welt;
            else return weltenName;
        }
        return null;
    }
}
