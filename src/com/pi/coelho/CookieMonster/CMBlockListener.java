package com.pi.coelho.CookieMonster;

import org.bukkit.Material;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;

public class CMBlockListener extends BlockListener {

    public CMBlockListener() {
    }

    public void onBlockBreak(BlockDamageEvent event) {
        if (event.getBlock().getType() == Material.MOB_SPAWNER) {
            CookieMonster.getRewardHandler().GivePlayerMobSpawnerCoinReward(event.getPlayer());
        }
    }
}
