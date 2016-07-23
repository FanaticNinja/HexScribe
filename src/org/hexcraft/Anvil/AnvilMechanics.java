package org.hexcraft.Anvil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.hexcraft.HexScribe;

import java.util.*;
import java.util.Map.Entry;

/**
 * Custom implementation of anvil mechanics to accommodate for custom enchantments
 */
public class AnvilMechanics {
    
	public static HexScribe plugin;

	
    public static void updateResult(AnvilView view, ItemStack[] input, String name, HexScribe plugin, Player player) {
        if (input.length < 2) {
            view.setResultSlot(null);
            return;
        }
        
        AnvilMechanics.plugin = plugin;

        ItemStack first = input[0];
        ItemStack second = input[1];
        //ItemStack result = input[1];
        
        
        // -- ONLY modify anvil mechanics if we are doing this...
        //if (isFilled(first) && first.getType().equals(Material.BOOK_AND_QUILL) && isFilled(second) && result == null) {
        if (isFilled(first) && first.getType().equals(Material.BOOK_AND_QUILL) && isFilled(second) && second.hasItemMeta()) {
        	

        	for (String type : plugin.config.allowedMats) {
            	if (second.getType().name().contains(type)) {
            		
            		ItemStack resultStack = new ItemStack(Material.ENCHANTED_BOOK);
                    
            		float pct = (second.getType().getMaxDurability() - second.getDurability()) / second.getType().getMaxDurability();
                    if ((int) Math.floor(pct * 100) > plugin.config.maxDurability) {
                    	
                    	String msg = plugin.config.maxDurabilityMessage;
                        if (!msg.equals("")) {
                            view.getPlayer().sendMessage(msg);
                        }
                        return;
                        
                        
                    } else if ((int) Math.floor(pct * 100) < plugin.config.minDurability) {
                    	
                        String msg = plugin.config.minDurabilityMessage;
                        if (!msg.equals("")) {
                        	view.getPlayer().sendMessage(msg);
                        }
                        return;
                        
                    }
            		
                    //ScribeResult scribeResult = new ScribeResult(second);
                    int cost = -1;
                    Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
                    
                    cost = plugin.config.baseCost;
                    enchantments.clear();

                    List<Entry<Enchantment, Integer>> entrySet = new ArrayList<Entry<Enchantment, Integer>>(second.getEnchantments().entrySet());
                    //Collections.shuffle(entrySet);
                    for (Entry<Enchantment, Integer> e: entrySet) {
                        int cap = -1;
                        int lvl = e.getValue();
                        if (cap != 0) {
                            if (cap == -1) { cap = lvl; }
                            if (lvl > cap) { lvl = cap; }
                            
                            int cpl = plugin.config.enchantmentCostPerLevel.get(e.getKey().getName());
                            int tmpCost = plugin.config.costPerEnchantment + (cpl * lvl);
                            
                            
                            if (plugin.config.usePermissions ==  true) {
    							if (!player.hasPermission("hexscribe.enchant." + e.getKey().getName())) {
    								player.sendMessage(ChatColor.RED + "You do not have permission for: " + e.getKey().getName());
    							}
    							else {
    								if (cost + tmpCost < 40) {
    	                                cost += tmpCost;
    	                                enchantments.put(e.getKey(), lvl);
    	                            }
    							}
    						}
                            else {
                            	if (cost + tmpCost < 40) {
                                    cost += tmpCost;
                                    enchantments.put(e.getKey(), lvl);
                                }
                            }
                            
                            
                        }
                    }
                    /*
                    for (Entry<Enchantment, Integer> e: entrySet) {
                        int cap = 30;
                        int lvl = e.getValue();
                        if (cap != 0) {
                            if (cap == -1) { cap = lvl; }
                            if (lvl > cap) { lvl = cap; }
                            int cpl = 1;
                            int tmpCost = 1 + (cpl * lvl);
                            if (cost + tmpCost < 40) {
                                cost += tmpCost;
                                enchantments.put(e.getKey(), lvl);
                            }
                        }
                    }
                    */

                    EnchantmentStorageMeta meta = ((EnchantmentStorageMeta) resultStack.getItemMeta());
                    for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                        meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                    }
                    
                    if (enchantments.isEmpty()) {
                    	view.setResultSlot(null);
                        return;
                    }
                    
                    resultStack.setItemMeta(meta);
                    view.setResultSlot(resultStack);
                    view.setRepairCost(cost);
            		
                    break;
                }
        	}
        	
        	
        }

        

        // Result if one is missing and the text differs
        /*
        if (isFilled(first) && !isFilled(second) && name != null) {
            ItemStack result = first.clone();
            ItemMeta meta = result.getItemMeta() == null ? Bukkit.getItemFactory().getItemMeta(result.getType()) : result.getItemMeta();
            meta.setDisplayName(name);
            result.setItemMeta(meta);
            view.setResultSlot(result);
            int cost = getBaseCost(first, false) + (first.getType().getMaxDurability() != 0 ? 7 : 5 * first.getAmount());
            view.setRepairCost(cost > 39 ? 39 : cost);
        }
        else if (!isFilled(first) && isFilled(second) && name != null) {
            ItemStack result = second.clone();
            ItemMeta meta = result.getItemMeta() == null ? Bukkit.getItemFactory().getItemMeta(result.getType()) : result.getItemMeta();
            meta.setDisplayName(name);
            result.setItemMeta(meta);
            view.setResultSlot(result);
            int cost = getBaseCost(first, false) + (first.getType().getMaxDurability() != 0 ? 7 : 5 * first.getAmount());
            view.setRepairCost(cost > 39 ? 39 : cost);
        }
        

        // No result if one is missing
        else if (!isFilled(first) || !isFilled(second))
            view.setResultSlot(null);


        // If there's more than one item, don't allow it to be used
        else if (first.getAmount() > 1)
            view.setResultSlot(null);
        else if (second.getAmount() > 1)
            view.setResultSlot(null);

        // If they are missing durability, make the output fix them
        else if (first.getDurability() > 0) {
            view.setResultSlot(makeItem(first, second));
            view.setRepairCost(getCombineCost(first, second));
        }

        // No result needed
        else view.setResultSlot(null);
        */
    }

    /**
     * Updates the result slot of the anvil when the inputs are changed
     *
     * @param view  anvil inventory
     * @param input input items
     */
    public static void updateResult(AnvilView view, ItemStack[] input, HexScribe plugin, Player player) {
        updateResult(view, input, null, plugin, player);
    }

    /**
     * Gets the base cost of an item
     *
     * @param item     item
     * @param withBook if using a book with the item
     * @return         base cost
     */
    private static int getBaseCost(ItemStack item, boolean withBook) {

        int cost = 0;
        int count = 0;

        // Add in the number of enchants cost and return the result
        return cost + (int)((count + 1) * (count / 2.0) + 0.5);
    }

    /**
     * Gets the cost of repairing an item with materials
     *
     * @param item     repaired item
     * @param material material used
     * @return         repair cost
     */
    @SuppressWarnings("unused")
	private static int getMaterialRepairCost(ItemStack item, ItemStack material) {

        int cost;

        // Diamonds cost 3 times as much as other materials
        int m = material.getType() == Material.DIAMOND ? 3 : 1;

        // Calculate the cost depending on how damaged the item is
        cost = 4 * m * item.getDurability() / item.getType().getMaxDurability() + 1;
        if (cost > m * material.getAmount())
            cost = m * material.getAmount();

        return cost;
    }

    /**
     * Gets the cost of combining to items
     *
     * @param first  primary item
     * @param second secondary item
     * @return       combine cost
     */
    @SuppressWarnings("unused")
	private static int getCombineCost(ItemStack first, ItemStack second) {

        // Books halve the cost of several factors
        boolean book = isBook(second);
        double m = book ? 0.5 : 1;

        // The 'extra' costs include the base cost of the primary item
        int cost = 0;
        int extra = getBaseCost(first, book);

        // Combining repair cost
        if (first.getDurability() > 0 && first.getType() == second.getType()) {
            int extraCost = (int)((durability(second) + 0.12 * first.getType().getMaxDurability()) / 100);
            cost += extraCost > 0 ? extraCost : 1;
        }

        // Return the total cost, halving the extra cost if a book was used
        return cost + (int)(m * extra);
    }

    

    /**
     * Creates a result item from a component and a material
     *
     * @param item   item to repair
     * @param amount amount of materials
     * @return       result item
     */
    static ItemStack makeItem(ItemStack item, int amount) {
        ItemStack newItem = item.clone();

        // Repair the item
        /*
        if (item.getDurability() - item.getType().getMaxDurability() * 0.25 * amount < 0)
            newItem.setDurability((short)0);
        else newItem.setDurability((short)(item.getDurability() - item.getType().getMaxDurability() * amount * 0.25));
        */
        // Return the item
        return newItem;
    }

    /**
     * Makes a result item from two components
     *
     * @param primary   target item
     * @param secondary supplement item
     * @return          resulting item
     */
    static ItemStack makeItem(ItemStack primary, ItemStack secondary) {

        // Take the type of the primary item
        ItemStack item = new ItemStack(primary.getType());
        if (primary.hasItemMeta()) item.setItemMeta(primary.getItemMeta());

        // Set the durability if applicable and add the corresponding cost
        if (primary.getDurability() > 0 && primary.getType() == secondary.getType()) {
            if (durability(item) + durability(secondary) < 0.88 * primary.getType().getMaxDurability())
                setDurability(item, (short)(durability(primary) + durability(secondary) + 0.12 * primary.getType().getMaxDurability()));
        }
        else item.setDurability(primary.getDurability());

        // Return the item
        return item;
    }

    /**
     * Checks if the item is not an empty slot
     *
     * @param item item to check
     * @return     true if not an empty slot, false otherwise
     */
    static boolean isFilled(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    /**
     * Checks if an item is a book
     *
     * @param item item to check
     * @return     true if book, false otherwise
     */
    static boolean isBook(ItemStack item) {
        return item.getType() == Material.BOOK || item.getType() == Material.ENCHANTED_BOOK;
    }

    /**
     * Gets the durability of an item
     *
     * @param item item
     * @return     durability
     */
    static short durability(ItemStack item) {
        return (short)(item.getType().getMaxDurability() - item.getDurability());
    }

    /**
     * Sets the durability of an item
     *
     * @param item  item to set the durability of
     * @param value durability remaining
     * @return      the item
     */
    static ItemStack setDurability(ItemStack item, short value) {
        item.setDurability((short)(item.getType().getMaxDurability() - value));
        return item;
    }
}
