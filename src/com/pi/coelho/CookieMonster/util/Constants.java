package com.pi.coelho.CookieMonster.util;

import java.io.File;

import org.bukkit.util.config.Configuration;

public class Constants {

    // Files and Directories
    public static File Configuration;
    public static String Plugin_Directory;
    //Monster Names
    public final static String Monsters[] = {
        "Creeper", "Skeleton", "Zombie", "Spider", "Slime", "PigZombie", "Ghast", "Giant", "Spawner"
    };
    //Monster Configuration
    public static int Monster_Drop[][][] = new int[Monsters.length][][];
    public static double Monster_Coin_Minimum[] = new double[Monsters.length];
    public static double Monster_Coin_Maximum[] = new double[Monsters.length];

    public static void load(Configuration config) {
        String s = "";
        config.load();

        for (int i = 0; i < Monsters.length; i++) {
            loadMonsterDrops(config.getString("Monster." + Monsters[i] + ".Drops", s), i);
            Monster_Coin_Minimum[i] = config.getDouble("Monster." + Monsters[i] + ".Coin_Minimum", Monster_Coin_Minimum[i]);
            Monster_Coin_Maximum[i] = config.getDouble("Monster." + Monsters[i] + ".Coin_Maximum", Monster_Coin_Maximum[i]);
        }
    }

    private static void loadMonsterDrops(String s, int index) {
        String[] s1 = s.split(";");
        String[] s2;

        try {
            if (s1.length > 0) {
                Monster_Drop[index] = new int[s1.length][3];
                for (int i = 0; i < s1.length; i++) {
                    s2 = s1[i].split(":");
                    if (s2.length == 3) {
                        Monster_Drop[index][i][0] = Integer.parseInt(s2[0]);
                        Monster_Drop[index][i][1] = Integer.parseInt(s2[1]);
                        Monster_Drop[index][i][2] = Integer.parseInt(s2[2]);
                    }
                }
                return;
            }
        } catch (Exception e) {
            System.out.println("[CookieMonster] Failed to load monster drops for: " + Monsters[index]);
        }

        Monster_Drop[index] = null;
    }
}
