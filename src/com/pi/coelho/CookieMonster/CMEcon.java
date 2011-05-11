/**
 * Programmer: Jacob Scott
 * Program Name: CMEcon
 * Description: handler for econ events
 * Date: Apr 11, 2011
 */
package com.pi.coelho.CookieMonster;

import cosine.boseconomy.BOSEconomy;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author jacob
 */
public class CMEcon {

    protected static com.iConomy.iConomy iConomy = null;
    protected static com.nijiko.coelho.iConomy.iConomy legacyIConomy = null;
    protected static BOSEconomy economy = null;

    public static boolean initEcon(Server sv) {
        Plugin test = sv.getPluginManager().getPlugin("iConomy");
        if (test != null) {//this.getServer().getPluginManager().isPluginEnabled("iConomy")) {
            try {
                legacyIConomy = (com.nijiko.coelho.iConomy.iConomy) test;
            } catch (NoClassDefFoundError e) {
                iConomy = (com.iConomy.iConomy) test;
            }
            CookieMonster.Log("Attached to iConomy.");
        } else {
            test = sv.getPluginManager().getPlugin("BOSEconomy");
            if (test != null) {
                economy = (BOSEconomy) test;
                CookieMonster.Log("Attached to BOSEconomy");
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean decimalSupported() {
        return iConomy != null;
    }

    public static boolean canAfford(Player pl, double amt) {
        if (legacyIConomy != null) {
            return com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(pl.getName()).getBalance() >= amt;
        } else if (iConomy != null) {
            return iConomy.getAccount(pl.getName()).getHoldings().balance() >= amt;
        } else if (economy != null) {
            return economy.getPlayerMoney(pl.getName()) >= amt;
        } else {
            return amt >= 0;
        }
    }

    public static boolean hasAccount(Player pl) {
        if (legacyIConomy != null) {
            return com.nijiko.coelho.iConomy.iConomy.getBank().hasAccount(pl.getName());
        } else if (iConomy != null) {
            return iConomy.hasAccount(pl.getName());
        } else if (economy != null) {
            return economy.playerRegistered(pl.getName(), false);
        } else {
            return false;
        }
    }

    public static void addMoney(Player pl, double amt) {
        if (legacyIConomy != null) {
            com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(pl.getName()).add(amt);
        } else if (iConomy != null) {
            iConomy.getAccount(pl.getName()).getHoldings().add(amt);
        } else if (economy != null) {
            economy.addPlayerMoney(pl.getName(), (int) Math.round(amt), true);
        }
    }

    public static void subtractMoney(Player pl, double amt) {
        if (legacyIConomy != null) {
            com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(pl.getName()).subtract(amt);
        } else if (iConomy != null) {
            iConomy.getAccount(pl.getName()).getHoldings().subtract(amt);
        } else if (economy != null) {
            economy.addPlayerMoney(pl.getName(), -(int) Math.round(amt), true);
        }
    }

    public static String format(double amt) {
        if (legacyIConomy != null) {
            com.nijiko.coelho.iConomy.iConomy.getBank().format(amt);
        } else if (iConomy != null) {
            return iConomy.format(amt);
        } else if (economy != null) {
            amt = Math.round(amt);
            if (amt < 1 || amt > 1) {
                return String.valueOf(amt) + " " + economy.getMoneyName();
            } else {
                return String.valueOf(amt) + " " + economy.getMoneyNamePlural();
            }
        }
        return String.format("%.2f", amt);
    }
} // end class CMEcon

