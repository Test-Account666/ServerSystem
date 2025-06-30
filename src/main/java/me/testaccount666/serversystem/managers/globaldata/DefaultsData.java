package me.testaccount666.serversystem.managers.globaldata;

import me.testaccount666.serversystem.managers.config.ConfigReader;

public class DefaultsData {
    private static Home _Home;

    public static void initialize(ConfigReader config) {
        _Home = new Home(config);
    }

    public static Home Home() {
        return _Home;
    }

    public static class Home {
        private final int _defaultMaxHomes;

        public Home(ConfigReader config) {
            _defaultMaxHomes = config.getInt("Defaults.MaxHomes");
        }

        public int getDefaultMaxHomes() {
            return _defaultMaxHomes;
        }
    }
}
