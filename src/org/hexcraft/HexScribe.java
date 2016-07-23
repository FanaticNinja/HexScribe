package org.hexcraft;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.hexcraft.Anvil.AnvilListener;
import org.hexcraft.hexscribe.Config;
import org.hexcraft.util.DiscUtil;

//import de.slikey.effectlib.EffectManager;

public class HexScribe extends HexCore {

	public String dataLayerFolderPath = "plugins" + File.separator + "HexScribe";
    public String configFilePath = dataLayerFolderPath + File.separator + "config.json";
    
    // -- per player settings
    // -- 10 talent slots
    // -- 150 points default
    public Config config;
    // -- config per type of attribute...
   
    //public EffectManager effectManager;

    /*
 	@Override 
 	public GsonBuilder getGsonBuilder() 
 	{ 
 		return this.getGsonBuilderWithoutPreprocessors(); 
 	}
 	*/
    
	@Override
	public void onEnable()
	{
		if ( ! preEnable()) return;
		
		// -- load from json config
		String content = DiscUtil.readCatch(new File(configFilePath));
		
		// -- check for defaults
		if(content == null)
		{
			config = new Config();
			
			config.baseBlock = Material.GOLD_BLOCK;
			config.usePermissions = false;
			
			config.minDurability = 0;
			config.maxDurability = 100;
			
			config.minDurabilityMessage = "Item is too damaged.";
			config.maxDurabilityMessage = "Item is not damaged enough.";
			
			config.baseCost = 1;
			config.costPerEnchantment = 1;
			
			config.enchantmentCostPerLevel = new HashMap<String, Integer>();
			
			config.enchantmentCostPerLevel.put("ARROW_DAMAGE", 1);
			config.enchantmentCostPerLevel.put("ARROW_FIRE", 1);
			config.enchantmentCostPerLevel.put("ARROW_INFINITE", 1);
			config.enchantmentCostPerLevel.put("ARROW_KNOCKBACK", 1);
			config.enchantmentCostPerLevel.put("DAMAGE_ALL", 1);
			config.enchantmentCostPerLevel.put("DAMAGE_ARTHROPODS", 1);
			config.enchantmentCostPerLevel.put("DAMAGE_UNDEAD", 1);
			config.enchantmentCostPerLevel.put("DEPTH_STRIDER", 1);
			config.enchantmentCostPerLevel.put("DIG_SPEED", 1);
			config.enchantmentCostPerLevel.put("DURABILITY", 1);
			config.enchantmentCostPerLevel.put("FROST_WALKER", 1);
			config.enchantmentCostPerLevel.put("FIRE_ASPECT", 1);
			config.enchantmentCostPerLevel.put("KNOCKBACK", 1);
			config.enchantmentCostPerLevel.put("LOOT_BONUS_BLOCKS", 1);
			config.enchantmentCostPerLevel.put("LOOT_BONUS_MOBS", 1);
			config.enchantmentCostPerLevel.put("LUCK", 1);
			config.enchantmentCostPerLevel.put("LURE", 1);
			config.enchantmentCostPerLevel.put("MENDING", 1);
			config.enchantmentCostPerLevel.put("OXYGEN", 1);
			config.enchantmentCostPerLevel.put("PROTECTION_ENVIRONMENTAL", 1);
			config.enchantmentCostPerLevel.put("PROTECTION_EXPLOSIONS", 1);
			config.enchantmentCostPerLevel.put("PROTECTION_FALL", 1);
			config.enchantmentCostPerLevel.put("PROTECTION_FIRE", 1);
			config.enchantmentCostPerLevel.put("PROTECTION_PROJECTILE", 1);
			config.enchantmentCostPerLevel.put("SILK_TOUCH", 1);
			config.enchantmentCostPerLevel.put("THORNS", 1);
			config.enchantmentCostPerLevel.put("WATER_WORKER", 1);
			
			//Enchantment.FROST_WALKER
			//Enchantment.MENDING
			
			
			// -- Scribables
			config.allowedMats = new ArrayList<String>();
			config.allowedMats.add("WOOD_");
			config.allowedMats.add("STONE_");
			config.allowedMats.add("CHAINMAIL_");
			config.allowedMats.add("IRON_");
			config.allowedMats.add("GOLD_");
			config.allowedMats.add("DIAMOND_");
			config.allowedMats.add("LEATHER_");
			config.allowedMats.add("BOW");
			config.allowedMats.add("FISHING_");
			

			// -- Save
			String save = gson.toJson(config);
			DiscUtil.writeCatch(new File(configFilePath), save);
		}
		else
		{
			// -- create config from json
			config = gson.fromJson(content, Config.class);
		}
		
		// -- commands
		//this.getCommand("attribute").setExecutor(new Commands(this));

		// -- after attribute load, create dynamic perms!
		
		for (Enchantment e : Enchantment.values()) {
			
			Permission perm = new Permission("hexscribe.enchant." + e.getName());
			perm.addParent("hexscribe.enchant.*", true);
			this.getServer().getPluginManager().addPermission(perm);
			
			//this.log("Enchant: " +  e.getName());
			
		}
		
		
		// Initialize a new EffectManager
       // effectManager = new EffectManager(this);
		
		// -- lets start the Listeners!!
		//new ScribeListener(this);
		new AnvilListener(this);
		//new OnPrepareAnvilEvent(this);
		
		postEnable();
	}
	
	public ItemStack CreateItem(Material mat, short damage, String name, List<String> lore, int ammount)
    {
    	Material Credit = mat;
    	ItemStack HexCreditStack = new ItemStack(Credit, ammount, damage);
    	ItemMeta HexCreditMeta = HexCreditStack.getItemMeta();
    	
    	HexCreditMeta.setDisplayName(name);
    	HexCreditMeta.setLore(lore);
    	
    	HexCreditStack.setItemMeta(HexCreditMeta);
    	
    	return HexCreditStack;
    }
	
}
