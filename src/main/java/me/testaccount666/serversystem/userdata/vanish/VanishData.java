package me.testaccount666.serversystem.userdata.vanish;

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

    public boolean canDrop() {
        return canDrop;
    }

    public void setCanDrop(boolean canDrop) {
        this.canDrop = canDrop;
    }

    public boolean canPickup() {
        return canPickup;
    }

    public void setCanPickup(boolean canPickup) {
        this.canPickup = canPickup;
    }

    public boolean canInteract() {
        return canInteract;
    }

    public void setCanInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }

    public boolean canMessage() {
        return canMessage;
    }

    public void setCanMessage(boolean canMessage) {
        this.canMessage = canMessage;
    }
}
