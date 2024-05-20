package me.trajkot;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.trajkot.util.VigilConfig;
import me.trajkot.listeners.PacketListener;
import me.trajkot.listeners.RegistrationListener;
import me.trajkot.player.VigilPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Vigil extends JavaPlugin {
    public static VigilPlayerManager vigilPlayerManager;
    public static ProtocolManager protocolManager;
    public static Vigil INSTANCE;
    public static VigilConfig vigilConfig;

    @Override
    public void onLoad() {
        INSTANCE = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        //getCommand("vigil").setExecutor(commandManager);
        vigilConfig = new VigilConfig();

        vigilPlayerManager = new VigilPlayerManager();
        Bukkit.getOnlinePlayers().forEach(player -> vigilPlayerManager.registerVigilPlayer(player));

        Bukkit.getServer().getPluginManager().registerEvents(new RegistrationListener(), this);
        new PacketListener(this).registerPacketListener();
    }

    @Override
    public void onDisable() {
        //vigilConfig = null;
    }
}