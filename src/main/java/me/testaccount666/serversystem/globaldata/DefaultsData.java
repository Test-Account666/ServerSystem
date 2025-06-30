package me.testaccount666.serversystem.globaldata;

import me.testaccount666.serversystem.managers.config.ConfigReader;

public class DefaultsData {
    private static Home Home;

    public static void initialize(ConfigReader config) {
        Home = new Home(config);
    }

    public static Home Home() {
        return Home;
    }

    public static class Home {
        private final int defaultMaxHomes;

        public Home(ConfigReader config) {
            defaultMaxHomes = config.getInt("Defaults.MaxHomes");
        }

        public int getDefaultMaxHomes() {
            return defaultMaxHomes;
        }
    }
}
