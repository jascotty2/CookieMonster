/**
 * Programmer: Jacob Scott
 * Program Name: CMRegions
 * Description: tracks all regions
 * Date: Jul 14, 2011
 */
package com.pi.coelho.CookieMonster;

import com.jascotty2.CheckInput;
import java.io.File;
import org.bukkit.Server;
import com.sk89q.wg_regions.CuboidRegion;
import com.sk89q.wg_regions.PolygonalRegion;
import com.sk89q.wg_regions.Region;
import com.sk89q.wg_regions.managers.RegionManager;
import com.sk89q.wg_regions.managers.GlobalRegionManager;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author jacob
 */
public class CMRegions {

    File loadedFolder = null;
    protected final GlobalRegionManager globalRegionManager;

    public CMRegions(Server server, File dataFolder) {
        loadedFolder = dataFolder;
        globalRegionManager = new GlobalRegionManager(server, dataFolder);
    } // end default constructor

    public void load() {
        globalRegionManager.preload();
    }

    public boolean define(Player pl, String args[], Selection sel) {

        if (sel == null) {
            pl.sendMessage("Select a region first");
            return false;
        } else if (args.length != 3) {
            pl.sendMessage("Error parsing id: incorrect # of args");
            return false;
        }

        String id = args[2];

        if (!Region.isValidId(id)) {
            pl.sendMessage("Invalid region ID specified!");
            return false;
        } else if (id.equalsIgnoreCase("__global__")) {
            pl.sendMessage("A region cannot be named __global__");
            return false;
        }

        Region region;

        // Detect the type of region from WorldEdit
        if (sel instanceof Polygonal2DSelection) {
            Polygonal2DSelection polySel = (Polygonal2DSelection) sel;
            int minY = polySel.getNativeMinimumPoint().getBlockY();
            int maxY = polySel.getNativeMaximumPoint().getBlockY();
            region = new PolygonalRegion(id, polySel.getNativePoints(), minY, maxY);
        } else if (sel instanceof CuboidSelection) {
            BlockVector min = sel.getNativeMinimumPoint().toBlockVector();
            BlockVector max = sel.getNativeMaximumPoint().toBlockVector();
            region = new CuboidRegion(id, min, max);
        } else {
            pl.sendMessage(ChatColor.RED + "The type of region selected in WorldEdit is unsupported!");
            return false;
        }

        RegionManager mgr = globalRegionManager.get(sel.getWorld());
        mgr.addRegion(region);

        try {
            mgr.save();
            pl.sendMessage(ChatColor.YELLOW + "Region saved as " + id + ".");
        } catch (IOException e) {
            pl.sendMessage(ChatColor.RED + "Failed to write regions file: " + e.getMessage());
        }
        return true;
    }

    public void list(CommandSender sender, String args[]) {
        if (args.length < 2 || args.length > 3) {
            sender.sendMessage("Error parsing command: incorrect # of args");
            return;
        }
        int listSize = 10;

        int page = args.length == 3 ? CheckInput.GetInt(args[2], 0) - 1 : 0;

        if (sender instanceof Player) {
            World world = ((Player) sender).getWorld();

            RegionManager mgr = globalRegionManager.get(world);
            Map<String, Region> regions = mgr.getRegions();

            int size = regions.size();

            String[] regionIDList = regions.keySet().toArray(new String[0]);
            Arrays.sort(regionIDList);

            int pages = (int) Math.ceil(size / (float) listSize);

            sender.sendMessage(ChatColor.AQUA
                    + world.getName() + " Regions (page " + (page + 1) + " of " + pages + "):");

            if (page < pages) {
                for (int i = page * listSize; i < page * listSize + listSize; i++) {
                    if (i >= size) {
                        break;
                    }
                    sender.sendMessage(ChatColor.YELLOW.toString() + (i + 1)
                            + ". " + regionIDList[i]);
                }
            }
        } else {

            Map<String, Map<String, Region>> regions = new TreeMap<String, Map<String, Region>>();

            for (World w : sender.getServer().getWorlds()) {
                RegionManager mgr = globalRegionManager.get(w);
                //regions.putAll(mgr.getRegions());
                regions.put(w.getName(), mgr.getRegions());
            }

            int size = 0;//regions.size();
            for (String w : regions.keySet()) {
                size += regions.get(w).size();
            }

            int i = 0;
            String[] regionIDList = new String[size];
            for (String w : regions.keySet()) {
                for (String r : regions.get(w).keySet()) {
                    regionIDList[i++] = w + ":" + r;
                }
            }
            Arrays.sort(regionIDList);

            int pages = (int) Math.ceil(size / (float) listSize);

            sender.sendMessage(ChatColor.AQUA
                    + "Server Regions (page " + (page + 1) + " of " + pages + "):");

            if (page < pages) {
                for (i = page * listSize; i < page * listSize + listSize; i++) {
                    if (i >= size) {
                        break;
                    }
                    sender.sendMessage(ChatColor.YELLOW.toString() + (i + 1)
                            + ". " + regionIDList[i]);
                }
            }
        }
    }

    public void remove(CommandSender sender, String args[]) {
        if (args.length < 2 || args.length > 3) {
            sender.sendMessage("Error parsing command: incorrect # of args");
            return;
        }

        String id = args[2];

        if (sender instanceof Player) {
            remove(sender, ((Player) sender).getWorld(), id);
        } else {
            String worldname;
            if (id.contains(":") && id.indexOf(":") == id.lastIndexOf(":")) {
                worldname = id.substring(0, id.indexOf(":"));
                id = id.substring(id.indexOf(":") + 1);
            } else {
                sender.sendMessage("must specify world (world:region)");
                return;
            }
            World world = null;

            for (World w : sender.getServer().getWorlds()) {
                if (w.getName().equalsIgnoreCase(worldname)) {
                    world = w;
                    break;
                }
            }

            if (world == null) {
                sender.sendMessage("world not found");
                return;
            }

            remove(sender, world, id);
        }
    }

    private void remove(CommandSender sender, World world, String id) {
        RegionManager mgr = globalRegionManager.get(world);
        Region region = mgr.getRegion(id);

        if (region == null) {
            sender.sendMessage("Could not find a region by that ID.");
            return;
        }

        mgr.removeRegion(id);

        sender.sendMessage(ChatColor.YELLOW + "Region '" + id + "' removed.");

        try {
            mgr.save();
        } catch (IOException e) {
            sender.sendMessage("Failed to write regions file: " + e.getMessage());
        }
    }
} // end class CMRegions

