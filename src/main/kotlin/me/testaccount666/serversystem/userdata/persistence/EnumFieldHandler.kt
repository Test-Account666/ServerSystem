package me.testaccount666.serversystem.userdata.persistence

import org.bukkit.configuration.file.FileConfiguration

class EnumFieldHandler<T : Enum<T>> : FieldHandler<T> {

    override fun save(config: FileConfiguration, path: String, value: T?) {
        config.set(path, value?.name)
    }

    override fun load(config: FileConfiguration, path: String, defaultValue: T?): T {
        requireNotNull(defaultValue) { "Default value must not be null for enum fields!" }

        val enumName = config.getString(path) ?: return defaultValue
        return try {
            val field = defaultValue::class.java.enumConstants.find { it.name.equals(enumName, true) }

            requireNotNull(field) { "Invalid enum value '$enumName' for field '$path'!" }

            return field
        } catch (exception: Exception) {
            exception.printStackTrace()
            defaultValue
        }
    }
}
