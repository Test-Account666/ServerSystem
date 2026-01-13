package me.testaccount666.serversystem

import java.util.concurrent.ConcurrentHashMap

class ServiceRegistry {
    private val _services: MutableMap<Class<*>, Any> = ConcurrentHashMap()

    @Synchronized
    fun <T : Any> registerService(type: Class<T>, service: T): T {
        check(!hasService(type)) { "Service already registered: ${type.name}" }

        _services[type] = service
        return service
    }

    fun <T : Any> getService(type: Class<T>): T = type.cast(_services[type])

    fun <T : Any> getServiceOrNull(type: Class<T>): T? = _services[type]?.let { type.cast(it) }

    fun <T : Any> hasService(type: Class<T>): Boolean = type in _services

    @Synchronized
    fun clearServices() = _services.clear()

    inline fun <reified T : Any> registerService(service: T): T = registerService(T::class.java, service)
    inline fun <reified T : Any> getService(): T = getService(T::class.java)
    inline fun <reified T : Any> getServiceOrNull(): T? = getServiceOrNull(T::class.java)
    inline fun <reified T : Any> hasService(): Boolean = hasService(T::class.java)

}