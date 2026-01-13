package me.testaccount666.serversystem.commands.executables.inventorysee.utils

import me.testaccount666.serversystem.commands.executables.inventorysee.online.CommandInventorySee
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor

/**
 * Abstract base class for inventory see listeners.
 * Extracts common functionality from ListenerInventorySee and ListenerOfflineInventorySee.
 */
abstract class AbstractInventorySeeListener {
    protected lateinit var _commandInventorySee: CommandInventorySee

    /**
     * Internal method to check if the listener can be registered.
     * This is called by the canRegister method in subclasses.
     * 
     * @param commands Set of available commands
     * @return true if the listener can be registered, false otherwise
     */
    protected fun internalCanRegister(commands: Set<ServerSystemCommandExecutor>): Boolean {
        _commandInventorySee = commands.firstOrNull { it is CommandInventorySee } as? CommandInventorySee ?: return false

        return additionalRegistrationChecks()
    }

    /**
     * Additional checks to be performed during registration.
     * Subclasses can override this method to add specific checks.
     * 
     * @return true if additional checks pass, false otherwise
     */
    protected open fun additionalRegistrationChecks(): Boolean = true
}
