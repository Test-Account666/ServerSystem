package me.entity303.serversystem.bansystem;

import java.util.UUID;

public class Ban {
    private final UUID BANNED_UUID;
    private final String BAN_REASON;
    private final String BAN_SENDER_UUID;
    private final Long UNBAN_TIME;
    private final String UNBAN_DATE;

    public Ban(UUID BANNED_UUID, String BAN_REASON, String BAN_SENDER_UUID, Long UNBAN_TIME, String UNBAN_DATE) {
        this.BANNED_UUID = BANNED_UUID;
        this.BAN_REASON = BAN_REASON;
        this.BAN_SENDER_UUID = BAN_SENDER_UUID;
        this.UNBAN_TIME = UNBAN_TIME;
        this.UNBAN_DATE = UNBAN_DATE;
    }

    public static Ban fromString(String banString) {
        String[] strings = banString.split("§!§");
        String bannedUUID = strings[0];
        String senderUUID = strings[1];
        String reason = strings[2];
        Long unbanTime = Long.parseLong(strings[3]);
        String unbanDate = strings[4];

        return new Ban(UUID.fromString(bannedUUID), reason, senderUUID, unbanTime, unbanDate);
    }

    public UUID getBANNED_UUID() {
        return this.BANNED_UUID;
    }

    public String getBAN_REASON() {
        return this.BAN_REASON;
    }

    public String getBAN_SENDER_UUID() {
        return this.BAN_SENDER_UUID;
    }

    public Long getUNBAN_TIME() {
        return this.UNBAN_TIME;
    }

    public String getUNBAN_DATE() {
        return this.UNBAN_DATE;
    }

    @Override
    public String toString() {
        return this.BANNED_UUID.toString() + "§!§" + this.BAN_SENDER_UUID + "§!§" + this.BAN_REASON + "§!§" + this.UNBAN_TIME + "§!§" + this.UNBAN_DATE;
    }
}
