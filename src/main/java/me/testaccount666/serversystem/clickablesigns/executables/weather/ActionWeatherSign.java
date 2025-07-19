package me.testaccount666.serversystem.clickablesigns.executables.weather;

import me.testaccount666.serversystem.clickablesigns.AbstractSignClickAction;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ActionWeatherSign extends AbstractSignClickAction {

    @Override
    public String getBasePermissionNode() {
        return "ClickableSigns.Weather";
    }

    @Override
    protected boolean executeAction(User user, Sign sign, FileConfiguration config) {
        var weatherType = config.getString("WeatherType", sign.getLine(1)).toLowerCase();
        weatherType = ChatColor.stripColor(weatherType);
        if (weatherType.isEmpty()) {
            sign("Weather.NoWeatherSpecified", user).build();
            return false;
        }

        var world = user.getPlayer().getWorld();

        var finalWeatherType = weatherType;
        return switch (weatherType) {
            case "sun", "clear" -> {
                world.setStorm(false);
                world.setThundering(false);
                sign("Weather.WeatherSet", user)
                        .postModifier(message -> message.replace("<WEATHER>", "clear")).build();
                yield true;
            }
            case "rain", "storm" -> {
                world.setStorm(true);
                world.setThundering(false);
                sign("Weather.WeatherSet", user)
                        .postModifier(message -> message.replace("<WEATHER>", "rain")).build();
                yield true;
            }
            case "thunder" -> {
                world.setStorm(true);
                world.setThundering(true);
                sign("Weather.WeatherSet", user)
                        .postModifier(message -> message.replace("<WEATHER>", "thunder")).build();
                yield true;
            }
            default -> {
                sign("Weather.InvalidWeather", user)
                        .postModifier(message -> message.replace("<WEATHER>", finalWeatherType)).build();
                yield false;
            }
        };
    }
}