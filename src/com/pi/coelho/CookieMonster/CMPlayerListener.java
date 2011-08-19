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
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class CMPlayerListener extends PlayerListener {

	Map<String, Long> spawns = new HashMap<String, Long>();

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

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		playerSpawn(event.getPlayer());
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		playerSpawn(event.getPlayer());
	}
} // end class CMPlayerListener

