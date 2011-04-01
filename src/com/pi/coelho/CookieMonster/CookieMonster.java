package com.pi.coelho.CookieMonster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.pi.coelho.CookieMonster.entity.cmBlockListener;
import com.pi.coelho.CookieMonster.entity.cmEntityListener;
import com.pi.coelho.CookieMonster.entity.cmRewardHandler;
import com.pi.coelho.CookieMonster.util.Constants;

public class CookieMonster extends JavaPlugin {

    private static Server Server;
    private static cmBlockListener blockListener;
    private static cmEntityListener entityListener;
    private static cmRewardHandler rewardHandler;

    @Override
    public void onEnable() {
        //Getting the server
        Server = getServer();

        // Directory
        getDataFolder().mkdir();
        getDataFolder().setWritable(true);
        getDataFolder().setExecutable(true);
        Constants.Plugin_Directory = getDataFolder().getPath();

        // Grab plugin details
        PluginManager pm = Server.getPluginManager();
        PluginDescriptionFile pdfFile = this.getDescription();

        // Default Files
        extractDefaultFile("CookieMonster.yml");

        // Configuration
        try {
            Constants.load(new Configuration(new File(getDataFolder(), "CookieMonster.yml")));
        } catch (Exception e) {
            Server.getPluginManager().disablePlugin(this);
            System.out.println("[CookieMonster] Failed to retrieve configuration from directory.");
            System.out.println("[CookieMonster] Please back up your current settings and let CookieMonster recreate it.");
            return;
        }

        // Initializing Listeners
        entityListener = new cmEntityListener();
        blockListener = new cmBlockListener();
        rewardHandler = new cmRewardHandler();

        // Event Registration
        pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);

        // Console Detail
        System.out.println("[iConomy] v" + pdfFile.getVersion() + " loaded successfully.");
        System.out.println("[iConomy] Developed by: " + pdfFile.getAuthors());
    }

    @Override
    public void onDisable() {
    }

    private void extractDefaultFile(String name) {
        File actual = new File(getDataFolder(), name);
        if (!actual.exists()) {
            InputStream input = this.getClass().getResourceAsStream("/default/" + name);
            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;

                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }

                    System.out.println("[CookieMonster] Default setup file written: " + name);
                } catch (Exception e) {
                    Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (Exception e) {
                    }
                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public static Server getBukkitServer() {
        return Server;
    }

    public static cmRewardHandler getRewardHandler() {
        return rewardHandler;
    }
}
