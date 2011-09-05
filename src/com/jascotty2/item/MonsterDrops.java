/**
 * Programmer: Jacob Scott
 * Program Name: MonsterDrops
 * Description:
 * Date: Apr 1, 2011
 */
package com.jascotty2.item;

import com.jascotty2.CheckInput;
import com.jascotty2.Rand;
import com.jascotty2.util.Str;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.inventory.ItemStack;

/**
 * @author jacob
 */
public class MonsterDrops {

    private final CoinReward reward = new CoinReward(); // double reward.max, reward.min;
    private final HashMap<Integer, CoinReward> itemRewards = new HashMap<Integer, CoinReward>();
    private final ArrayList<Drop> drops = new ArrayList<Drop>();
    public boolean useCustomDrops = false;

    public boolean setReward(String str) {
        return reward.setReward(str);
    }

    public void setReward(double min, double max) {
        reward.setReward(min, max);
    }
    
    public void setReward(double amt) {
        reward.setReward(amt, amt);
    }

    public boolean setItemRewards(String str) {
        boolean good = true;
        itemRewards.clear();
        if (str != null) {
            for (String s : str.split(",")) {
                String p[] = s.split("\\>");
                if (p.length == 2) {
                    int i = CheckInput.GetInt(p[0].trim(), -1);
                    if (i >= 0) {
                        itemRewards.put(i, new CoinReward());
                        if (!itemRewards.get(i).setReward(p[1])) {
                            good = false;
                        }
                    } else {
                        good = false;
                    }
                } else {
                    good = false;
                }
            }
        }
        return good;
    }

    public boolean setDrops(String str) {
        drops.clear();
        useCustomDrops = (str != null);
        if (str != null) {
            for (String dropStr : str.split(",")) {
                Drop toAdd = Drop.fromStr(dropStr);
                if (toAdd != null) {
                    drops.add(toAdd);
                } else {
                    System.out.println("Invalid drop or drop syntax: " + dropStr);
                }
            }
        }
        return true;
    }

    public double getMinCoin() {
        return reward.min;
    }

    public double getMaxCoin() {
        return reward.max;
    }

    public double getMinCoin(int itemId) {
        if (itemRewards.containsKey(itemId)) {
            return itemRewards.get(itemId).min;
        }
        return reward.min;
    }

    public double getMaxCoin(int itemId) {
        if (itemRewards.containsKey(itemId)) {
            return itemRewards.get(itemId).max;
        }
        return reward.max;
    }

    public double getCoinReward() {
        return reward.getCoinReward();
    }

    public double getCoinReward(int itemId) {
        if (itemRewards.containsKey(itemId)) {
            return itemRewards.get(itemId).getCoinReward();
        }
        return reward.getCoinReward();
    }

    public boolean itemHasReward(int itemId) {
        return itemRewards.containsKey(itemId);
    }

    public ItemStack[] getDropsReward() {
        if (!useCustomDrops) {
            return null;
        }
        final ArrayList<ItemStack> droppings = new ArrayList<ItemStack>();
        for (Drop d : drops) {
            if (Rand.RandomBoolean(d.probability / 100) && d.amount > 0) {
                int num = Rand.RandomInt(1, d.amount < 1 ? 1 : d.amount);
                droppings.add(d.item.toItemStack(num));
            }
        }
        return droppings.toArray(new ItemStack[0]);
    }

    private static final class CoinReward {

        public double max, min;

        public CoinReward() {
            max = min = 0;
        }

        public boolean setReward(String str) {
            max = min = 0;
            if (str != null) {
                if (str.contains("-")) {
                    if (str.indexOf("-") == 0) {
                        if (Str.count(str, "-") == 1) {
                            min = max = CheckInput.GetDouble(str, Double.NEGATIVE_INFINITY);
                        } else {
                            min = CheckInput.GetDouble(str.substring(0, str.indexOf("-", 1)).trim(), Double.NEGATIVE_INFINITY);
                            max = CheckInput.GetDouble(str.substring(str.indexOf("-", 1) + 1).trim(), Double.NEGATIVE_INFINITY);
                        }
                    } else {
                        min = CheckInput.GetDouble(str.substring(0, str.indexOf("-")).trim(), Double.NEGATIVE_INFINITY);
                        max = CheckInput.GetDouble(str.substring(str.indexOf("-") + 1).trim(), Double.NEGATIVE_INFINITY);
                    }
                } else {
                    min = max = CheckInput.GetDouble(str, Double.NEGATIVE_INFINITY);
                }
                if (min == Double.NEGATIVE_INFINITY || max == Double.NEGATIVE_INFINITY) {
                    min = max = 0;
                    return false;
                } else if (min > max) {
                    double t = min;
                    min = max;
                    max = t;
                }
            } else {
                min = max = 0;
            }
            return true;
        }

        public void setReward(double minCoin, double maxCoin) {
            min = minCoin;
            max = maxCoin;if (min > max) {
                double t = min;
                min = max;
                max = t;
            }
        }

        public double getCoinReward() {
            if (min == 0 && max == 0) {
                return 0;
            }
            return Rand.RandomDouble(min, max);
        }
    }
}
