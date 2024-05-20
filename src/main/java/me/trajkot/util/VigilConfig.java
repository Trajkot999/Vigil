package me.trajkot.util;

import me.trajkot.Vigil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class VigilConfig {

    private FileConfiguration config;
    private static File configf;

    public VigilConfig() {
        createConfig();
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void createConfig() {
        configf = new File(Vigil.INSTANCE.getDataFolder(), "config.yml");

        if(!configf.exists()) {
            configf.getParentFile().mkdirs();
            Vigil.INSTANCE.saveResource( "config.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(configf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configf);
        try {
            config.load(configf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}