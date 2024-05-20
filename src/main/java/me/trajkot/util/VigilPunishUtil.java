package me.trajkot.util;

import lombok.experimental.UtilityClass;
import me.trajkot.Vigil;
import me.trajkot.player.VigilPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

@UtilityClass
public class VigilPunishUtil {

    public void punishVigilPlayer(final VigilPlayer vigilPlayer, final Vigil vigil) {
        if (!Vigil.vigilConfig.getConfig().getString("punishments.punishment-command").isEmpty()) {

            Bukkit.getScheduler().runTask(vigil, () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), ChatColor.translateAlternateColorCodes('&', Vigil.vigilConfig.getConfig().getString("punishments.punishment-command").replaceAll("%player%", vigilPlayer.getPlayer().getName()))));

            if(Vigil.vigilConfig.getConfig().getBoolean("punishments.broadcast-message-enabled")) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Vigil.vigilConfig.getConfig().getString("punishments.broadcast-message").replaceAll("%player%", vigilPlayer.getPlayer().getName())));
            }
        }
    }
}