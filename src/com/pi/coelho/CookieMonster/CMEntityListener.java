package com.pi.coelho.CookieMonster;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class CMEntityListener implements Listener {

	protected HashMap<Integer, MonsterAttack> attacks = new HashMap<Integer, MonsterAttack>();
	Server sv = null;

	public CMEntityListener(Server bukkitServer) {
		sv = bukkitServer;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent entEvent) {
		if (entEvent.isCancelled() || !(entEvent.getEntity() instanceof LivingEntity)) {
			return;
		}
		if (entEvent instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entEvent;
			if (event.getDamager() instanceof Projectile) {
				entDamage((LivingEntity) event.getEntity(), ((Projectile) event.getDamager()).getShooter(), entEvent);
			} else {
				entDamage((LivingEntity) event.getEntity(), event.getDamager(), entEvent);
			}
		} else {
			monsterDamaged((LivingEntity) entEvent.getEntity(), null, true, entEvent.getDamage());
		}
	}

	void entDamage(LivingEntity monster, Entity damager, EntityDamageEvent entEvent) {
		if (monster instanceof LivingEntity) {
			//System.out.println(damager);
			Player pl = null;
			if (damager instanceof Player) {
				pl = (Player) damager;
			} else if (damager instanceof Wolf && CookieMonster.config.allowWolfHunt) {
				if (((Wolf) damager).isTamed()) {
					AnimalTamer at = ((Wolf) damager).getOwner();
					if (at instanceof Player) {
						pl = (Player) at;
					}
				}
			}
			boolean handleKill = CookieMonster.config.cmEnabled(entEvent.getEntity().getLocation());
			if (pl == null) {
				monsterDamaged(monster, null, handleKill, entEvent.getDamage());
			} else {
				monsterDamaged(monster, pl, handleKill, entEvent.getDamage());
			}
		}
	}

	public void monsterDamaged(LivingEntity monster, Player player, boolean handleKill, double damage) {

		if (!attacks.containsKey(monster.getEntityId())) {
			if (player != null) {
				attacks.put(monster.getEntityId(), new MonsterAttack(player, damage, handleKill));
			}
		} else {
			attacks.get(monster.getEntityId()).addAttack(player, damage, handleKill);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		EntityDeathEvent entd = new EntityDeathEvent(event.getEntity(), event.getDrops(), event.getDroppedExp());
		onEntityDeath(entd);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)
				|| event.getDrops() == null
				|| event.getDrops() == Collections.EMPTY_LIST
				|| event.getEntity().getLastDamageCause() == null) {
			return;
		}
		MonsterAttack at = attacks.get(event.getEntity().getEntityId());
		if (at != null && !at.handleKill) {
			return;
		}

		// check against camping
		if (CookieMonster.config.globalCampTrackingEnabled) {
			Location loc = event.getEntity().getLocation();
			CookieMonster.killTracker.addKill(loc);
//			System.out.println("kill added, bringing total about " + loc + " to " + CookieMonster.killTracker.numKills(loc,
//					CookieMonster.config.deltaX, CookieMonster.config.deltaY,
//					CookieMonster.config.campTrackingTimeout));
			if (CookieMonster.killTracker.numKills(loc,
					CookieMonster.config.deltaX, CookieMonster.config.deltaY,
					CookieMonster.config.campTrackingTimeout) > CookieMonster.config.campKills) {
				if (at != null) {
					for (Player p : at.damagers.keySet()) {
						if (p != null) {
							p.sendMessage(CMConfig.messages.get("nocampingreward"));
						}
					}
				}

				// don't remove player exp or drops, but don't reward the kill, either
				if (!(event.getEntity() instanceof Player)) {
					if (CookieMonster.config.disableCampingDrops) {
						event.getDrops().clear();
					}
					if (CookieMonster.config.disableCampingExp) {
						event.setDroppedExp(0);
					}
				} else {
					Player vic = (Player) event.getEntity();
					CookieMonster.playerListener.addRespawnInv(vic);
					event.getDrops().clear();
					event.setDroppedExp(0);
				}
				return;
			}
		}

		if (event.getEntity() instanceof Player) {
			if (at != null) {
				if (at.attackTimeAgo() <= CookieMonster.config.damageTimeThreshold) {
					at.rewardKill(event);
				}
				attacks.remove(event.getEntity().getEntityId());
			}
			return;
		}

		if (CookieMonster.config.disableAnoymDrop) {
			if (at == null) {
				event.getDrops().clear();
			}
		} else if ((at == null || at.attackTimeAgo() > CookieMonster.config.damageTimeThreshold)
				&& CookieMonster.config.alwaysReplaceDrops) {
			// if this is not a rewarded kill & alwaysReplaceDrops, set the drops
			ItemStack newDrops[] = CookieMonster.getRewardHandler().getDropReward(event.getEntity());
			if (newDrops != null) {
				if (CookieMonster.config.replaceDrops) {
					event.getDrops().clear();
				}
				event.getDrops().addAll(Arrays.asList(newDrops));
			}
		} else if (at != null) {
			if (at.attackTimeAgo() <= CookieMonster.config.damageTimeThreshold) {
				at.rewardKill(event);
			}
			attacks.remove(event.getEntity().getEntityId());
		}
	}

	public class MonsterAttack {

		final HashMap<Player, Double> damagers = new HashMap<Player, Double>();
		Player lastAttacker;
		long lastAttackTime;
		boolean handleKill = true;

		public MonsterAttack(Player attacker, double dmg, boolean handleKill) {
			addAttack(attacker, dmg, handleKill);
		}

		public long attackTimeAgo() {
			return lastAttackTime > 0 ? System.currentTimeMillis() - lastAttackTime : 0;
		}

		public final void addAttack(Player attacker, double dmg, boolean handleKill) {
			lastAttacker = attacker;
			final long now = System.currentTimeMillis();
			if (lastAttackTime == 0 || (now - lastAttackTime) > CookieMonster.config.damageDelay) {
				damagers.clear();
			}
			lastAttackTime = now;
			Double oldDmg = damagers.get(attacker);
			damagers.put(attacker, (oldDmg != null ? oldDmg.doubleValue() : 0) + dmg);


			this.handleKill = handleKill;
			lastAttackTime = System.currentTimeMillis();
		}

		Map<Player, Double> killResults() {
			if (lastAttackTime > 0 && (System.currentTimeMillis() - lastAttackTime) <= CookieMonster.config.damageDelay) {
				HashMap<Player, Double> damagePercents = new HashMap<Player, Double>();
				double total = 0;
				for (Double d : damagers.values()) {
					total += d;
				}
				for (Map.Entry<Player, Double> e : damagers.entrySet()) {
					damagePercents.put(e.getKey(), e.getValue() / total);
				}
				return damagePercents;
			}
			damagers.clear();
			return null;
		}

		public void rewardKill(EntityDeathEvent event) {
			if (handleKill && !damagers.isEmpty()) {
				Map<Player, Double> damagePercents = killResults();
				if (damagePercents != null && !damagePercents.isEmpty()) {
					if (CookieMonster.config.multipleRewards) {
						if (damagePercents.size() == 1) {
							Player p = damagePercents.keySet().iterator().next();
							CookieMonster.getRewardHandler().GivePlayerCoinReward(p, event.getEntity(), 1);
						} else {
							for (Map.Entry<Player, Double> e : damagePercents.entrySet()) {
								final Player p = e.getKey();
								CookieMonster.getRewardHandler().GivePlayerCoinReward(p, event.getEntity(), e.getValue());
							}
						}
					} else {
						CookieMonster.getRewardHandler().GivePlayerCoinReward(lastAttacker, event.getEntity(), 1);
					}
					//if (!(event.getEntity() instanceof Player)) {
					ItemStack newDrops[] = CookieMonster.getRewardHandler().getDropReward(event.getEntity());
					if (newDrops != null) {
						if (CookieMonster.config.replaceDrops) {
							event.getDrops().clear();
						}
						event.getDrops().addAll(Arrays.asList(newDrops));
					}
					if (CookieMonster.config.expMultiplier != 1) {
						event.setDroppedExp((int) (event.getDroppedExp() * CookieMonster.config.expMultiplier));
					}
					//}
				}
			}
		}
	}
}
