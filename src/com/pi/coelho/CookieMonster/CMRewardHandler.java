package com.pi.coelho.CookieMonster;

import org.bukkit.entity.*;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import org.bukkit.inventory.ItemStack;

public class CMRewardHandler {

    public CMRewardHandler() {
    }
    /*
    public void GivePlayerReward(Player p, Monster m) {
    if (m instanceof Creeper) {
    CookieMonster.getRewardHandler().GivePlayerReward(p, 0);
    } else if (m instanceof Skeleton) {
    CookieMonster.getRewardHandler().GivePlayerReward(p, 1);
    } else if (m instanceof Zombie) {
    CookieMonster.getRewardHandler().GivePlayerReward(p, 2);
    } else if (m instanceof Spider) {
    CookieMonster.getRewardHandler().GivePlayerReward(p, 3);
    } else if (m instanceof Slime) {
    CookieMonster.getRewardHandler().GivePlayerReward(p, 4);
    } else if (m instanceof PigZombie) {
    CookieMonster.getRewardHandler().GivePlayerReward(p, 5);
    } else if (m instanceof Ghast) {
    CookieMonster.getRewardHandler().GivePlayerReward(p, 6);
    } else if (m instanceof Giant) {
    CookieMonster.getRewardHandler().GivePlayerReward(p, 7);
    }
    }

    public void GivePlayerReward(Player p, int m) {
    GivePlayerCoinReward(p, m);
    GivePlayerDropReward(p, m);
    }//*/

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
        //System.out.println(CMConfig.Monster_Drop[m].getMaxCoin());
        if (amount != 0 && iConomy.getBank().hasAccount(p.getName())) {
            Account account = iConomy.getBank().getAccount(p.getName());

            if (amount > 0.0) {
                account.add(amount);
                p.sendMessage(CMConfig.messages.get("reward").replaceAll("<amount>", iConomy.getBank().format(amount)).replaceAll("<monster>", CMConfig.CreatureNodes[m]));
            } else if (amount < 0.0) {
                account.subtract(amount);
                p.sendMessage(CMConfig.messages.get("penalty").replaceAll("<amount>", iConomy.getBank().format(amount)).replaceAll("<monster>", CMConfig.CreatureNodes[m]));
            }
        } else {
            System.out.println("no reward for " + CMConfig.CreatureNodes[m]);
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
