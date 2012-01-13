/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pi.coelho.CookieMonster.protectcheck;

import com.pi.coelho.CookieMonster.CMConfig;
import com.pi.coelho.CookieMonster.CookieMonster;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

/**
 *
 * @author Jacob
 */
public class EntityProtectListener extends EntityListener {

	@Override
	public void onEntityDamage(EntityDamageEvent entEvent) {
		if (entEvent.isCancelled() || !CookieMonster.getSettings().disableExpensiveKill) {
			return;
		}
		if (entEvent instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entEvent;
			if (event.getDamager() instanceof Arrow) {
				entDamage(event.getEntity(), ((Arrow) event.getDamager()).getShooter(), entEvent);
			} else {
				entDamage(event.getEntity(), event.getDamager(), entEvent);
			}
		}
	}

	void entDamage(Entity monster, Entity damager, EntityDamageEvent entEvent) {
		if (monster instanceof LivingEntity) {
			Player pl = null;
			if (damager instanceof Player) {
				pl = (Player) damager;
			} else if (damager instanceof Wolf && CookieMonster.getSettings().allowWolfHunt) {
				if (((CraftWolf) damager).isTamed()) {
					AnimalTamer at = ((CraftWolf) damager).getOwner();
					if (at instanceof Player) {
						pl = (Player) at;
					}
				}
			}
			if (pl != null && CookieMonster.getSettings().cmEnabled(entEvent.getEntity().getLocation())) {
				if (!CookieMonster.getRewardHandler().canAffordKill(pl, monster)) {
					entEvent.setCancelled(true);
					if (damager instanceof Wolf) {
						((CraftWolf) damager).setTarget(null);
					}
					return;
				}
			}
		}
	}
}
