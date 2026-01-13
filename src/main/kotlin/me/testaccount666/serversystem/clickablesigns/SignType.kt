package me.testaccount666.serversystem.clickablesigns

import me.testaccount666.serversystem.clickablesigns.executables.give.ActionGiveSign
import me.testaccount666.serversystem.clickablesigns.executables.give.ConfiguratorGiveSign
import me.testaccount666.serversystem.clickablesigns.executables.kit.ActionKitSign
import me.testaccount666.serversystem.clickablesigns.executables.kit.ConfiguratorKitSign
import me.testaccount666.serversystem.clickablesigns.executables.time.ActionTimeSign
import me.testaccount666.serversystem.clickablesigns.executables.time.ConfiguratorTimeSign
import me.testaccount666.serversystem.clickablesigns.executables.warp.ActionWarpSign
import me.testaccount666.serversystem.clickablesigns.executables.warp.ConfiguratorWarpSign
import me.testaccount666.serversystem.clickablesigns.executables.weather.ActionWeatherSign
import me.testaccount666.serversystem.clickablesigns.executables.weather.ConfiguratorWeatherSign

enum class SignType(private val _key: String, signName: String, clickAction: SignClickAction, configurator: SignConfigurator) {
    GIVE("Give", "&#3F3FD1[Give]", ActionGiveSign(), ConfiguratorGiveSign()),
    KIT("Kit", "&#3F3FD1[KIT]", ActionKitSign(), ConfiguratorKitSign()),
    WARP("Warp", "&#3F3FD1[WARP]", ActionWarpSign(), ConfiguratorWarpSign()),
    TIME("Time", "&#3F3FD1[TIME]", ActionTimeSign(), ConfiguratorTimeSign()),
    WEATHER("Weather", "&#3F3FD1[WEATHER]", ActionWeatherSign(), ConfiguratorWeatherSign());

    private val _signName: String = signName
    private val _clickAction = clickAction
    private val _configurator: SignConfigurator = configurator

    /*TODO: REMOVE THIS AFTER KOTLIN MIGRATION!!!!*/
    fun key(): String {
        return _key
    }

    fun signName(): String {
        return _signName
    }

    fun clickAction(): SignClickAction {
        return _clickAction
    }

    fun configurator(): SignConfigurator {
        return _configurator
    }

    /*TODO END: REMOVE THIS AFTER KOTLIN MIGRATION!!!!*/

    companion object {
        fun getSignTypeByKey(key: String): SignType? {
            for (signType in entries) {
                if (!signType._key.equals(key, ignoreCase = true)) continue
                return signType
            }

            return null
        }
    }
}
