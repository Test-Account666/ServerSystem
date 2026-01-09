package me.testaccount666.serversystem.placeholderapi

import me.testaccount666.serversystem.placeholderapi.executables.PlaceholderExpansionWrapper

class PlaceholderApiSupport private constructor() {
    init {
        throw IllegalStateException("Utility class cannot be instantiated")
    }

    companion object {
        private var _Wrapper: PlaceholderExpansionWrapper? = null

        val isPlaceholderApiInstalled: Boolean
            get() {
                try {
                    Class.forName("me.clip.placeholderapi.PlaceholderAPI")
                    return true
                } catch (throwable: Throwable) {
                    return false
                }
            }

        fun registerPlaceholders() {
            if (!isPlaceholderApiInstalled) return

            if (_Wrapper != null) unregisterPlaceholders()

            _Wrapper = PlaceholderExpansionWrapper()
            _Wrapper!!.register()
        }

        fun unregisterPlaceholders() {
            if (!isPlaceholderApiInstalled) return

            if (_Wrapper == null) return

            _Wrapper!!.unregister()
            _Wrapper = null
        }
    }
}
