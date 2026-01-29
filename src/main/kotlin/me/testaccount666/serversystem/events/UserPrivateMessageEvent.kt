package me.testaccount666.serversystem.events

import me.testaccount666.serversystem.userdata.User
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class UserPrivateMessageEvent(val sender: User, val message: String, vararg recipients: User) : Event(), Cancellable {
    val recipients = hashSetOf(sender, *recipients)
    private var _cancelled = false

    override fun getHandlers() = handlerList

    override fun isCancelled() = _cancelled

    override fun setCancelled(cancel: Boolean) {
        _cancelled = cancel
    }


    companion object {
        @JvmStatic // Important!
        val handlerList = HandlerList()
    }
}
