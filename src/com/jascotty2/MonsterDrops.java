/**
 * Programmer: Jacob Scott
 * Program Name: MonsterDrops
 * Description:
 * Date: Apr 1, 2011
 */
package com.jascotty2;

import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

/**
 * @author jacob
 */
public class MonsterDrops {

    protected double coin_max, coin_min;
    protected ArrayList<Drop> drops = new ArrayList<Drop>();

    public boolean setReward(String str) {
        if (str != null) {
            if (str.contains("-")) {
                if (str.indexOf("-") == 0) {
                    if (Str.count(str, "-") == 1) {
                        coin_min = coin_max = CheckInput.GetDouble(str, Double.NEGATIVE_INFINITY);
                    } else {
                        coin_min = CheckInput.GetDouble(str.substring(0, str.indexOf("-", 1)).trim(), Double.NEGATIVE_INFINITY);
                        coin_max = CheckInput.GetDouble(str.substring(str.indexOf("-", 1) + 1).trim(), Double.NEGATIVE_INFINITY);
                    }
                } else {
                    coin_min = CheckInput.GetDouble(str.substring(0, str.indexOf("-")).trim(), Double.NEGATIVE_INFINITY);
                    coin_max = CheckInput.GetDouble(str.substring(str.indexOf("-") + 1).trim(), Double.NEGATIVE_INFINITY);
                }
            } else {
                coin_min = coin_max = CheckInput.GetDouble(str, Double.NEGATIVE_INFINITY);
            }
            if (coin_min == Double.NEGATIVE_INFINITY || coin_max == Double.NEGATIVE_INFINITY) {
                coin_min = coin_max = 0;
                return false;
            }else if(coin_min > coin_max){
                double t = coin_min;
                coin_min = coin_max;
                coin_max = t;
            }
        } else {
            coin_min = coin_max = 0;
        }
        return true;
    }

    public boolean setDrops(String str) {
        drops.clear();
        if (str != null) {
            for (String dropStr : str.split(",")) {
                Drop toAdd = Drop.fromStr(dropStr);
                if (toAdd != null) {
                    drops.add(toAdd);
                } else {
                    System.out.println("Invalid drop syntax: " + dropStr);
                }
            }
        }
        return true;
    }

    public double getMinCoin(){
        return coin_min;
    }
    public double getMaxCoin(){
        return coin_max;
    }

    public double getCoinReward() {
        if (coin_min == 0 && coin_max == 0) {
            return 0;
        }
        return Rand.RandomDouble(coin_min, coin_max);
    }

    public ItemStack[] getDropsReward() {
        if (drops.isEmpty()) {
            return null;
        }
        ArrayList<ItemStack> droppings = new ArrayList<ItemStack>();
        for (Drop d : drops) {
            if (Rand.RandomBoolean(d.probability / 100) && d.amount > 0) {
                int num = Rand.RandomInt(1, d.amount < 1 ? 1 : d.amount);
                droppings.add(d.item.toItemStack(num));
            }
        }
        return droppings.toArray(new ItemStack[0]);
    }
}
