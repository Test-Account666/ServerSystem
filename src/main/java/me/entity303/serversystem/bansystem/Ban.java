package me.entity303.serversystem.bansystem;

import java.util.UUID;

public record Ban(UUID BANNED_UUID, String BAN_REASON, String BAN_SENDER_UUID, Long UNBAN_TIME, String UNBAN_DATE) {

    public static Ban fromString(String banString) {
        var strings = banString.split("§!§");
        var bannedUUID = strings[0];
        var senderUUID = strings[1];
        var reason = strings[2];
        Long unbanTime = Long.parseLong(strings[3]);
        var unbanDate = strings[4];

        return new Ban(UUID.fromString(bannedUUID), reason, senderUUID, unbanTime, unbanDate);
    }

    @Override
    public String toString() {
        return this.BANNED_UUID.toString() + "§!§" + this.BAN_SENDER_UUID + "§!§" + this.BAN_REASON + "§!§" + this.UNBAN_TIME + "§!§" + this.UNBAN_DATE;
    }
}
