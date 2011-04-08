package com.pi.coelho.CookieMonster;

import com.nijiko.coelho.iConomy.iConomy;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class CMEntityListener extends EntityListener {

    protected HashMap<Integer, MonsterAttack> attacks = new HashMap<Integer, MonsterAttack>();

    public CMEntityListener() {
    }

    @Override
    public void onEntityDamage(EntityDamageEvent entEvent) {
        if (entEvent.isCancelled()) {
            return;
        }

        if (entEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entEvent;
            entDamage(event.getEntity(),
                    event.getDamager() instanceof Player ? (Player) event.getDamager() : null,
                    entEvent);
        } else if (entEvent instanceof EntityDamageByProjectileEvent) {
            EntityDamageByProjectileEvent event = (EntityDamageByProjectileEvent) entEvent;
            entDamage(event.getEntity(),
                    event.getDamager() instanceof Player ? (Player) event.getDamager() : null,
                    entEvent);
        } else {
            monsterDamaged(entEvent.getEntity(), null);
        }
    }

    void entDamage(Entity monster, Player player, EntityDamageEvent entEvent) {
        if (monster instanceof LivingEntity) {
            if (player == null) {
                monsterDamaged(monster, player);
            } else {
                if (CMConfig.disableExpensiveKill) {
                    int c = CMConfig.creatureIndex(monster);
                    if (c >= 0 && CMConfig.Monster_Drop[c].getMinCoin() < 0
                            && iConomy.getBank().getAccount(player.getName()).getBalance()
                            < -CMConfig.Monster_Drop[c].getMinCoin()) {
                        //if (!attacks.containsKey(monster.getEntityId())) {
                        player.sendMessage("You cannot afford to kill a " + CMConfig.CreatureNodes[c]);
                        entEvent.setCancelled(true);
                        return;
                        //}
                    }
                }
                monsterDamaged(monster, player);
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
        if (event.getEntity() instanceof LivingEntity
                && attacks.containsKey(event.getEntity().getEntityId())) {
            if (attacks.get(event.getEntity().getEntityId()).attackTimeAgo()
                    <= CMConfig.damageTimeThreshold) {
                attacks.get(event.getEntity().getEntityId()).rewardKill(event);
            } else if (CMConfig.disableAnoymDrop) {
                event.getDrops().clear();
            }
            attacks.remove(event.getEntity().getEntityId());
        } else if (CMConfig.disableAnoymDrop) {
            event.getDrops().clear();
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
