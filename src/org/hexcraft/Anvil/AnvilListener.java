package org.hexcraft.Anvil;

import org.hexcraft.HexScribe;
import org.hexcraft.Anvil.EUpdateTask;
import org.hexcraft.hexscribe.compat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public class AnvilListener implements Listener {

    private final HexScribe plugin;

    private final Hashtable<String, AnvilTask> tasks = new Hashtable<String, AnvilTask>();

    private boolean custom = false;
    
    // -- just wow
    public Map<UUID, Block> anvilLocations = new HashMap<UUID, Block>();

    public AnvilListener(HexScribe plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
    			else
    			{
    				anvilLocations.put(event.getPlayer().getUniqueId(), event.getClickedBlock());
    			}
    			
    		}
    	}
    }


    /**
     * Opens the custom inventory instead of the default anvil inventory
     *
     * @param event event details
     */
    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL) {

        	// -- check to see if its a GOLDEN_ANVIL!! 
        	// -- our custom type
        	if (anvilLocations.get(event.getPlayer().getUniqueId()) != null )
    		{

        		//plugin.log("Golden Anvil Check");
            	if (!CheckStructure(anvilLocations.get(event.getPlayer().getUniqueId()))) {
            		plugin.log("Not a Golden Anvil");
            		return;
            	}
            	

            	
                Player player = plugin.getServer().getPlayer(event.getPlayer().getName());

                
                AnvilView anvil = null;
                String version = null;
                
                try {

                    version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];

                } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
                    //
                }
                
                if (version.equals("v1_10_R1")) {
                	anvil = new MainAnvil_v1_10_R1(plugin, event.getInventory(), player);
                	
                } else if (version.equals("v1_9_R2")) {
                	anvil = new MainAnvil_v1_9_R2(plugin, event.getInventory(), player);
                	
	    		} else if (version.equals("v1_9_R1")) {
	    			anvil = new MainAnvil_v1_9_R1(plugin, event.getInventory(), player);
	    			
		        } else if (version.equals("v1_8_R3")) {
		        	anvil = new MainAnvil_v1_8_R3(plugin, event.getInventory(), player);
		        	
			    } else if (version.equals("v1_8_R2")) {
			    	anvil = new MainAnvil_v1_8_R2(plugin, event.getInventory(), player);
			    	
	    		} else if (version.equals("v1_8_R1")) {
	    			anvil = new MainAnvil_v1_8_R1(plugin, event.getInventory(), player);
	    			
	    		} else if (version.equals("v1_7_R4")) {
	    			anvil = new MainAnvil_v1_7_R4(plugin, event.getInventory(), player);
	    			
			    }
                // -- might work...
	    		else {
                    event.setCancelled(true);
                    anvil = new CustomAnvil(plugin, player);
                    custom = true;
                }
                
                /*
                int id = 0;
                try {
                    String v = plugin.getServer().getVersion();
                    plugin.log("getVersion" + v);
                    int ind = v.indexOf("MC: 1.8.") + 8;
                    plugin.log("indexOf: " + ind);
                    id = Integer.parseInt(v.substring(ind, ind + 1));
                    plugin.log("ID: " + id);
                }
                catch (Exception ex) { }
                
                
                
                if (id > 4) {
                    anvil = new MainAnvil(plugin, event.getInventory(), player);
                }
                else if (id > 0) {
                    anvil = new MainAnvil(plugin, event.getInventory(), player);
                }
                else if (plugin.getServer().getVersion().contains("MC: 1.8")) {
                    anvil = new MainAnvil(plugin, event.getInventory(), player);
                }
                else {
                    event.setCancelled(true);
                    anvil = new CustomAnvil(plugin, player);
                    custom = true;
                }
                */
                
                tasks.put(player.getName(), new AnvilTask(plugin, anvil, player));
    		}
            
        }
    }

    /**
     * Gives back any items when the inventory is closed
     *
     * @param event event details
     */
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (tasks.containsKey(event.getPlayer().getName())) {
            tasks.get(event.getPlayer().getName()).getView().close();
            tasks.get(event.getPlayer().getName()).cancel();
            tasks.remove(event.getPlayer().getName());
            
            // -- clean up anvil inventories
            anvilLocations.remove(event.getPlayer().getUniqueId());
        }
    }

    /**
     * Handles anvil transactions
     *
     * @param event event details
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = plugin.getServer().getPlayer(event.getWhoClicked().getName());

        // Make sure the inventory is the custom inventory
        if (tasks.containsKey(player.getName()) && custom) {
            if (tasks.get(player.getName()).getView().getInventory().getName().equals(event.getInventory().getName())) {
            	
            	
                AnvilView view = tasks.get(player.getName()).getView();
                ItemStack[] inputs = view.getInputSlots();
                boolean top = event.getRawSlot() < view.getInventory().getSize();
                if (event.getSlot() == -999) return;

                if (event.isShiftClick()) {

                    if (event.getRawSlot() == view.getResultSlotID() && isFilled(view.getResultSlot())) {
                        if (player.getGameMode() != GameMode.CREATIVE && (view.getRepairCost() > player.getLevel() || view.getRepairCost() >= 40)) {
                            event.setCancelled(true);
                        }
                        else {
                            view.clearInputs();
                            if (player.getGameMode() != GameMode.CREATIVE) {
                                player.setLevel(player.getLevel() - view.getRepairCost());
                                // -- give player second item back if needed
                                
                                if (view.getSecondItem() != null) {
                                	// -- check inventory space
                                	if (view.getPlayer().getInventory().firstEmpty() != -1) {
                                		
                                		// -- space available
                                		player.getInventory().addItem(view.getSecondItem());
                                		player.updateInventory();
                                		
                                	}
                                }
                                
                                
                            }
                        }
                    }

                    // Don't allow clicking in other slots in the anvil
                    else if (top && !view.isInputSlot(event.getSlot())) {
                        event.setCancelled(true);
                    }

                    // Don't allow shift clicking into the product slot
                    else if (!top && areFilled(inputs[0], inputs[1])) {
                        event.setCancelled(true);
                    }
                }
                else if (event.isLeftClick()) {

                    // Same as shift-clicking out the product
                    if (event.getRawSlot() == view.getResultSlotID() && !isFilled(event.getCursor()) && isFilled(view.getResultSlot())) {
                        if (player.getGameMode() != GameMode.CREATIVE && (view.getRepairCost() > player.getLevel() || view.getRepairCost() >= 40)) {
                            event.setCancelled(true);
                        }
                        else {
                            view.clearInputs();
                            if (player.getGameMode() != GameMode.CREATIVE)
                                player.setLevel(player.getLevel() - view.getRepairCost());
                        }
                    }

                    // Don't allow clicks in other slots of the anvil
                    else if (top && !view.isInputSlot(event.getSlot())) {
                        event.setCancelled(true);
                    }
                }
                else if (event.isRightClick()) {
                    if (top) event.setCancelled(true);
                }

                // Update the inventory manually after the click has happened
                new EUpdateTask(plugin, player);
            }
        }
        /*
        else {
        	if (event.getInventory() instanceof AnvilInventory) {
        		AnvilInventory ai = (AnvilInventory) event.getInventory();
                
                ItemStack first = ai.getItem(0);
                ItemStack second = ai.getItem(1);
                ItemStack result = ai.getItem(2);
                
                // -- ONLY modify anvil mechanics if we are doing this...
                if (first != null && first.getType().equals(Material.BOOK_AND_QUILL) && second != null && result == null) {
                	// Scribe mechanics
                    if (isFilled(first) && first.getType().equals(Material.BOOK_AND_QUILL) && second != null) {
                    	SetAnvilView(event);
                    }
                }
        	}
        }
        */
    }

    private boolean isFilled(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }

    private boolean areFilled(ItemStack item1, ItemStack item2) {
        return isFilled(item1) && isFilled(item2);
    }
}
