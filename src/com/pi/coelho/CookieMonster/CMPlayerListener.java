/**
 * Programmer: Jacob Scott
 * Program Name: CMPlayerListener
 * Description:
 * Date: Aug 17, 2011
 */
package com.pi.coelho.CookieMonster;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class CMPlayerListener implements Listener {

	final Map<String, Long> spawns = new HashMap<String, Long>();
	final Map<Player, ItemStack[]> spawnInv = new HashMap<Player, ItemStack[]>();
	final Map<Player, Integer> spawnExp = new HashMap<Player, Integer>();

	public CMPlayerListener() {
	} // end default constructor

	public void playerSpawn(Player p) {
		if (p != null) {
			//System.out.println("spawn: " + p.getName());
			spawns.put(p.getName(), System.currentTimeMillis());
		}
	}

	public long playerLifeLength(Player p) {
		return (p != null && spawns.containsKey(p.getName()))
				? System.currentTimeMillis() - spawns.get(p.getName()) : -1;
	}

	public long playerLifeLength(Entity e) {
		return (e != null && e instanceof Player
				&& spawns.containsKey(((Player) e).getName()))
				? System.currentTimeMillis() - spawns.get(((Player) e).getName()) : -1;
	}

	/**
	 * set this player's current inventory to not be lost on death
	 */
	public void addRespawnInv(Player p) {
		if (p != null) {
			ItemStack[] is = p.getInventory().getContents();
			ItemStack[] cpy = new ItemStack[is.length];
			for (int i = 0; i < is.length; ++i) {
				cpy[i] = is[i] == null ? null : is[i].clone();
			}
			spawnInv.put(p, cpy);
			spawnExp.put(p, p.getTotalExperience());
			//System.out.println("die exp: " + p.getExp() + " , " + p.getExperience() + " , " + p.getTotalExperience());
			//System.out.println(p.getName() + " has inv saved..");
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		playerSpawn(event.getPlayer());
		if (spawnInv.containsKey(event.getPlayer())) {
			event.getPlayer().getInventory().setContents(spawnInv.get(event.getPlayer()));
			//System.out.println("resp exp: " + event.getPlayer().getExp() + " , " + event.getPlayer().getExperience() + " , " + event.getPlayer().getTotalExperience());
			event.getPlayer().setTotalExperience(spawnExp.get(event.getPlayer()));
			//event.getPlayer().updateInventory();
			//System.out.println("resp exp: " + event.getPlayer().getExp() + " , " + event.getPlayer().getExperience() + " , " + event.getPlayer().getTotalExperience());
			spawnInv.remove(event.getPlayer());
			//spawnExp.remove(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		playerSpawn(event.getPlayer());
		//System.out.println("join exp: " + event.getPlayer().getExp() + " , " + event.getPlayer().getExperience() + " , " + event.getPlayer().getTotalExperience());
		//event.getPlayer().giveExp(100);
		//System.out.println("join exp: " + event.getPlayer().getExp() + " , " + event.getPlayer().getExperience() + " , " + event.getPlayer().getTotalExperience());
	}
} // end class CMPlayerListener

