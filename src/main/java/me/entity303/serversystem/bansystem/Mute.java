package me.entity303.serversystem.bansystem;

public class Mute {
    private final String SENDER_UUID;
    private final String MUTED_UUID;
    private final Long UNMUTE_TIME;
    private final String UNMUTE_DATE;
    private final String REASON;
    private final Boolean SHADOW;

    public Mute(String MUTED_UUID, String SENDER_UUID, Long UNMUTE_TIME, String UNMUTE_DATE, String REASON) {
        this.SENDER_UUID = SENDER_UUID;
        this.MUTED_UUID = MUTED_UUID;
        this.UNMUTE_TIME = UNMUTE_TIME;
        this.UNMUTE_DATE = UNMUTE_DATE;
        this.REASON = REASON;
        this.SHADOW = false;
    }

    public Mute(String MUTED_UUID, String SENDER_UUID, Long UNMUTE_TIME, String UNMUTE_DATE, String REASON, boolean SHADOW) {
        this.SENDER_UUID = SENDER_UUID;
        this.MUTED_UUID = MUTED_UUID;
        this.UNMUTE_TIME = UNMUTE_TIME;
        this.UNMUTE_DATE = UNMUTE_DATE;
        this.REASON = REASON;
        this.SHADOW = SHADOW;
    }

    public static Mute fromString(String muteString) {
        var strings = muteString.split("§!§");
        var mutedUUID = strings[0];
        var senderUUID = strings[1];
        var shadow = Boolean.parseBoolean(strings[2]);
        var reason = strings[3];
        Long unmuteTime = Long.parseLong(strings[4]);
        var unmuteDate = strings[5];

        return new Mute(mutedUUID, senderUUID, unmuteTime, unmuteDate, reason, shadow);
    }

    public boolean isSHADOW() {
        return this.SHADOW;
    }

    public String getSENDER_UUID() {
        return this.SENDER_UUID;
    }

    public String getMUTED_UUID() {
        return this.MUTED_UUID;
    }

    public Long getUNMUTE_TIME() {
        return this.UNMUTE_TIME;
    }

    public String getUNMUTE_DATE() {
        return this.UNMUTE_DATE;
    }

    public String getREASON() {
        return this.REASON;
    }

    @Override
    public String toString() {
        return this.MUTED_UUID + "§!§" + this.SENDER_UUID + "§!§" + this.SHADOW + "§!§" + this.REASON + "§!§" + this.UNMUTE_TIME + "§!§" + this.UNMUTE_DATE;
    }
}
