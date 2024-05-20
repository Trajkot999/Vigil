package me.trajkot.listeners;

import me.trajkot.Vigil;
import me.trajkot.player.VigilPlayer;
import me.trajkot.util.VigilAlertUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class RegistrationListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Vigil.vigilPlayerManager.registerVigilPlayer(player);

        if(event.getPlayer().isOp() || event.getPlayer().hasPermission("vigil.alerts") || event.getPlayer().hasPermission("vigil.main")) {
            VigilAlertUtil.registerPlayerAlert(Vigil.vigilPlayerManager.getVigilPlayer(player));
        }

        Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().createNPC(Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().randomName(8), 13);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        VigilAlertUtil.unregisterPlayerAlert(Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()));
        Vigil.vigilPlayerManager.unregisterVigilPlayer(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        VigilPlayer vigilPlayer = Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer());
        if(vigilPlayer != null) {
            Bukkit.getScheduler().runTaskLater(Vigil.INSTANCE, () -> {
                vigilPlayer.getVigilNPCManager().bypassAntibotRemove();
            }, 10L);
        }
    }
}