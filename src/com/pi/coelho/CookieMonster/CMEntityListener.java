package com.pi.coelho.CookieMonster;

import com.jynxdaddy.wolfspawn_04.UpdatedWolf;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class CMEntityListener extends EntityListener {

    protected HashMap<Integer, MonsterAttack> attacks = new HashMap<Integer, MonsterAttack>();
    Server sv = null;

    public CMEntityListener(Server bukkitServer) {
        sv = bukkitServer;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent entEvent) {
        if (entEvent.isCancelled()) {
            return;
        }

        if (entEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entEvent;
            entDamage(event.getEntity(), event.getDamager(), entEvent);
        } else if (entEvent instanceof EntityDamageByProjectileEvent) {
            EntityDamageByProjectileEvent event = (EntityDamageByProjectileEvent) entEvent;
            entDamage(event.getEntity(), event.getDamager(), entEvent);
        } else {
            monsterDamaged(entEvent.getEntity(), null);
        }
    }

    void entDamage(Entity monster, Entity damager, EntityDamageEvent entEvent) {
        if (monster instanceof LivingEntity) {
            //System.out.println(damager);
            Player pl = null;
            if (damager instanceof Player) {
                pl = (Player) damager;
            } else if (damager instanceof Wolf && CookieMonster.config.allowWolfHunt && sv != null) {
                UpdatedWolf w = new UpdatedWolf((Wolf) damager);
                //System.out.println(w);
                if (w.isTame()) {
                    pl = sv.getPlayer(w.getOwner());
                }
            }
            if (pl == null) {
                monsterDamaged(monster, pl);
            } else {
                if (CookieMonster.config.disableExpensiveKill) {
                    int c = CMConfig.creatureIndex(monster);
                    if (c >= 0 && !CookieMonster.getRewardHandler().canAffordKill(pl, monster)) {
                        entEvent.setCancelled(true);
                        return;
                    }
                }
                monsterDamaged(monster, pl);
            }
        }
    }

    public void monsterDamaged(Entity monster, Player player) {
        if (!attacks.containsKey(monster.getEntityId())) {
            if (player != null) {
                attacks.put(monster.getEntityId(), new MonsterAttack(player));
            }
        } else {
            attacks.get(monster.getEntityId()).setAttack(player);
        }
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }
        if (CookieMonster.config.disableAnoymDrop) {
            if(!attacks.containsKey(event.getEntity().getEntityId()))
            event.getDrops().clear();
        } else if (CookieMonster.config.alwaysReplaceDrops) {
            ItemStack newDrops[] = CookieMonster.getRewardHandler().getDropReward(event.getEntity());
            if (newDrops != null) {
                event.getDrops().clear();
                event.getDrops().addAll(Arrays.asList(newDrops));
            }
        }
        if (event.getEntity() instanceof LivingEntity
                && attacks.containsKey(event.getEntity().getEntityId())) {
            if (attacks.get(event.getEntity().getEntityId()).attackTimeAgo()
                    <= CookieMonster.config.damageTimeThreshold) {
                attacks.get(event.getEntity().getEntityId()).rewardKill(event);
            }
            attacks.remove(event.getEntity().getEntityId());
        }
    }

    public class MonsterAttack {

        long lastAttackTime;
        Player lastAttackPlayer;

        public MonsterAttack(Player attacker) {
            setAttack(attacker);
        }

        public long attackTimeAgo() {
            return lastAttackTime > 0 ? System.currentTimeMillis() - lastAttackTime : 0;
        }

        public final void setAttack(Player attacker) {
            lastAttackPlayer = attacker;
            lastAttackTime = System.currentTimeMillis();
        }

        public void rewardKill(EntityDeathEvent event) {
            if (lastAttackPlayer != null) {
                CookieMonster.getRewardHandler().GivePlayerCoinReward(lastAttackPlayer, event.getEntity());
                ItemStack newDrops[] = CookieMonster.getRewardHandler().getDropReward(event.getEntity());
                if (newDrops != null) {
                    event.getDrops().clear();
                    event.getDrops().addAll(Arrays.asList(newDrops));
                }
            }
        }
    }
}
