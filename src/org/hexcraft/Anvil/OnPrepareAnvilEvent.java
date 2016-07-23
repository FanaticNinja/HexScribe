package org.hexcraft.Anvil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.hexcraft.HexScribe;
import org.hexcraft.hexscribe.listeners.BaseListener;

public class OnPrepareAnvilEvent extends BaseListener {

	// -- just wow
    public Map<UUID, Block> anvilLocations = new HashMap<UUID, Block>();
	
	public OnPrepareAnvilEvent(HexScribe _plugin) {
		super(_plugin);
	}
	
	public boolean CheckStructure(Block block) {
    	
    	//plugin.log("Inside CheckStructure");
    	
    	boolean bGoldenAnvil = true;
    	for (int x = -1; x <= 1; x++) {
    		
    		//plugin.log("For X: " + x);
    		
    		for (int z = -1; z <= 1; z++) {
    			
    			//plugin.log("For Z: " + z);
				//Check block at (x, y, z)
    			//plugin.log("Block Location " + block);
    			
    			//Location locCheck = new Location(block.getWorld()
    					//, block.getLocation().getX() - x
    					//, block.getLocation().getY() - 1
    					//, block.getLocation().getZ() - z);
    			
    			if (!block.getLocation().add(x, -1, z).getBlock().getType().equals(plugin.config.baseBlock)) {
    				bGoldenAnvil = false;
    				//plugin.log("Block Check False");
    			}
    			else {
    				//plugin.log("Block Check True");
    			}
    			
			}
    	}
    	
    	return bGoldenAnvil;
    }
    @EventHandler
    public void InteractAnvil(PlayerInteractEvent event) {

    	// -- save location of anvil blocks...
    	if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ANVIL) {
    		
    		// -- check block structure
    		if (!CheckStructure(event.getClickedBlock())) {
        		//plugin.log("Not a Golden Anvil");
        		return;
        	}
    		else
    		{
    			// -- check permissions
    			// -- allow usage as a golden anvil if they have perms.
    			
    			if (plugin.config.usePermissions ==  true) {
    				if (event.getPlayer().hasPermission("hexscribe.use")) {
        				anvilLocations.put(event.getPlayer().getUniqueId(), event.getClickedBlock());
        			}
    				else {
    					// -- deny event, send player message
    					event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use a:" + ChatColor.GOLD + " Golden Anvil");
    					event.setCancelled(true);
    					event.setUseInteractedBlock(Result.DENY);
    				}
    			}
    			
    		}
    	}
    }
	
    private boolean isFilled(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }
	
	@EventHandler
    public void AnvilEvent(PrepareAnvilEvent event) {
		
		if (event.getInventory().getType() != InventoryType.ANVIL) {
			return;
		}
		
		if (event.getInventory().getItem(0) == null && event.getInventory().getItem(1) == null) {
			return;
		}
		
		if (event.getViewers().isEmpty()) {
			return;
		}
		
		if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            
            
            ItemStack first = event.getInventory().getItem(0);
            ItemStack second = event.getInventory().getItem(2);
            
            if (isFilled(first) && first.getType().equals(Material.BOOK_AND_QUILL) && isFilled(second) && second.hasItemMeta()) {
            	

            	for (String type : plugin.config.allowedMats) {
                	if (second.getType().name().contains(type)) {
                		
                		ItemStack resultStack = new ItemStack(Material.ENCHANTED_BOOK);
                        
                		float pct = (second.getType().getMaxDurability() - second.getDurability()) / second.getType().getMaxDurability();
                        if ((int) Math.floor(pct * 100) > plugin.config.maxDurability) {
                        	
                        	String msg = plugin.config.maxDurabilityMessage;
                            if (!msg.equals("")) {
                            	player.sendMessage(msg);
                            }
                            return;
                            
                            
                        } else if ((int) Math.floor(pct * 100) < plugin.config.minDurability) {
                        	
                            String msg = plugin.config.minDurabilityMessage;
                            if (!msg.equals("")) {
                            	player.sendMessage(msg);
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

                        EnchantmentStorageMeta meta = ((EnchantmentStorageMeta) resultStack.getItemMeta());
                        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                            meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                        }
                        
                        if (enchantments.isEmpty()) {
                        	event.setResult(null);
                        	//event.setResultSlot(null);
                            return;
                        }
                        
                        
                        
                        resultStack.setItemMeta(meta);
                        event.setResult(resultStack);
                        //event.getInventory().
                        //view.setRepairCost(cost);
                		
                        break;
                    }
            	}
            	
            	
            }
            
		}
		
		
		
	}
	
}
