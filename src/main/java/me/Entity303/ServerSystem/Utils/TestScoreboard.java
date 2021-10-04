package me.Entity303.ServerSystem.Utils;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class TestScoreboard {

    private void sendPacket(Player p, Packet packet) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendBoard(Player player, Plugin plugin) {
        Scoreboard scoreboard = new Scoreboard();

        ScoreboardTeam scoreboardTeam1 = new ScoreboardTeam(scoreboard, "99Spieler");
        PacketPlayOutScoreboardTeam team1 = new PacketPlayOutScoreboardTeam(scoreboardTeam1, 1);
        this.sendPacket(player, team1);

        ScoreboardTeam scoreboardTeam = new ScoreboardTeam(scoreboard, "99Spieler");
        scoreboardTeam.setPrefix(new ChatMessage("§7Spieler " + (char) (new Random().nextInt(26) + 65) + " "));
        scoreboardTeam.setColor(EnumChatFormat.GRAY);
        scoreboardTeam.getPlayerNameSet().add(player.getName());
        PacketPlayOutScoreboardTeam team = new PacketPlayOutScoreboardTeam(scoreboardTeam, 0);

        this.sendPacket(player, team);
        Bukkit.getScheduler().runTask(plugin, () -> player.setPlayerListName("§cSpieler | " + player.getName()));

        ScoreboardObjective obj = new ScoreboardObjective(scoreboard, "dummy", IScoreboardCriteria.DUMMY, new ChatMessage("Test"), IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        PacketPlayOutScoreboardObjective createPacket = new PacketPlayOutScoreboardObjective(obj, 0);
        PacketPlayOutScoreboardDisplayObjective display = new PacketPlayOutScoreboardDisplayObjective(1, obj);

        PacketPlayOutScoreboardObjective removePacket = new PacketPlayOutScoreboardObjective(obj, 1);
        PacketPlayOutScoreboardScore score0 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "dummy", "§a§7§7§7§1", 3);
        PacketPlayOutScoreboardScore score1 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "dummy", "§8»§7 Random", 2);
        PacketPlayOutScoreboardScore score2 = new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, "dummy", "§7➥§6 " + new Random().nextLong(), 1);

        this.sendPacket(player, removePacket);
        this.sendPacket(player, createPacket);
        this.sendPacket(player, display);

        this.sendPacket(player, score0);
        this.sendPacket(player, score1);
        this.sendPacket(player, score2);
    }
}
