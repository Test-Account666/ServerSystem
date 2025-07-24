package me.testaccount666.serversystem.placeholderapi;

import me.testaccount666.serversystem.placeholderapi.executables.PlaceholderExpansionWrapper;

public class PlaceholderApiSupport {
    private static PlaceholderExpansionWrapper _Wrapper;

    private PlaceholderApiSupport() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    public static boolean isPlaceholderApiInstalled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static void registerPlaceholders() {
        if (!isPlaceholderApiInstalled()) return;

        if (_Wrapper != null) unregisterPlaceholders();

        _Wrapper = new PlaceholderExpansionWrapper();
        _Wrapper.register();
    }

    public static void unregisterPlaceholders() {

        if (!isPlaceholderApiInstalled()) return;

        if (_Wrapper == null) return;

        _Wrapper.unregister();
        _Wrapper = null;

    }
}
