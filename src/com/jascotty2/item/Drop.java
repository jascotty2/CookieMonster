/**
 * Programmer: Jacob Scott
 * Program Name: Drop
 * Description:
 * Date: Apr 1, 2011
 */
package com.jascotty2.item;

import com.jascotty2.CheckInput;
import com.jascotty2.Str;

/**
 * @author jacob
 */
public class Drop {

    public double probability = .5; // out of 100
    public int amount = 1;
    public Item item = null;

    public Drop() {
    }

    public Drop(Item itm, int amt, double prob) {
        item = itm;
        amount = amt;
        probability = prob;
    }

    public static Drop fromStr(String str) {
        // syntax: itemID[:subData][@maxDrop]%Probability
        if (!str.contains(",")
                && Str.count(str, "%") == 1
                && Str.count(str, ":") <= 1
                && Str.count(str, "@") <= 1) {
            int sdat = str.indexOf(":");
            int samt = str.indexOf("@");
            int sper = str.indexOf("%");
            if (sdat <= samt && sdat < sper && samt < sper) {
                Item src = Item.fromIDD(str.substring(0, samt > 0 ? samt : sper).trim());
                if (src != null) {
                    if (org.bukkit.Material.getMaterial(src.itemId) != null) {
                        return new Drop(src,
                                samt > 0 ? CheckInput.GetInt(str.substring(samt + 1, sper), 1) : 1,
                                CheckInput.GetDouble(str.substring(sper + 1), 50));
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
