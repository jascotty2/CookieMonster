package com.pi.coelho.CookieMonster;

import org.bukkit.entity.*;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import org.bukkit.inventory.ItemStack;

public class CMRewardHandler {

    public CMRewardHandler() {
    }

    public void GivePlayerCoinReward(Player p, Entity e) {
        int c = CMConfig.creatureIndex(e);
        if (c >= 0) {
            GivePlayerCoinReward(p, c);
        }
    }

    public void GivePlayerMobSpawnerCoinReward(Player p) {
        GivePlayerCoinReward(p, CMConfig.creatureIndex("MobSpawner"));
    }

    private void GivePlayerCoinReward(Player p, int m) {
        double amount = CMConfig.Monster_Drop[m].getCoinReward();
        if (CMConfig.intOnly) {
            amount = Math.round(amount);
        }
        //System.out.println(CMConfig.Monster_Drop[m].getMaxCoin());
        if (amount != 0 && iConomy.getBank().hasAccount(p.getName())) {
            Account account = iConomy.getBank().getAccount(p.getName());

            if (amount > 0.0) {
                account.add(amount);
                p.sendMessage(CMConfig.messages.get("reward").replaceAll("<amount>", iConomy.getBank().format(amount)).replaceAll("<monster>", CMConfig.CreatureNodes[m]));
            } else if (amount < 0.0) {
                account.subtract(-amount);
                p.sendMessage(CMConfig.messages.get("penalty").replaceAll("<amount>", iConomy.getBank().format(amount)).replaceAll("<monster>", CMConfig.CreatureNodes[m]));
            }
        } else if (amount == 0) {
            if (CMConfig.isCreature(m)) {
                if (CMConfig.messages.get("norewardCreature").length() > 0) {
                    p.sendMessage(CMConfig.messages.get("norewardCreature").replaceAll("<monster>", CMConfig.CreatureNodes[m]));
                }
            } else {
                if (CMConfig.messages.get("norewardMonster").length() > 0) {
                    p.sendMessage(CMConfig.messages.get("norewardMonster").replaceAll("<monster>", CMConfig.CreatureNodes[m]));
                }
            }
        }
    }
    /*
    public void GivePlayerDropReward(Player p, int m) {
    for (int i = 0; i < CMConfig.Monster_Drop[m].length; i++) {
    double random = Math.floor(Math.random() * 100);
    if (random < CMConfig.Monster_Drop[m][i][2]) {
    ItemStack item = new ItemStack(CMConfig.Monster_Drop[m][i][0],
    CMConfig.Monster_Drop[m][i][2], (short) 0);
    p.getWorld().dropItemNaturally(p.getLocation(), item);
    }
    }
    }*/

    public ItemStack[] getDropReward(Entity e) {
        int c = CMConfig.creatureIndex(e);
        if (c >= 0) {
            return CMConfig.Monster_Drop[c].getDropsReward();
        }
        return null;
    }
}
