package me.testaccount666.serversystem.moderation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractModerationManager {
    protected UUID ownerUuid;

    public AbstractModerationManager(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public abstract void addModeration(AbstractModeration moderation);

    public abstract void removeModeration(AbstractModeration moderation);

    public abstract List<AbstractModeration> getModerations();

    public boolean hasActiveModeration() {
        return getModerations().stream().anyMatch(moderation -> !moderation.isExpired());
    }

    public Optional<AbstractModeration> getActiveModeration() {
        return getModerations().stream().filter(moderation -> !moderation.isExpired()).findFirst();
    }
}
