package me.testaccount666.serversystem.clickablesigns.executables.weather;

import me.testaccount666.serversystem.clickablesigns.AbstractSignConfigurator;
import me.testaccount666.serversystem.clickablesigns.SignType;
import me.testaccount666.serversystem.userdata.User;
import me.testaccount666.serversystem.utils.ChatColor;
import me.testaccount666.serversystem.utils.ComponentColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import static me.testaccount666.serversystem.utils.MessageBuilder.sign;

public class ConfiguratorWeatherSign extends AbstractSignConfigurator {

    @Override
    protected String getCreatePermissionNode() {
        return "ClickableSigns.Weather.Create";
    }

    @Override
    protected SignType getSignType() {
        return SignType.WEATHER;
    }

    @Override
    protected boolean validateConfiguration(User user, Sign sign, YamlConfiguration config) {
        var front = sign.getSide(Side.FRONT);
        var weatherType = front.getLine(1).toLowerCase();
        if (weatherType.isEmpty()) {
            sign("Weather.NoWeatherSpecified", user).build();
            return false;
        }

        if (!isValidWeatherType(weatherType)) {
            sign("Weather.InvalidWeather", user)
                    .postModifier(message -> message.replace("<WEATHER>", weatherType)).build();
            return false;
        }

        front.line(0, ComponentColor.translateToComponent(SignType.WEATHER.signName()));
        front.line(1, ComponentColor.translateToComponent("&2${weatherType}"));
        var back = sign.getSide(Side.BACK);
        for (var index = 0; index < 4; index++) back.line(index, front.line(index));
        sign.update();
        return true;
    }

    @Override
    protected void addSignSpecificConfiguration(User user, Sign sign, FileConfiguration config) {
        var weatherType = sign.getSide(Side.FRONT).getLine(1).toLowerCase();
        weatherType = ChatColor.stripColor(weatherType);
        config.set("WeatherType", weatherType);
    }

    @Override
    protected String getSuccessMessageKey() {
        return "Weather.Created";
    }

    private boolean isValidWeatherType(String weatherType) {
        return weatherType.equals("sun") ||
                weatherType.equals("clear") ||
                weatherType.equals("storm") ||
                weatherType.equals("thunder") ||
                weatherType.equals("rain");
    }
}