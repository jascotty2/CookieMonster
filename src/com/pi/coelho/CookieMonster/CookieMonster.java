package com.pi.coelho.CookieMonster;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CookieMonster extends JavaPlugin {

    protected final static Logger logger = Logger.getLogger("Minecraft");
    public static final String name = "CookieMonster";
    protected static CMConfig config = new CMConfig();
    private static Server server;
    private static CMBlockListener blockListener = null;
    private static CMEntityListener entityListener = null;
    private static CMRewardHandler rewardHandler = null;

    @Override
    public void onEnable() {
        
        // Grab plugin details
        server = getServer();
        PluginManager pm = server.getPluginManager();
        PluginDescriptionFile pdfFile = this.getDescription();

        // economic plugin
        if (!CMEcon.initEcon(server)) {
            Log(Level.WARNING, "Failed to find a supported economy plugin!");
        }

        // Directory
        getDataFolder().mkdir();
        getDataFolder().setWritable(true);
        getDataFolder().setExecutable(true);
        //CMConfig.Plugin_Directory = getDataFolder().getPath();

        // Configuration
        if (!config.load()) {
            server.getPluginManager().disablePlugin(this);
            Log(Level.SEVERE, "Failed to retrieve configuration from directory.");
            Log(Level.SEVERE, "Please back up your current settings and let CookieMonster recreate it.");
            return;
        }

        // Initializing Listeners
        entityListener = new CMEntityListener(getServer());
        blockListener = new CMBlockListener();
        rewardHandler = new CMRewardHandler();

        // Event Registration
        pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);

        // Console Detail
        Log(" v" + pdfFile.getVersion() + " loaded successfully.");
        Log(" Developed by: " + pdfFile.getAuthors());
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("cookiemonster")) {
            if (!sender.isOp()) {
                sender.sendMessage("You are not OP!");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!config.load()) {
                    sender.sendMessage("Reload Failed!");
                }
                sender.sendMessage("Settings Reloaded");
            } else {
                return false;
            }
        }
        return true;
    }

    public static Server getBukkitServer() {
        return server;
    }

    public static CMRewardHandler getRewardHandler() {
        return rewardHandler;
    }

    public static void Log(String txt) {
        logger.log(Level.INFO, String.format("[%s] %s", name, txt));
    }

    public static void Log(Level loglevel, String txt) {
        Log(loglevel, txt, true);
    }

    public static void Log(Level loglevel, String txt, boolean sendReport) {
        logger.log(loglevel, String.format("[%s] %s", name, txt == null ? "" : txt));
    }

    public static void Log(Level loglevel, String txt, Exception params) {
        if (txt == null) {
            Log(loglevel, params);
        } else {
            logger.log(loglevel, String.format("[%s] %s", name, txt == null ? "" : txt), (Exception) params);
        }
    }

    public static void Log(Level loglevel, Exception err) {
        logger.log(loglevel, String.format("[%s] %s", name, err == null ? "? unknown exception ?" : err.getMessage()), err);
    }
}
