package me.testaccount666.serversystem.commands.executables.inventorysee.utils;

import me.testaccount666.serversystem.commands.executables.inventorysee.online.CommandInventorySee;
import me.testaccount666.serversystem.commands.interfaces.ServerSystemCommandExecutor;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract base class for inventory see listeners.
 * Extracts common functionality from ListenerInventorySee and ListenerOfflineInventorySee.
 */
public abstract class AbstractInventorySeeListener {
    protected CommandInventorySee _commandInventorySee;

    /**
     * Internal method to check if the listener can be registered.
     * This is called by the canRegister method in subclasses.
     *
     * @param commands Set of available commands
     * @return true if the listener can be registered, false otherwise
     */
    protected boolean internalCanRegister(Set<ServerSystemCommandExecutor> commands) {
        var canRegister = new AtomicBoolean(false);

        commands.forEach(command -> {
            if (!(command instanceof CommandInventorySee foundCommand)) return;

            _commandInventorySee = foundCommand;
            canRegister.set(true);
        });

        return canRegister.get() && additionalRegistrationChecks();
    }

    /**
     * Additional checks to be performed during registration.
     * Subclasses can override this method to add specific checks.
     *
     * @return true if additional checks pass, false otherwise
     */
    protected boolean additionalRegistrationChecks() {
        return true;
    }
}
