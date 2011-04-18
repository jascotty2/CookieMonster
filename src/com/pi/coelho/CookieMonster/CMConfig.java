package com.pi.coelho.CookieMonster;

import com.jascotty2.item.MonsterDrops;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.entity.*;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class CMConfig {

    // Files and Directories
    public final static File pluginFolder = new File("plugins", CookieMonster.name);
    public final static File configfile = new File(pluginFolder, "config.yml");
    //public final static File configurationFile = new File("plugins" + File.pathSeparatorChar + "CookieMonster" + File.pathSeparatorChar + "config.yml");
    //public static String Plugin_Directory;
    //Creature Names (MUST be parallel to CreatureType.values())
    public final static String CreatureNodes[] = {
        "Chicken", "Cow", "Creeper", "Ghast", "Giant", "Monster", "Pig", "PigZombie",
        "Sheep", "Skeleton", "Slime", "Spider", "Squid", "Zombie", "Wolf", "MobSpawner"
    };
    //Monster Configuration
    public MonsterDrops Monster_Drop[] = new MonsterDrops[CreatureNodes.length];
    // settings
    public long damageTimeThreshold = 500; // if dies within this time of damage (ms), will reward killer
    public boolean intOnly = false,
            disableAnoymDrop = false,
            alwaysReplaceDrops = true,
            allowWolfHunt = true,
            disableExpensiveKill = true;
    // messages
    public static HashMap<String, String> messages = new HashMap<String, String>();

    public CMConfig() {
        for (int i = 0; i < Monster_Drop.length; ++i) {
            Monster_Drop[i] = new MonsterDrops();
        }
    }

    public boolean load() {
        extractConfig();
        messages.clear();
        messages.put("reward", "&a You are rewarded &f<amount>&a for killing the &f<monster>");
        messages.put("itemreward", "&a You are rewarded &f<amount>&a for killing the &f<monster> with a <item>");

        messages.put("penalty", "&c You are penalized &f<amount>&c for killing the &f<monster>");
        messages.put("itempenalty", "&c You are penalized &f<amount>&c for killing the &f<monster>&c with a &f<item>");

        messages.put("notafford", "&c You cannot afford to kill a &f<monster>");
        messages.put("itemnotafford", "&c You cannot afford to kill a &f<monster>&c with a &f<item>");

        messages.put("norewardMonster", "&c there is no reward for killing a &f<monster>");
        messages.put("norewardCreature", "");

        messages.put("itemnorewardMonster", "&c there is no reward for killing a &f<monster>&c with a &f<item>");
        messages.put("itemnorewardCreature", "");

        for (int i = 0; i < Monster_Drop.length; ++i) {
            Monster_Drop[i].useCustomDrops = false;
            Monster_Drop[i].setReward(0);
        }
        try {
            Configuration config = new Configuration(configfile);
            config.load();
            if (config.getNode("settings") != null) {
                ConfigurationNode n = config.getNode("settings");
                intOnly = n.getBoolean("wholeNumberRewards", intOnly);
                disableAnoymDrop = n.getBoolean("onlyKillDrop", disableAnoymDrop);
                allowWolfHunt = n.getBoolean("allowWolfHunt", allowWolfHunt);
                disableExpensiveKill = n.getBoolean("disableExpensiveKill", disableExpensiveKill);
                alwaysReplaceDrops = n.getBoolean("alwaysReplaceDrops", alwaysReplaceDrops);
            }
            if (config.getNodes("rewards") != null) {
                for (String k : config.getNodes("rewards").keySet()) {
                    int i = creatureIndex(k);
                    if (i >= 0) {
                        if (!Monster_Drop[i].setDrops(config.getString("rewards." + k + ".drops"))) {
                            CookieMonster.Log(Level.WARNING, k + " coin reward has an invalid value");
                        }
                        if (!Monster_Drop[i].setItemRewards(config.getString("rewards." + k + ".itemCoins"))) {
                            CookieMonster.Log(Level.WARNING, k + " item coin reward has an invalid value");
                        }
                        Monster_Drop[i].setReward(config.getString("rewards." + k + ".coins"));
                    } else {
                        CookieMonster.Log(Level.WARNING, "Invalid Entity Node: " + k);
                    }
                }
            } else {
                CookieMonster.Log(Level.SEVERE, "rewards node missing from config");
            }
            if (config.getNodes("messages") != null) {
                List<String> msgs = config.getKeys("messages");//.getNodes("messages").keySet();
                for (String k : msgs) {
                    if (!messages.containsKey(k)) {
                        CookieMonster.Log(Level.WARNING, "unused message setting: " + k);
                    } else {
                        messages.put(k, config.getString("messages." + k, messages.get(k)));
                    }
                }
                // check if there are any missing, and replace chatcolorcodes
                for (String k : messages.keySet()) {
                    if (!msgs.contains(k)) {
                        CookieMonster.Log(Level.WARNING, "missing message setting: " + k);
                    }
                }
            } else {
                CookieMonster.Log(Level.WARNING, "messages node missing from config");
            }
            // replace chatcolorcodes
            for (String k : messages.keySet()) {
                messages.put(k, messages.get(k).replaceAll("&&", "\b").replaceAll("&", "\u00A7").replaceAll("\b", "&"));
            }
            return true;
        } catch (Exception ex) {
            CookieMonster.Log(Level.SEVERE, "error loading configuration", ex);
        }
        return false;
    }

    public static String checkMonsters(LivingEntity le) {
        String name = "";
        if (le instanceof Chicken) {
            name = "Chicken";
        } else if (le instanceof Cow) {
            name = "Cow";
        } else if (le instanceof Creeper) {
            name = "Creeper";
        } else if (le instanceof Ghast) {
            name = "Ghast";
        } else if (le instanceof Giant) {
            name = "Giant";
        } else if (le instanceof Pig) {
            name = "Pig";
        } else if (le instanceof PigZombie) {
            name = "PigZombie";
        } else if (le instanceof Sheep) {
            name = "Sheep";
        } else if (le instanceof Skeleton) {
            name = "Skeleton";
        } else if (le instanceof Slime) {
            name = "Slime";
        } else if (le instanceof Spider) {
            name = "Spider";
        } else if (le instanceof Squid) {
            name = "Squid";
        } else if (le instanceof Zombie) {
            name = "Zombie";
        }
        return name;
    }

    public static int creatureIndex(Entity le) {
        if (le == null) {
            return -1;
        }
        if (le instanceof Chicken) {
            return 0;
        } else if (le instanceof Cow) {
            return 1;
        } else if (le instanceof Creeper) {
            return 2;
        } else if (le instanceof Ghast) {
            return 3;
        } else if (le instanceof Giant) {
            return 4;
        } else if (le instanceof Pig) {
            return 6;
        } else if (le instanceof PigZombie) {
            return 7;
        } else if (le instanceof Sheep) {
            return 8;
        } else if (le instanceof Skeleton) {
            return 9;
        } else if (le instanceof Slime) {
            return 10;
        } else if (le instanceof Spider) {
            return 11;
        } else if (le instanceof Squid) {
            return 12;
        } else if (le instanceof Zombie) {
            return 13;
        } else if (le instanceof Wolf) {
            return 14;
        } else if (le instanceof Monster) {
            return 5;
        }
        return -1;
    }

    public static int creatureIndex(String e) {
        if (e == null) {
            return -1;
        }
        for (int i = 0; i < CreatureNodes.length; ++i) {
            if (e.equalsIgnoreCase(CreatureNodes[i])) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isCreature(int i) {
        return i == 0 || i == 1 || i == 6 || i == 8 || i == 12 || i == 14;
    }

    public static boolean isMonster(int i) {
        return i == 2 || i == 3 || i == 4 || i == 5 || i == 7 || i == 9 || i == 10 || i == 11 || i == 13 || i == 14;
    }

    private static void extractConfig() {
        //(new File(configurationFile.getAbsolutePath().substring(
        //        0, configurationFile.getAbsolutePath().length() - configurationFile.getName().length()))).mkdirs();
        pluginFolder.mkdirs();

        if (!configfile.exists()) {
            InputStream input = CMConfig.class.getResourceAsStream("/config.yml");
            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(configfile);
                    byte[] buf = new byte[8192];
                    int length = 0;

                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }

                    System.out.println("[CookieMonster] Default setup file written: " + configfile);
                } catch (Exception e) {
                    CookieMonster.Log(Level.SEVERE, e);
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
}
