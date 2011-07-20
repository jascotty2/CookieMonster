package com.pi.coelho.CookieMonster;

import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;

import org.bukkit.inventory.ItemStack;

public class CMRewardHandler {

    public CMRewardHandler() {
    }

    public void GivePlayerCoinReward(Player p, Entity e) {
        int c = CMConfig.creatureIndex(e, p);
        if (c >= 0) {
            GivePlayerCoinReward(p, c, p.getItemInHand().getTypeId());
        }
    }

    public void GivePlayerMobSpawnerCoinReward(Player p) {
        GivePlayerCoinReward(p, CMConfig.creatureIndex("MobSpawner"), p.getItemInHand().getTypeId());
    }

    public boolean canAffordKill(Player p, Entity e) {
        int c = CMConfig.creatureIndex(e);
        return c >= 0 ? canAffordKill(p, c) : true;
    }

    public boolean canAffordMobSpawner(Player p) {
        int c = CMConfig.creatureIndex("MobSpawner");
        return canAffordKill(p, c);
    }
    
    public boolean hasPenalty(Player p, Entity e) {
        int c = CMConfig.creatureIndex(e);
        return c >= 0 ? canAffordKill(p, c) : false;
    }
    
    private boolean hasPenalty(Player p, int c) {
		return CookieMonster.config.Monster_Drop[c].getMinCoin(p.getItemInHand().getTypeId()) < 0;
	}

    private boolean canAffordKill(Player p, int c) {
		double min = CookieMonster.config.Monster_Drop[c].getMinCoin(p.getItemInHand().getTypeId());
        if (c >= 0 && min < 0
                && !CMEcon.canAfford(p, -min)) {
            if (CookieMonster.config.Monster_Drop[c].itemHasReward(p.getItemInHand().getTypeId())) {
                p.sendMessage(CMConfig.messages.get("itemnotafford").
                        replace("<item>", Material.getMaterial(p.getItemInHand().getTypeId()).name()).
                        replace("<monster>", CMConfig.CreatureNodes[c]));
            } else {
                p.sendMessage(CMConfig.messages.get("notafford").
                        replace("<item>", Material.getMaterial(p.getItemInHand().getTypeId()).name()).
                        replace("<monster>", CMConfig.CreatureNodes[c]));
            }
            return false;
        }
        return true;
    }

    public double MinMobSpawnerCoinReward(int itemId) {
        int c = CMConfig.creatureIndex("MobSpawner");
        if (c >= 0) {
            return CookieMonster.config.Monster_Drop[c].getMinCoin(itemId);
        }
        return 0;
    }

    private void GivePlayerCoinReward(Player p, int m, int itemId) {
        if (m < 0 || !CMEcon.hasAccount(p)) {
            return;
        }
        try {
            double amount = CookieMonster.config.Monster_Drop[m].getCoinReward(itemId);
            if (CookieMonster.config.intOnly || !CMEcon.decimalSupported()) {
                amount = Math.round(amount);
            }
            //System.out.println(CookieMonster.config.Monster_Drop[m].getMaxCoin());
            String pre = "";
            if (CookieMonster.config.Monster_Drop[m].itemHasReward(itemId)) {
                pre = "item";
            }
            Material i = Material.getMaterial(itemId);
            if (amount != 0) {
                if (amount > 0.0) {
                    CMEcon.addMoney(p, amount);
                    p.sendMessage(CMConfig.messages.get(pre + "reward").
                            replace("<amount>", CMEcon.format(amount)).
                            replace("<item>", i.name()).
                            replace("<monster>", CMConfig.CreatureNodes[m]));
                } else if (amount < 0.0) {
                    CMEcon.subtractMoney(p, -amount);
                    p.sendMessage(CMConfig.messages.get(pre + "penalty").
                            replace("<amount>", CMEcon.format(amount)).
                            replace("<item>", i.name()).
                            replace("<monster>", CMConfig.CreatureNodes[m]));
                }
            } else if (amount == 0) {
                if (CMConfig.isCreature(m)) {
                    if (CMConfig.messages.get(pre + "norewardCreature").length() > 0) {
                        p.sendMessage(CMConfig.messages.get(pre + "norewardCreature").
                                replace("<item>", i.name()).
                                replace("<monster>", CMConfig.CreatureNodes[m]));
                    }
                } else {
                    if (CMConfig.messages.get(pre + "norewardMonster").length() > 0) {
                        p.sendMessage(CMConfig.messages.get(pre + "norewardMonster").
                                replace("<item>", i.name()).
                                replace("<monster>", CMConfig.CreatureNodes[m]));
                    }
                }
            }
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Unexpected Error processing Reward");
            CookieMonster.Log(Level.SEVERE, "Unexpected Error processing Reward", e);
        }
    }
    /*
    public void GivePlayerDropReward(Player p, int m) {
    for (int i = 0; i < CookieMonster.config.Monster_Drop[m].length; i++) {
    double random = Math.floor(Math.random() * 100);
    if (random < CookieMonster.config.Monster_Drop[m][i][2]) {
    ItemStack item = new ItemStack(CookieMonster.config.Monster_Drop[m][i][0],
    CookieMonster.config.Monster_Drop[m][i][2], (short) 0);
    p.getWorld().dropItemNaturally(p.getLocation(), item);
    }
    }
    }*/

    public ItemStack[] getDropReward(Entity e) {
        return getDropReward(CMConfig.creatureIndex(e));
    }

    public ItemStack[] getMSDropReward() {
        return getDropReward(CMConfig.creatureIndex("MobSpawner"));
    }

    private ItemStack[] getDropReward(int c) {
        if (c >= 0) {
            return CookieMonster.config.Monster_Drop[c].getDropsReward();
        }
        return null;
    }
}
