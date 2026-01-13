package me.testaccount666.serversystem.userdata

import me.testaccount666.serversystem.managers.messages.MessageManager
import me.testaccount666.serversystem.userdata.money.DisabledBankAccount
import java.math.BigInteger
import java.util.*

class NpcUser internal constructor() : User(UserManager.USER_DATA_PATH.resolve("${NPC_UUID}.yml.gz").toFile()) {
    override fun loadBasicData() {
        name = "NPC"
        uuid = NPC_UUID
        bankAccount = DisabledBankAccount(NPC_UUID, BigInteger.valueOf(0))
        playerLanguage = MessageManager.defaultLanguage
    }

    override fun save() {
        // We don't want to save NPC users
    }

    companion object {
        // 11111111-1111-1111-1111-111111111111 is never a player, so let's just use that for NPCs
        @JvmField
        val NPC_UUID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
    }
}