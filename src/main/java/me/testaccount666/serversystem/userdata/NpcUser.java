package me.testaccount666.serversystem.userdata;

import me.testaccount666.serversystem.managers.messages.MessageManager;
import me.testaccount666.serversystem.userdata.money.DisabledBankAccount;

import java.math.BigInteger;
import java.util.UUID;

public class NpcUser extends User {
    // 11111111-1111-1111-1111-111111111111 is never a player, so let's just use that for NPCs
    public static final UUID NPC_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    protected NpcUser() {
        super(UserManager.USER_DATA_PATH.resolve("${NPC_UUID}.yml.gz").toFile());
    }

    @Override
    protected void loadBasicData() {
        name = "NPC";
        uuid = NpcUser.NPC_UUID;
        bankAccount = new DisabledBankAccount(NPC_UUID, BigInteger.valueOf(0));
        playerLanguage = MessageManager.getDefaultLanguage();
    }

    @Override
    public void save() {
        // We don't want to save NPC users
    }
}
