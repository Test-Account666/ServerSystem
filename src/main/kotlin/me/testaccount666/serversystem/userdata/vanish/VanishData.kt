package me.testaccount666.serversystem.userdata.vanish

import lombok.experimental.Accessors

@Accessors(fluent = true)
class VanishData(
    var canDrop: Boolean,
    var canPickup: Boolean,
    var canInteract: Boolean,
    var canMessage: Boolean
) {
    //TODO: Remove after Kotlin migration!!!!
    fun canDrop(): Boolean = this.canDrop

    fun canPickup(): Boolean = this.canPickup

    fun canInteract(): Boolean = this.canInteract

    fun canMessage(): Boolean = this.canMessage

    fun canDrop(canDrop: Boolean): VanishData {
        this.canDrop = canDrop
        return this
    }

    fun canPickup(canPickup: Boolean): VanishData {
        this.canPickup = canPickup
        return this
    }

    fun canInteract(canInteract: Boolean): VanishData {
        this.canInteract = canInteract
        return this
    }

    fun canMessage(canMessage: Boolean): VanishData {
        this.canMessage = canMessage
        return this
    }
}