package me.testaccount666.serversystem.events

import me.testaccount666.serversystem.userdata.User
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class UserPrivateMessageEvent(val sender: User, val message: String, vararg recipients: User) :
    Event(), Cancellable {
    val recipients = HashSet<User>()
    private var cancelled = false

    init {
        this.recipients.addAll(recipients)
        this.recipients.add(sender)
    }

    override fun getHandlers(): HandlerList = handlerList

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }


    companion object {
        val handlerList: HandlerList = HandlerList()
    }
}
