package me.testaccount666.serversystem.managers.globaldata;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.testaccount666.serversystem.managers.config.ConfigReader;

@Accessors(fluent = true)
public class DefaultsData {
    @Getter
    private static Home _Home;

    public static void initialize(ConfigReader config) {
        _Home = new Home(config);
    }

    @Getter
    @Accessors(fluent = false)
    public static class Home {
        private final int _defaultMaxHomes;

        public Home(ConfigReader config) {
            _defaultMaxHomes = config.getInt("DefaultValues.Home.MaxHomes");
        }

    }
}
