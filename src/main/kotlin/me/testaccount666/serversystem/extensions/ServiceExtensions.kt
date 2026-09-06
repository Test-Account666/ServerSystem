package me.testaccount666.serversystem.extensions

import me.testaccount666.serversystem.ServerSystem

inline fun <reified T : Any> getService(): T = ServerSystem.instance.registry.getService<T>()
inline fun <reified T : Any> getServiceOrNull(): T? = ServerSystem.instance.registry.getServiceOrNull<T>()
