package com.pi.coelho.CookieMonster.entity;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;

import com.pi.coelho.CookieMonster.CookieMonster;

public class cmBlockListener extends BlockListener {

    public cmBlockListener() {
    }

    public void onBlockBreak(BlockDamageEvent event) {
        if (event.getBlock().getType() == Material.MOB_SPAWNER) {
            Player player = event.getPlayer();

            CookieMonster.getRewardHandler().GivePlayerReward(player, 8);
        }
    }
}
