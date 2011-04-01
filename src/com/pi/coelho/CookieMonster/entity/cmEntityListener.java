package com.pi.coelho.CookieMonster.entity;

import java.util.HashMap;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.pi.coelho.CookieMonster.CookieMonster;

public class cmEntityListener extends EntityListener {

    private HashMap<Integer, Long> recent = new HashMap<Integer, Long>();

    public cmEntityListener() {
    }

    @Override
    public void onEntityDamage(EntityDamageEvent entEvent) {
        if (entEvent.isCancelled()) {
            return;
        }

        if (entEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entEvent;
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Monster) {
                Monster monster = (Monster) event.getEntity();
                Player player = (Player) event.getDamager();
                if (event.getDamage() >= monster.getHealth()) {
                    doMonsterDeath(monster, player);
                }

            }
        } else if (entEvent instanceof EntityDamageByProjectileEvent) {
            EntityDamageByProjectileEvent event = (EntityDamageByProjectileEvent) entEvent;
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Monster) {
                Monster monster = (Monster) event.getEntity();
                Player player = (Player) event.getDamager();
                if (event.getDamage() >= monster.getHealth()) {
                    doMonsterDeath(monster, player);
                }

            }
        }
    }

    public void doMonsterDeath(Monster monster, Player player) {
        if (!(recent.containsKey(monster.getEntityId())
                && recent.get(monster.getEntityId()) + 5000 > System.currentTimeMillis())) {
            if (monster instanceof Creeper) {
                CookieMonster.getRewardHandler().GivePlayerReward(player, 0);
            } else if (monster instanceof Skeleton) {
                CookieMonster.getRewardHandler().GivePlayerReward(player, 1);
            } else if (monster instanceof Zombie) {
                CookieMonster.getRewardHandler().GivePlayerReward(player, 2);
            } else if (monster instanceof Spider) {
                CookieMonster.getRewardHandler().GivePlayerReward(player, 3);
            } else if (monster instanceof Slime) {
                CookieMonster.getRewardHandler().GivePlayerReward(player, 4);
            } else if (monster instanceof PigZombie) {
                CookieMonster.getRewardHandler().GivePlayerReward(player, 5);
            } else if (monster instanceof Ghast) {
                CookieMonster.getRewardHandler().GivePlayerReward(player, 6);
            } else if (monster instanceof Giant) {
                CookieMonster.getRewardHandler().GivePlayerReward(player, 7);
            }

            if (recent.containsKey(monster.getEntityId())) {
                recent.remove(monster.getEntityId());
            }
            recent.put(monster.getEntityId(), System.currentTimeMillis());
        }
    }
}
