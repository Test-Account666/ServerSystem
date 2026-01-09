package me.testaccount666.serversystem.managers.globaldata

import me.testaccount666.serversystem.managers.config.ConfigReader

object DefaultsData {
    private lateinit var _Home: Home

    fun initialize(config: ConfigReader) {
        _Home = Home(config)
    }

    @JvmStatic
    fun home(): Home = _Home

    class Home(config: ConfigReader) {
        val defaultMaxHomes: Int = config.getInt("DefaultValues.Home.MaxHomes", 0)
    }
}
