package me.testaccount666.serversystem.commands.executables.vanish

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.ServerSystem.Companion.instance
import me.testaccount666.serversystem.managers.PermissionManager.hasCommandPermission
import me.testaccount666.serversystem.userdata.User
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.entity.CraftPlayer
import java.util.*

class VanishPacket {
    fun sendVanishPacket(vanishUser: User) {
        val enableVanish = vanishUser.isVanish
        val craftPlayer = vanishUser.getPlayer() as CraftPlayer

        val gameMode = if (enableVanish) GameType.SPECTATOR else GameType.byName(craftPlayer.gameMode.name)

        val constructor = ClientboundPlayerInfoUpdatePacket.Entry::class.java.constructors.firstOrNull { it.parameterCount > 6 } ?: run {
            ServerSystem.log.warning("VanishPacket: Failed to find constructor for ClientboundPlayerInfoUpdatePacket.Entry")
            return
        }

        val parameters = if (constructor.parameterCount == 9) {
            listOf(
                vanishUser.uuid, craftPlayer.profile, true,
                craftPlayer.ping, gameMode,
                craftPlayer.handle.getDisplayName(),
                true, craftPlayer.playerListOrder, null
            )

        } else {
            listOf(
                vanishUser.uuid, craftPlayer.profile, true,
                craftPlayer.ping, gameMode,
                craftPlayer.handle.getDisplayName(), null
            )
        }


        val updateEntry = runCatching {
            constructor.newInstance(*parameters.toTypedArray()) as ClientboundPlayerInfoUpdatePacket.Entry
        }.onFailure {
            ServerSystem.log.warning("VanishPacket: Failed to create ClientboundPlayerInfoUpdatePacket.Entry: ${it.message}")
        }.getOrNull() ?: return

        val infoPacket = ClientboundPlayerInfoUpdatePacket(
            EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE),
            updateEntry
        )

        for (all in Bukkit.getOnlinePlayers()) {
            if (all === vanishUser.getPlayer()) continue

            if (!enableVanish) {
                val craftAll = all as CraftPlayer
                craftAll.handle.connection.send(infoPacket)
                all.showPlayer(instance, vanishUser.getPlayer()!!)
                continue
            }

            if (hasCommandPermission(all, "Vanish.Show", false)) {
                val craftAll = all as CraftPlayer
                craftAll.handle.connection.send(infoPacket)
                continue
            }

            all.hidePlayer(instance, vanishUser.getPlayer()!!)
        }
    }
}
