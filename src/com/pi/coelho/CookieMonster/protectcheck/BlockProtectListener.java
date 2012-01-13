/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pi.coelho.CookieMonster.protectcheck;

import com.pi.coelho.CookieMonster.CookieMonster;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

/**
 *
 * @author Jacob
 */
public class BlockProtectListener extends BlockListener {
	
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().getType() == Material.MOB_SPAWNER) {
            if (!CookieMonster.getRewardHandler().canAffordMobSpawner(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }
}
