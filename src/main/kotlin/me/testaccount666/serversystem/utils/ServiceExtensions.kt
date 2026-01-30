package me.testaccount666.serversystem.utils

import me.testaccount666.serversystem.ServerSystem
import org.bukkit.event.Listener

object ServiceExtensions {
    inline fun <reified T : Any> Listener.getService(): T = ServerSystem.instance.registry.getService<T>()
    inline fun <reified T : Any> Listener.getServiceOrNull(): T? = ServerSystem.instance.registry.getServiceOrNull<T>()
}