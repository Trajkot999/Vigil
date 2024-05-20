package me.trajkot.player;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class VigilPlayer {

    private final Player player;

    private final VigilNPCManager vigilNPCManager;

    public boolean sendNextAlert = false;

    public VigilPlayer(Player player) {
        this.player = player;
        this.vigilNPCManager = new VigilNPCManager(this);
    }
}