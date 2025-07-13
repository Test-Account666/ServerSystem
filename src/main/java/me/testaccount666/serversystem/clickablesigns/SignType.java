package me.testaccount666.serversystem.clickablesigns;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.testaccount666.serversystem.ServerSystem;
import me.testaccount666.serversystem.userdata.User;
import org.bukkit.block.Sign;

import java.util.function.BiConsumer;
import java.util.function.Function;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum SignType {
    FREE("Free", "&9[FREE]", (user, sign) -> {
    }, (sign) -> {
        return true;
    }),
    KIT("Kit", "&9[KIT]", (user, sign) -> {
    }, (sign) -> {
        return true;
    }),
    WARP("Warp", "&9[WARP]", (user, sign) -> {
    }, (sign) -> {
        var warpName = sign.getLine(1);
        if (warpName.isEmpty()) return false;

        var warpManager = ServerSystem.Instance.getWarpManager();
        return warpManager.getWarpByName(warpName).isPresent();
    }),
    TIME("Time", "&9[TIME]", (user, sign) -> {
    }, (sign) -> {
        var timeType = sign.getLine(1).toLowerCase();
        if (timeType.isEmpty()) return false;

        return timeType.equals("day") ||
                timeType.equals("night") ||
                timeType.equals("noon") ||
                timeType.equals("midnight") ||
                timeType.matches("\\d+");
    }),
    WEATHER("Weather", "&9[WEATHER]", (user, sign) -> {
    }, (sign) -> {
        var weatherType = sign.getLine(1).toLowerCase();
        if (weatherType.isEmpty()) return false;

        return weatherType.equals("sun") ||
                weatherType.equals("clear") ||
                weatherType.equals("storm") ||
                weatherType.equals("thunder") ||
                weatherType.equals("rain");
    });

    private final String _key;
    private final String _signName;
    private final BiConsumer<User, Sign> _clickAction;
    private final Function<Sign, Boolean> _validator;
}
