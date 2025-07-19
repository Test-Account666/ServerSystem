package me.testaccount666.serversystem.userdata.vanish;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@Setter
public class VanishData {
    protected boolean canDrop;
    protected boolean canPickup;
    protected boolean canInteract;
    protected boolean canMessage;

    public VanishData(boolean canDrop, boolean canPickup, boolean canInteract, boolean canMessage) {
        this.canDrop = canDrop;
        this.canPickup = canPickup;
        this.canInteract = canInteract;
        this.canMessage = canMessage;
    }
}
