package com.pi.coelho.CookieMonster;

import com.jascotty2.CheckInput;
import com.jascotty2.item.MonsterDrops;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftWolf;
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
		"Sheep", "Skeleton", "Slime", "Spider", "Squid", "Zombie", "Tame_Wolf", "MobSpawner",
		"Charged_Creeper", "Wild_Wolf", "Pet_Wolf", "Player"
	};
	//Monster Configuration
	public MonsterDrops Monster_Drop[] = new MonsterDrops[CreatureNodes.length];
	// settings
	public long damageTimeThreshold = 500; // if dies within this time of damage (ms), will reward killer
	public boolean intOnly = false,
			disableAnoymDrop = false,
			replaceDrops = true,
			alwaysReplaceDrops = true,
			allowWolfHunt = true,
			disableExpensiveKill = true,
			regionsDisable = true;
	protected ArrayList<String> disabledWorlds = new ArrayList<String>();
	// messages
	public static HashMap<String, String> messages = new HashMap<String, String>();
	// spawn camping settings
	public boolean campTrackingEnabled = false,
			disableCampingDrops = true,
			globalCampTrackingEnabled = false;
	public int deltaY = 5, deltaX = 20, campKills = 50;
	public long campTrackingTimeout = 20 * 60000;
	// PvP settings
	// for how long a player is 'protected' from spawn camping
	//		(player kill rewards are nulled)
	public long playerRewardWait = 60000;
	// if a player killed within the playerRewardWait period
	//		reward is reversed (in case of positive reward only)
	//		eg. player who kills the player pays amount
	public boolean playerReverseProtect = true,
			playerPaysReward = true; // if the players who die pay the killer (assuming has enough)

	public CMConfig() {
		for (int i = 0; i < Monster_Drop.length; ++i) {
			Monster_Drop[i] = new MonsterDrops();
		}
	}

	public boolean load() {
		extractConfig();
		disabledWorlds.clear();
		messages.clear();
		messages.put("reward", "&a You are rewarded &f<amount>&a for killing the &f<monster>");
		messages.put("itemreward", "&a You are rewarded &f<amount>&a for killing the &f<monster> with a <item>");
		messages.put("playerreward", "&a You are rewarded &f<amount>&a for killing the Player &f<player>");
		messages.put("itemplayerreward", "&a You are rewarded &f<amount>&a for killing the Player &f<player>&a with a &f<item>");

		messages.put("victimpay", "&f <player>&c took &f<amount>&c from you when you died");
		messages.put("victimprotection", "&f <player>&a payed you &f<amount>&a as penalty for killing you");

		messages.put("nocampingreward", "&a No more rewards avaliable for this area.. Try again later");

		messages.put("penalty", "&c You are penalized &f<amount>&c for killing the &f<monster>");
		messages.put("itempenalty", "&c You are penalized &f<amount>&c for killing the &f<monster>&c with a &f<item>");

		messages.put("playerpenalty", "&c You are penalized &f<amount>&c for killing Player &f<player>");
		messages.put("playercamppenalty", "&c You are penalized &f<amount>&c for killing &f<player>&c during spawn protection");
		messages.put("itemplayerpenalty", "&c You are penalized &f<amount>&c for killing &f<player>&c during spawn with a &f<item>");
		messages.put("itemplayercamppenalty", "&c You are penalized &f<amount>&c for killing &f<player>&c during spawn with a &f<item>");

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
				replaceDrops = n.getBoolean("replaceDrops", replaceDrops);
				alwaysReplaceDrops = n.getBoolean("alwaysReplaceDrops", alwaysReplaceDrops);
				regionsDisable = n.getBoolean("regionsDisable", regionsDisable);
				String dw = n.getString("disableWorlds");
				if (dw != null) {
					for (String w : dw.split(",")) {
						disabledWorlds.add(w.trim().toLowerCase());
					}
				}
				String t = n.getString("playerRewardWait");
				if (n != null) {
					playerRewardWait = CheckInput.GetBigInt_TimeSpanInSec(t, 'm').longValue() * 1000;
				}
				playerReverseProtect = n.getBoolean("playerReverseProtect", playerReverseProtect);
				playerPaysReward = n.getBoolean("playerPaysReward", playerPaysReward);
			}

			if (config.getNode("spwanCampTracking") != null) {
				ConfigurationNode n = config.getNode("spwanCampTracking");
				campTrackingEnabled = n.getBoolean("enabled", campTrackingEnabled);
				globalCampTrackingEnabled = n.getBoolean("global", globalCampTrackingEnabled);
				disableCampingDrops = n.getBoolean("disableDrops", disableCampingDrops);
				deltaY = n.getInt("deltaY", deltaY);
				deltaX = n.getInt("deltaX", deltaX);
				campKills = n.getInt("campKills", campKills);
				String t = n.getString("timeout");
				if (n != null) {
					campTrackingTimeout = CheckInput.GetBigInt_TimeSpanInSec(t, 'm').longValue() * 1000;
				}
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
				// check if there are any missing
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

	public boolean cmEnabled(Location l) {
		if (l != null) {
			boolean isRegion = CookieMonster.regions != null && CookieMonster.regions.globalRegionManager.hasRegion(l);
			if (!regionsDisable) { // regions are enabled areas
				return isRegion;
			} else { // regions are disabled on allowed worlds, enabled on disabled worlds
				String w = l.getWorld().getName().toLowerCase();
				boolean dw = disabledWorlds.contains(w);
				return dw == isRegion;
			}
		}
		return true;
	}

	public static String checkMonsters(LivingEntity le) {
		return checkMonsters(le, null);
	}

	public static String checkMonsters(LivingEntity le, Player p) {
		String name = "";
		if (le instanceof Chicken) {
			name = "Chicken";
		} else if (le instanceof Cow) {
			name = "Cow";
		} else if (le instanceof Creeper) {
			if (((Creeper) le).isPowered()) {
				name = "Charged_Creeper";
			} else {
				name = "Creeper";
			}
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
		} else if (le instanceof Wolf) {
			if (((Wolf) le).isTamed()) {
				if (p != null && ((CraftWolf) le).getOwner() == p) {
					name = "Pet_Wolf";
				} else {
					name = "Tame_Wolf";
				}
			} else {
				name = "Wild_Wolf";
			}
		} else if (le instanceof Monster) {
			return "Monster";
		}
		return name;
	}

	public static int creatureIndex(Entity le) {
		return creatureIndex(le, null);
	}

	public static int creatureIndex(Entity le, Player p) {
		if (le == null) {
			return -1;
		}
		if (le instanceof Chicken) {
			return 0;
		} else if (le instanceof Cow) {
			return 1;
		} else if (le instanceof Creeper) {
			if (((Creeper) le).isPowered()) {
				return 16;
			} else {
				return 2;
			}
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
			if (((Wolf) le).isTamed()) {
				if (p != null && ((CraftWolf) le).getOwner() == p) {
					return 18;
				} else {
					return 14;
				}
			} else {
				return 17;
			}
		} else if (le instanceof Player) {
			return 19;
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
		return i == 0 || i == 1 || i == 6 || i == 8 || i == 12 || i == 14 || i == 18;
	}

	public static boolean isMonster(int i) {
		return i == 2 || i == 3 || i == 4 || i == 5 || i == 7 || i == 9
				|| i == 10 || i == 11 || i == 13 || i == 14 || i == 16 || i == 17;
	}

	public static boolean isPlayer(int i) {
		return i == 19;
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
