package com.pi.coelho.CookieMonster;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class CMBlockListener implements Listener {

	public CMBlockListener() {
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER) {
			CookieMonster.getRewardHandler().GivePlayerMobSpawnerCoinReward(event.getPlayer());
			final ItemStack[] newDrops = CookieMonster.getRewardHandler().getMSDropReward();
			if (newDrops != null) {
				for (ItemStack i : newDrops) {
					event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), i);
				}
			}
		}
	}
}
