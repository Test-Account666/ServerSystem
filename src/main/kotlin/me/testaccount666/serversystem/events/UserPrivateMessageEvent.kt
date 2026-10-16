package me.testaccount666.serversystem.events

import me.testaccount666.paperktx.events.KtxEventCancellable
import me.testaccount666.serversystem.userdata.User

class UserPrivateMessageEvent(val sender: User, val message: String, vararg recipients: User) : KtxEventCancellable() {
    val recipients = hashSetOf(sender, *recipients)
}
