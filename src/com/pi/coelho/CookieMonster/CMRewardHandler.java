package com.pi.coelho.CookieMonster;

import org.bukkit.Material;
import org.bukkit.entity.*;

import org.bukkit.inventory.ItemStack;

public class CMRewardHandler {

    //private static final String strs[] = new String[]{"reward", "penalty", "norewardCreature", "norewardMonster"};
    public CMRewardHandler() {
    }

    public void GivePlayerCoinReward(Player p, Entity e) {
        int c = CMConfig.creatureIndex(e);
        if (c >= 0) {
            GivePlayerCoinReward(p, c, p.getItemInHand().getTypeId());
        }
    }

    public void GivePlayerMobSpawnerCoinReward(Player p) {
        GivePlayerCoinReward(p, CMConfig.creatureIndex("MobSpawner"), p.getItemInHand().getTypeId());
    }

    public boolean canAffordKill(Player p, Entity e) {
        int c = CMConfig.creatureIndex(e);
        return canAffordKill(p, c);
    }

    public boolean canAffordMobSpawner(Player p) {
        int c = CMConfig.creatureIndex("MobSpawner");
        return canAffordKill(p, c);
    }

    private boolean canAffordKill(Player p, int c) {
        if (c >= 0 && CookieMonster.config.Monster_Drop[c].getMinCoin() < 0
                && !CMEcon.canAfford(p, -CookieMonster.config.Monster_Drop[c].getMinCoin(p.getItemInHand().getTypeId()))) {
            if (CookieMonster.config.Monster_Drop[c].itemHasReward(p.getItemInHand().getTypeId())) {
                p.sendMessage(CMConfig.messages.get("notafford").
                        replaceAll("<item>", Material.getMaterial(p.getItemInHand().getTypeId()).name()).
                        replaceAll("<monster>", CMConfig.CreatureNodes[c]));
            } else {
                p.sendMessage(CMConfig.messages.get("notafford").
                        replaceAll("<item>", Material.getMaterial(p.getItemInHand().getTypeId()).name()).
                        replaceAll("<monster>", CMConfig.CreatureNodes[c]));
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
                        replaceAll("<amount>", CMEcon.format(amount)).
                        replaceAll("<item>", i.name()).
                        replaceAll("<monster>", CMConfig.CreatureNodes[m]));
            } else if (amount < 0.0) {
                CMEcon.subtractMoney(p, -amount);
                p.sendMessage(CMConfig.messages.get(pre + "penalty").
                        replaceAll("<amount>", CMEcon.format(amount)).
                        replaceAll("<item>", i.name()).
                        replaceAll("<monster>", CMConfig.CreatureNodes[m]));
            }
        } else if (amount == 0) {
            if (CMConfig.isCreature(m)) {
                if (CMConfig.messages.get(pre + "norewardCreature").length() > 0) {
                    p.sendMessage(CMConfig.messages.get(pre + "norewardCreature").
                            replaceAll("<item>", i.name()).
                            replaceAll("<monster>", CMConfig.CreatureNodes[m]));
                }
            } else {
                if (CMConfig.messages.get(pre + "norewardMonster").length() > 0) {
                    p.sendMessage(CMConfig.messages.get(pre + "norewardMonster").
                            replaceAll("<item>", i.name()).
                            replaceAll("<monster>", CMConfig.CreatureNodes[m]));
                }
            }
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
