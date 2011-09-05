/**
 * Programmer: Jacob Scott
 * Program Name: Item
 * Description: class for defining an item
 * Date: Mar 12, 2011
 */
package com.jascotty2.item;

import com.jascotty2.CheckInput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.inventory.ItemStack;

public class Item {
    // all items.. used by ItemDB

    protected static HashMap<String, Item> items = new HashMap<String, Item>();
    // Item Information
    protected int itemId;
    protected byte itemData;
    protected boolean isLegal = true; // is is legitemitely obtainable
    protected int maxStack = 64;
    // color is used if this item has a custom color
    public String name, color = null;
    private LinkedList<String> itemAliases = new LinkedList<String>();
    private LinkedList<String> subAliases = new LinkedList<String>();
    // max data value to accept in a damage value (ItemStack)
    //public final static byte MAX_DATA = (byte)30;
    // max damage. indicates that this is a tool
    protected short maxdamage = 0;

    public Item() {
        itemId = -1;
        name = "";
    } // end default constructor

    public Item(String name) {
        this.name = "";
        if (!setIDD(name)) {
            this.name = name;
        }
    }

    public Item(Item copy) {
        SetItem(copy);
    }

    public Item(int id) {
        itemId = id;
        name = "";
    }

    public Item(int id, byte dat) {
        itemId = id;
        itemData = dat;
        name = "";
    }

    public Item(int id, byte dat, String name) {
        itemId = id;
        itemData = dat;
        this.name = name;
    }

    public Item(ItemStack i) {
        itemId = i.getTypeId();
        //if(maxdamage==0)// i.getDurability()<MAX_DATA)
        itemData = (byte) i.getDurability();
        name = "";
    }

    public int ID() {
        return itemId;
    }

    public byte Data() {
        return itemData;
    }

    public short MaxDamage() {
        return maxdamage;
    }

    public boolean IsTool() {
        return maxdamage > 0;
    }

    public boolean IsLegal() {
        return isLegal;
    }

    public String IdDatStr() {
        return String.format("%d:%d", itemId, itemData);
    }

    public void setID(int id) {
        if (id >= 0 && id <= 9999) {
            itemId = id;
        }
    }

    public void setData(Byte d) {
        itemData = d;
    }

    public void setMaxDamage(short d) {
        maxdamage = d;
    }

    public void SetLegal(boolean isAllowed) {
        isLegal = isAllowed;
    }

    public void SetMaxStack(int stack) {
        maxStack = stack;
    }

    public final boolean setIDD(String idd) {
        if (idd.contains(":")) {
            if (idd.length() > idd.indexOf(":")
                    && CheckInput.IsInt(idd.substring(0, idd.indexOf(":")))
                    && CheckInput.IsByte(idd.substring(idd.indexOf(":") + 1))) {
                itemId = CheckInput.GetInt(idd.substring(0, idd.indexOf(":")), -1);
                itemData = CheckInput.GetByte(idd.substring(idd.indexOf(":") + 1), (byte) 0);
            } else {
                itemId = -1;
            }
        } else {
            itemId = CheckInput.GetInt(idd, -1);
        }
        return itemId >= 0;
    }

    public static Item fromIDD(String idd) {
        if (idd.contains(":")) {
            if (idd.length() > idd.indexOf(":")
                    && CheckInput.IsInt(idd.substring(0, idd.indexOf(":")))
                    && CheckInput.IsByte(idd.substring(idd.indexOf(":") + 1))) {
                int itemId = CheckInput.GetInt(idd.substring(0, idd.indexOf(":")), -1);
                byte itemData = CheckInput.GetByte(idd.substring(idd.indexOf(":") + 1), (byte) -1);
                if (itemId >= 0 && itemData >= 0) {
                    return new Item(itemId, itemData);
                }
            }
        } else {
            int itemId = CheckInput.GetInt(idd, -1);
            if (itemId >= 0) {
                return new Item(itemId);
            }
        }
        return null;
    }

    public final void SetItem(Item copy) {
        itemAliases.clear();
        subAliases.clear();
        if (copy == null) {
            this.itemId = -1;
            this.itemData = (byte) 0;
            this.name = "null";
        } else {
            this.itemAliases.addAll(copy.itemAliases);
            this.subAliases.addAll(copy.subAliases);
            this.itemId = copy.itemId;
            this.itemData = copy.itemData;
            this.name = copy.name;
            this.isLegal = copy.isLegal;
            this.maxStack = copy.maxStack;
            this.color = copy.color;
        }
    }

    public void AddAlias(String a) {
        itemAliases.add(a.trim().toLowerCase());
    }

    public void AddSubAlias(String a) {
        subAliases.add(a.trim().toLowerCase());
    }

    public boolean HasAlias(String a) {
        return itemAliases.contains(a.trim().toLowerCase());
    }

    public boolean HasSubAlias(String a) {
        return subAliases.contains(a.trim().toLowerCase());
    }

    public static Item findItem(ItemStack search) {
        if (search == null) {
            return null;// || search.getAmount()==0
        }
        //System.out.println("find: " + search.getTypeId() + ":" + search.getDurability());
        return findItem(search.getTypeId() + ":" + search.getDurability());//(search.getDurability()<MAX_DATA?search.getDurability():0));
    }
    /*
    public static Item findItem(ItemStockEntry search) {
    if (search == null) {
    return null;
    }
    return findItem(search.itemNum + ":" + search.itemSub);
    }
     */

    public static Item findItem(Item search) {
        if (search == null) {
            return null;
        }
        final Item searchitem = new Item(search);
        for (final Item i : items.values()) {
            //if(i.equals(search)) return i;
            if (i.equals(searchitem)) {
                return i;
            }
        }
        return null;
        /*
        if (search.ID() >= 0) {
        return findItem(search.IdDatStr());
        }
        return findItem(search.name);*/
    }

    public static Item findItem(int id, byte sub) {
        for (final Item i : items.values()) {
            if (i.ID() == id && i.Data() == sub) {
                return i;
            }
        }
        return null;
    }

    public static Item findItem(String search) {
        if (search == null) {
            return null;
        }
        //System.out.println("searching: " + search);
        if (items.containsKey(search)) {
            return items.get(search);
        } else if (CheckInput.IsInt(search)) {// && (!search.contains(":") || search.length() == search.indexOf(":"))) {
            return items.get(search.replace(":", "") + ":0");
        } else if (search.contains(":")) {
            // run a search for both parts (faster than .equals for string)
            final Item isearch = findItem(search.substring(0, search.indexOf(":")));
            //System.out.println("found: " + (isearch==null?"null" : isearch) + "   " + (isearch != null && isearch.IsTool()));
            if (isearch != null) {
                if (isearch.IsTool()) {
                    // this is a tool, so return as found
                    return isearch;
                } else {
                    int id = isearch.ID();
                    // now check second part
                    if (CheckInput.IsByte(search.substring(search.indexOf(":") + 1))) {
                        final byte dat = CheckInput.GetByte(search.substring(search.indexOf(":") + 1), (byte) 0);
                        for (Item i : items.values()) {
                            if (i.ID() == id && i.Data() == dat) {
                                return i;
                            }
                        }
                    }
                    search = search.substring(search.indexOf(":") + 1);
                    for (final Item i : items.values()) {
                        if (i.ID() == id && i.HasSubAlias(search)) {
                            return i;
                        }
                    }
                }
            }
        } 
    	Item i = findItemSearch(search, items.values());
    	if (i != null) return i;
    	if (search.endsWith("s")) i = findItemSearch(search.substring(0, search.length() - 1), items.values());
    	if (i != null) return i;
    	if (search.endsWith("es")) return findItemSearch(search.substring(0, search.length() - 2), items.values());
    	return null;
    }
    
    private static Item[] findItemsSearch(final String search, final Collection<Item> items)
    {
    	ArrayList<Item> is = new ArrayList<Item>();
    	if (search != null) {
    		for (final Item i : items) {
    			if (i.name.equalsIgnoreCase(search)) {
    				is.add(i);
    			}
    			for (final String suba : i.itemAliases) {
    				if (suba.equalsIgnoreCase(search)) {
    					is.add(i);
    				}
    			}
    		}
    	}
    	return is.toArray(new Item[0]);
    }
    
    private static Item findItemSearch(final String search, final Collection<Item> items)
    {
    	if (search != null) {
    		for (final Item i : items) {
    			if (i.name.equalsIgnoreCase(search)) {
    				return i;
    			}
    			for (final String suba : i.itemAliases) {
    				if (suba.equalsIgnoreCase(search)) {
    					return i;
    				}
    			}
    		}
    	}
    	return null;
    }

    public static Item[] findItems(String search) {
        if (search == null) {
            return null;
        }
        if (CheckInput.IsInt(search)) {
            return new Item[]{items.get(search.replace(":", "") + ":0")};
        } else if (items.containsKey(search)) {
            return new Item[]{items.get(search)};
        } else if (search.contains(":")) {
            return new Item[]{findItem(search)};
        }
        
        return findItemsSearch(search.toLowerCase(), items.values());
    }

    public boolean equals(Item i) {
        if (i == null) {
            return false;
        }
        return (i.ID() == itemId && ((IsTool() && i.IsTool()) || i.Data() == itemData)) || i.equals(name) || equals(i.name);
    }

    public boolean equals(String s) {
        if (s == null) {
            return false;
        }
        s = s.toLowerCase().trim();
        if (s.contains(":")) {
            // find base id
            final Item first = findItem(s.substring(0, s.indexOf(":")));
            // if exists & id matched this one:
            if (first != null && first.ID() == itemId) {
                // check if second part is a number or alias
                if (CheckInput.IsByte(s.substring(s.indexOf(":") + 1))) {
                    return itemData == CheckInput.GetByte(s.substring(s.indexOf(":") + 1), (byte) 0);
                } else {
                    return itemAliases.contains(s.substring(s.indexOf(":") + 1));
                }
            } else {
                return false;
            }
            //return (s.substring(0, s.indexOf(":")).equalsIgnoreCase(name) || itemAliases.indexOf(s.substring(0, s.indexOf(":"))) != -1)
            //        && (subAliases.indexOf(s.substring(s.indexOf(":") + 1)) != -1);
        } else {
            return s.equalsIgnoreCase(name) || itemAliases.indexOf(s) != -1;
        }
    }

    public String coloredName() {
        return color == null ? name : color + name;
    }

    public boolean SetColor(String col) {
        if (col == null) {
            return false;
        }
        
        col = col.toLowerCase().trim();
        
        /*
        #       &0 is black
        #       &1 is dark blue
        #       &2 is dark green
        #       &3 is dark sky blue
        #       &4 is red
        #       &5 is magenta
        #       &6 is gold or amber
        #       &7 is light grey
        #       &8 is dark grey
        #       &9 is medium blue
        #       &2 is light green
        #       &b is cyan
        #       &c is orange-red
        #       &d is pink
        #       &e is yellow
        #       &f is white
         */
        
        final ConcurrentHashMap<String, String> colors = new ConcurrentHashMap<String, String>();
        colors.put("black", "\u00A70");
        colors.put("blue", "\u00A71");
        colors.put("dark blue", "\u00A71");
        colors.put("green", "\u00A72");
        colors.put("dark green", "\u00A72");
        colors.put("sky blue", "\u00A73");
        colors.put("dark sky blue", "\u00A73");
        colors.put("red", "\u00A74");
        colors.put("magenta", "\u00A75");
        colors.put("purple", "\u00A75");
        colors.put("gold", "\u00A76");
        colors.put("amber", "\u00A76");
        colors.put("dark yellow", "\u00A76");
        colors.put("light gray", "\u00A77");
        colors.put("light grey", "\u00A77");
        colors.put("dark gray", "\u00A78");
        colors.put("dark grey", "\u00A78");
        colors.put("gray", "\u00A78");
        colors.put("grey", "\u00A78");
        colors.put("medium blue", "\u00A79");
        colors.put("light green", "\u00A7a");
        colors.put("lime", "\u00A7a");
        colors.put("lime green", "\u00A7a");
        colors.put("cyan", "\u00A7b");
        colors.put("light blue", "\u00A7b");
        colors.put("orange", "\u00A7c");
        colors.put("orange-red", "\u00A7c");
        colors.put("red-orange", "\u00A7c");
        colors.put("pink", "\u00A7d");
        colors.put("light red", "\u00A7d");
        colors.put("yellow", "\u00A7e");
        colors.put("white", "\u00A7f");
        
        if (colors.containsKey(col))
        {
        	color = colors.get(col);
        	return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Item) {
            return equals((Item) obj);
        } else if (obj instanceof String) {
            return equals((String) obj);
        } else if (obj instanceof ItemStack) {
            return equals((ItemStack) obj);
        }
        return false;
    }

    public boolean equals(ItemStack i) {
        if (i == null) {
            return false;
        }
        return itemId == i.getTypeId() && (IsTool() || itemData == i.getDurability());
    }
    /*
    public boolean equals(KitItem ki){
    return ki.equals(this);
    }*/

    // required for equals(Object obj)
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.itemId;
        hash = 79 * hash + this.itemData;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%s (%d:%d)", name, itemId, itemData);
    }

    public ItemStack toItemStack() {
        return new ItemStack(itemId, 1, (short) 0, itemData);
    }

    public ItemStack toItemStack(int amount) {
        return new ItemStack(itemId, amount, (short) 0, itemData);
    }

    // creatures are numbered starting at 4000
    public boolean isEntity() {
        return itemId >= 4000 && itemId < 5000;
    }

    // kits are numbered at 5000+
    public boolean isKit() {
        return itemId >= 5000;
    }

    public int getMaxStackSize() {
        return maxStack;
    }
} // end class Item

