package me.trajkot.player;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VigilPlayerManager {
    private final Map<UUID, VigilPlayer> vigilPlayers = new HashMap<>();

    public VigilPlayer getVigilPlayer(final Player player) {
        return vigilPlayers.get(player.getUniqueId());
    }

    public void registerVigilPlayer(final Player player) {
        vigilPlayers.put(player.getUniqueId(), new VigilPlayer(player));
    }

    public void unregisterVigilPlayer(final Player player) {
        vigilPlayers.remove(player.getUniqueId());
    }
}