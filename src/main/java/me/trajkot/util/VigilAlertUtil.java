package me.trajkot.util;

import lombok.experimental.UtilityClass;
import me.trajkot.player.VigilPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class VigilAlertUtil {
    private final List<VigilPlayer> vigilPlayerAlerts = new ArrayList<>();

    public void registerPlayerAlert(final VigilPlayer data) {
        vigilPlayerAlerts.add(data);
    }
    public void unregisterPlayerAlert(final VigilPlayer data) {
        vigilPlayerAlerts.remove(data);
    }

    public void sendVigilAlert(final VigilPlayer vigilPlayer, final String alertMessage, final int vl) {
        final TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&3VigilAC &7> " + alertMessage)
                .replaceAll("%player%", vigilPlayer.getPlayer().getName())
                .replaceAll("%vl%", Integer.toString(vl)));

        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + vigilPlayer.getPlayer().getName()));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                "&3Click to teleport to &b" + vigilPlayer.getPlayer().getName() + "&3.")).create()));

        vigilPlayerAlerts.forEach(p -> p.getPlayer().spigot().sendMessage(message));
    }
}