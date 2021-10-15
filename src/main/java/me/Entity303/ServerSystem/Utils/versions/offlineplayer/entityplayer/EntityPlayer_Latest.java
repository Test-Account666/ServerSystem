package me.Entity303.ServerSystem.Utils.versions.offlineplayer.entityplayer;

import com.mojang.authlib.GameProfile;
import me.Entity303.ServerSystem.Main.ss;
import me.Entity303.ServerSystem.Utils.MessageUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.World;
import org.bukkit.OfflinePlayer;

public class EntityPlayer_Latest extends MessageUtils implements EntityPlayer {

    public EntityPlayer_Latest(ss plugin) {
        super(plugin);
    }

    @Override
    public Object getEntityPlayer(OfflinePlayer offlinePlayer) {
        GameProfile gameProfile = new GameProfile(offlinePlayer.getUniqueId(), offlinePlayer.getName());
        return new net.minecraft.server.level.EntityPlayer(MinecraftServer.getServer(), MinecraftServer.getServer().getWorldServer(World.f), gameProfile);
    }
}
