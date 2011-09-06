/**
 * Programmer: Jacob Scott
 * Program Name: CMCampTracker
 * Description:
 * Date: Jun 7, 2011
 */
package com.pi.coelho.CookieMonster;

import com.jascotty2.util.HashMap3D;
import com.jascotty2.util.Point3D;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;

/**
 * @author jacob
 */
public class CMCampTracker {

    final HashMap<String, HashMap3D<KillGridSquare>> kills = new HashMap<String, HashMap3D<KillGridSquare>>();

    public CMCampTracker() {
        load();
    } // end default constructor

    public final void load() {
        // TODO ? save & load saved points & times between server stop/starts
    }

    public final void save() {
        // TODO ? save & load saved points & times between server stop/starts
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
        KillGridSquare pt = getPoint(worldName, x, y, z, true);
        pt.newKill();
    }

    /**
     * count how many kills within a square within a period of time <br>
     * also will truncate older records
     * @param loc location at center
     * @param dx distance from the center of the square
     * @param dy max. deviation in height
     * @param dt max time ago the kills occurred (milliseconds)
     * @return total number of kills in the area
     */
    public int numKills(Location loc, int dx, int dy, long dt) {
        if (kills.containsKey(loc.getWorld().getName())) {
            return numkills(loc.getWorld().getName(),
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                    dx, dy, dt);
        } else {
            return 0;
        }
    }

    private int numkills(String worldName, int ix, int iy, int iz, int dx, int dy, long dt) {
        int num = 0;
        for (int x = ix - dx; x < ix + dx; ++x) {
            for (int z = iz - dx; z < iz + dx; ++z) {
                for (int y = iy - dy; y < iy + dy; ++y) {
                    KillGridSquare s = getPoint(worldName, x, y, z, false);
                    if (s != null) {
                        s.truncate(dt);
                        num += s.numKills();
                    }
                }
            }
        }
        return num;
    }

    /**
     * count how many kills within a square in the database
     * @param loc center of square to check
     * @param dx distance from the center of the square
     * @param dy max. deviation in height
     * @return total number of kills in the area
     */
    public int numKills(Location loc, int dx, int dy) {
        if (kills.containsKey(loc.getWorld().getName())) {
            return numkills(loc.getWorld().getName(),
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), dx, dy);
        } else {
            return 0;
        }
    }

    private int numkills(String worldName, int ix, int iy, int iz, int dx, int dy) {
        int num = 0;
        for (int x = ix - dx; x < ix + dx; ++x) {
            for (int z = iz - dx; z < iz + dx; ++z) {
                for (int y = iy - dy; y < iy + dy; ++y) {
                    KillGridSquare s = getPoint(worldName, x, y, z, false);
                    if (s != null) {
                        num += s.numKills();
                    }
                }
            }
        }
        return num;
    }

    /**
     * remove old records from the database
     * @param maxAge the max age (milliseconds)
     * @return number of records removed
     */
    public int truncate(long maxAge) {
        int n = 0;
        for (String w : kills.keySet()) {
            n += truncate(w, maxAge);
        }
        return n;
    }

    /**
     * remove old records from the database
     * @param worldName world to truncate
     * @param maxAge the max age (milliseconds)
     * @return number of records removed
     */
    public int truncate(String worldName, long maxAge) {
        int removed = 0;
        if (kills.containsKey(worldName)) {
            HashMap3D<KillGridSquare> world = kills.get(worldName);
            ArrayList<Point3D> keys = world.allPoints();
            for (Point3D p : keys) {
                removed += world.get(p).truncate(maxAge);
            }
        }
        return removed;
    }

    private KillGridSquare getPoint(String worldName, int x, int y, int z, boolean create) {
        if (!kills.containsKey(worldName)) {
            kills.put(worldName, new HashMap3D<KillGridSquare>());
        }
        if (!kills.get(worldName).contains(x, y, z)) {
            if (create) {
                KillGridSquare n = new KillGridSquare();
                kills.get(worldName).put(x, y, z, n);
                return n;
            } else {
                return null;
            }
        } else {
            return kills.get(worldName).get(x, y, z);
        }
    }
} // end class CMCampTracker

class KillGridSquare {

    final ArrayList<KillRecord> kills = new ArrayList<KillRecord>();

    public int numKills() {
        return kills.size();
    }

    public int truncate(long maxAge) {
        int n = 0;
        for (int i = 0; i < kills.size(); ++i) {
            if (kills.get(i).age() > maxAge) {
                kills.remove(i);
                --i;
                ++n;
            }
        }
        return n;
    }

    public void newKill() { // int x, int y, int z) {
        kills.add(new KillRecord()); // x, y, z));
    }
}

class KillRecord {

    //public int x, y, z;
    public long time;

    public KillRecord() { //int x, int y, int z) {
        //this.x = x;
        //this.y = y;
        //this.z = z;
        this.time = System.currentTimeMillis();
    }

    public long age() {
        return System.currentTimeMillis() - time;
    }
}
