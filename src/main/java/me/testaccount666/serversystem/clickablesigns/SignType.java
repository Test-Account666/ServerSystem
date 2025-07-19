package me.testaccount666.serversystem.clickablesigns;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.testaccount666.serversystem.clickablesigns.executables.give.ActionGiveSign;
import me.testaccount666.serversystem.clickablesigns.executables.give.ConfiguratorGiveSign;
import me.testaccount666.serversystem.clickablesigns.executables.kit.ActionKitSign;
import me.testaccount666.serversystem.clickablesigns.executables.kit.ConfiguratorKitSign;
import me.testaccount666.serversystem.clickablesigns.executables.time.ActionTimeSign;
import me.testaccount666.serversystem.clickablesigns.executables.time.ConfiguratorTimeSign;
import me.testaccount666.serversystem.clickablesigns.executables.warp.ActionWarpSign;
import me.testaccount666.serversystem.clickablesigns.executables.warp.ConfiguratorWarpSign;
import me.testaccount666.serversystem.clickablesigns.executables.weather.ActionWeatherSign;
import me.testaccount666.serversystem.clickablesigns.executables.weather.ConfiguratorWeatherSign;

import java.util.Optional;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum SignType {
    GIVE("Give", "&#3F3FD1[Give]", new ActionGiveSign(), new ConfiguratorGiveSign()),
    KIT("Kit", "&#3F3FD1[KIT]", new ActionKitSign(), new ConfiguratorKitSign()),
    WARP("Warp", "&#3F3FD1[WARP]", new ActionWarpSign(), new ConfiguratorWarpSign()),
    TIME("Time", "&#3F3FD1[TIME]", new ActionTimeSign(), new ConfiguratorTimeSign()),
    WEATHER("Weather", "&#3F3FD1[WEATHER]", new ActionWeatherSign(), new ConfiguratorWeatherSign());

    private final String _key;
    private final String _signName;
    private final SignClickAction _clickAction;
    private final SignConfigurator _configurator;

    public static Optional<SignType> getSignTypeByKey(String key) {
        for (var signType : values()) {
            if (!signType._key.equalsIgnoreCase(key)) continue;
            return Optional.of(signType);
        }

        return Optional.empty();
    }
}
