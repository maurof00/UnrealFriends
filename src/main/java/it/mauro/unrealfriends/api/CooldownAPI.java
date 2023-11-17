package it.mauro.unrealfriends.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownAPI {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long cooldownDuration;

    public CooldownAPI(long cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
    }

    public boolean hasCooldown(UUID playerUUID) {
        if (cooldowns.containsKey(playerUUID)) {
            long lastUsage = cooldowns.get(playerUUID);
            long currentTime = System.currentTimeMillis();
            return currentTime - lastUsage < cooldownDuration;
        }
        return false;
    }

    public void setCooldown(UUID playerUUID) {
        cooldowns.put(playerUUID, System.currentTimeMillis());
    }
}

