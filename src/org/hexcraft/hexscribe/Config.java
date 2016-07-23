package org.hexcraft.hexscribe;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import com.google.gson.annotations.SerializedName;

public class Config {

	// -- we need weapon categories
	@SerializedName("usePermissions") public boolean usePermissions;
	@SerializedName("baseBlock") public Material baseBlock;
	
	@SerializedName("maxDurability") public int maxDurability;
	@SerializedName("minDurability") public int minDurability;
	@SerializedName("maxDurabilityMessage") public String maxDurabilityMessage;
	@SerializedName("minDurabilityMessage") public String minDurabilityMessage;
	
	@SerializedName("baseCost") public int baseCost;
	@SerializedName("costPerEnchantment") public int costPerEnchantment;
	
	@SerializedName("enchantmentCostPerLevel") public Map<String, Integer> enchantmentCostPerLevel;
	
	@SerializedName("allowedMats") public List<String> allowedMats;
	
}
