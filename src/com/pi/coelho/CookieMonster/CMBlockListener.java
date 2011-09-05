package com.pi.coelho.CookieMonster;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;

public class CMBlockListener extends BlockListener {

    public CMBlockListener() {
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.MOB_SPAWNER) {
            if (!CookieMonster.getRewardHandler().canAffordMobSpawner(event.getPlayer())) {
                event.setCancelled(true);
            } else {
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
}
