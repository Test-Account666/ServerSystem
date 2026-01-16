package me.testaccount666.serversystem.managers

import me.testaccount666.serversystem.ServerSystem
import me.testaccount666.serversystem.managers.config.ConfigReader
import me.testaccount666.serversystem.managers.config.DefaultConfigReader
import me.testaccount666.serversystem.userdata.User
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.io.File
import java.nio.file.Path

object PermissionManager {
    private val _PERMISSION_FILE: File = Path.of("plugins", "ServerSystem", "permissions.yml").toFile()
    private lateinit var _configReader: ConfigReader

    fun initialize(plugin: Plugin) {
        _configReader = DefaultConfigReader(_PERMISSION_FILE, plugin)
    }

    @JvmStatic
    @JvmOverloads
    fun hasCommandPermission(commandSender: CommandSender, permissionPath: String, sendFailInfo: Boolean = true): Boolean {
        return hasPermission(commandSender, "Commands.${permissionPath}", sendFailInfo)
    }

    @JvmStatic
    @JvmOverloads
    fun hasCommandPermission(user: User, permissionPath: String?, sendFailInfo: Boolean = true): Boolean {
        return hasPermission(user.commandSender!!, "Commands.${permissionPath}", sendFailInfo)
    }

    @JvmStatic
    fun hasPermission(user: User, permissionPath: String?, sendFailInfo: Boolean): Boolean {
        return hasPermission(user.commandSender!!, permissionPath, sendFailInfo)
    }

    @JvmStatic
    @JvmOverloads
    fun hasPermission(commandSender: CommandSender, permissionPath: String?, sendFailInfo: Boolean = true): Boolean {
        if (!isPermissionRequired(permissionPath)) return true
        val permission = getPermission("${permissionPath}")

        return hasPermissionString(commandSender, permission, sendFailInfo)
    }

    @JvmOverloads
    fun hasPermissionString(commandSender: CommandSender, permission: String?, sendFailInfo: Boolean = true): Boolean {
        if (permission == null) return false
        val hasPermission = commandSender.hasPermission(permission)

        if (!hasPermission && sendFailInfo) ServerSystem.log.info("${commandSender.name} has failed a permission check! Permission: ${permission}")

        return hasPermission
    }

    private fun isPermissionRequired(permissionPath: String?): Boolean {
        val permissionPath = "Permissions.${permissionPath}.Required"

        return _configReader.getBoolean(permissionPath, true)
    }

    @JvmStatic
    fun getPermission(permissionPath: String): String? {
        val permissionPath = "Permissions.${permissionPath}.Value"

        val permission = _configReader.getString(permissionPath)

        if (permission == null) ServerSystem.log.warning("Permission '${permissionPath}' not found! (Denying permission)")

        return permission
    }
}