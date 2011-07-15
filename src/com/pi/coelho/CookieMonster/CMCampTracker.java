/**
 * Programmer: Jacob Scott
 * Program Name: CMCampTracker
 * Description:
 * Date: Jun 7, 2011
 */
package com.pi.coelho.CookieMonster;

import com.jascotty2.util.HashMap3D;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;

/**
 * @author jacob
 */
public class CMCampTracker {

    HashMap<String, HashMap3D<KillGridSquare>> kills = new HashMap<String, HashMap3D<KillGridSquare>>();

    public CMCampTracker() {
        load();
    } // end default constructor

    public final void load() {
        // TODO ?
        // save & load saved points & times between server stop/starts
    }

    /**
     * adds a kill to the specified spot
     * @param loc 
     */
    public void addKill(Location loc) {
        if (loc != null) {
            addKill(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }
    }

    /**
     * adds a kill to the specified spot
     * @param worldName
     * @param x
     * @param y
     * @param z
     */
    public void addKill(String worldName, int x, int y, int z) {
        KillGridSquare pt = getPoint(worldName, x, y, z);
        pt.newKill(x, y, z);
    }

    private KillGridSquare getPoint(String worldName, int x, int y, int z) {
        if (!kills.containsKey(worldName)) {
            kills.put(worldName, new HashMap3D<KillGridSquare>());
        }
        if (!kills.get(worldName).contains(x, y, z)) {
            return kills.get(worldName).put(x, y, z, new KillGridSquare());
        } else {
            return kills.get(worldName).get(x, y, z);
        }
    }
} // end class CMCampTracker

class KillGridSquare {

    ArrayList<KillRecord> kills = new ArrayList<KillRecord>();

    public int numKills() {
        truncate();
        return kills.size();
    }

    public void truncate() {
        long maxAge = CookieMonster.config.campTrackingTimeout;
        for (int i = 0; i < kills.size(); ++i) {
            if (kills.get(i).age() > maxAge) {
                kills.remove(i);
                --i;
            }
        }
    }

    public void newKill(int x, int y, int z) {
        kills.add(new KillRecord(x, y, z));
    }
}

class KillRecord {

    public int x, y, z;
    public long time;

    public KillRecord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = System.currentTimeMillis();
    }

    public long age() {
        return System.currentTimeMillis() - time;
    }
}
