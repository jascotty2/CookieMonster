package com.pi.coelho.CookieMonster.entity;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;

import com.pi.coelho.CookieMonster.util.Constants;

public class cmRewardHandler {

    public cmRewardHandler() {
    }

    public void GivePlayerReward(Player p, int m) {
        GivePlayerCoinReward(p, m);
        GivePlayerDropReward(p, m);
    }

    public void GivePlayerCoinReward(Player p, int m) {
        DecimalFormat DecimalFormat = new DecimalFormat("#.##");
        double amount = Double.parseDouble(DecimalFormat.format(Math.random()
                * (Constants.Monster_Coin_Maximum[m] - Constants.Monster_Coin_Minimum[m])
                + (Constants.Monster_Coin_Minimum[m])));

        if (iConomy.getBank().hasAccount(p.getName())) {
            Account account = iConomy.getBank().getAccount(p.getName());
            account.add(amount);

            if (amount > 0.0) {
                p.sendMessage(ChatColor.GREEN + "You are rewarded " + ChatColor.WHITE
                        + iConomy.getBank().format(amount) + ChatColor.GREEN
                        + " for killing the " + ChatColor.WHITE + Constants.Monsters[m]);
            } else if (amount < 0.0) {
                p.sendMessage(ChatColor.RED + "You are penalized " + ChatColor.WHITE
                        + iConomy.getBank().format(amount) + ChatColor.RED
                        + " for killing the " + ChatColor.WHITE + Constants.Monsters[m]);
            }
        }
    }

    public void GivePlayerDropReward(Player p, int m) {
        for (int i = 0; i < Constants.Monster_Drop[m].length; i++) {
            double random = Math.floor(Math.random() * 100);
            if (random < Constants.Monster_Drop[m][i][2]) {
                ItemStack item = new ItemStack(Constants.Monster_Drop[m][i][0],
                        Constants.Monster_Drop[m][i][2], (short) 0);
                p.getWorld().dropItemNaturally(p.getLocation(), item);
            }
        }
    }
}
